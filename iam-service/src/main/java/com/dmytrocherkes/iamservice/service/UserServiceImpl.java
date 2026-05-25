package com.dmytrocherkes.iamservice.service;

import com.dmytrocherkes.iamservice.mapper.UserMapper;
import com.dmytrocherkes.iamservice.model.constants.ApiErrorMessage;
import com.dmytrocherkes.iamservice.model.dto.user.UserDTO;
import com.dmytrocherkes.iamservice.model.dto.user.UserSearchDTO;
import com.dmytrocherkes.iamservice.model.entity.Role;
import com.dmytrocherkes.iamservice.model.entity.User;
import com.dmytrocherkes.iamservice.model.enums.AwsMessageTypes;
import com.dmytrocherkes.iamservice.model.exception.DataExistException;
import com.dmytrocherkes.iamservice.model.exception.NotFoundException;
import com.dmytrocherkes.iamservice.model.request.NewUserRequest;
import com.dmytrocherkes.iamservice.model.request.UpdateUserRequest;
import com.dmytrocherkes.iamservice.model.request.UserSearchRequest;
import com.dmytrocherkes.iamservice.model.response.PaginationResponse;
import com.dmytrocherkes.iamservice.repository.RoleRepository;
import com.dmytrocherkes.iamservice.repository.UserRepository;
import com.dmytrocherkes.iamservice.repository.UserSearchCriteria;
import com.dmytrocherkes.iamservice.security.validation.AccessValidator;
import com.dmytrocherkes.iamservice.service.model.IamServiceUserRole;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AccessValidator accessValidator;
    private final SnsPublisher snsPublisher;

    public UserDTO getById(@NonNull Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        return userMapper.toDto(user);
    }

    public UserDTO getByUsername(@NonNull String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USERNAME_NOT_FOUND.getMessage(username)));

        return userMapper.toDto(user);
    }


    public UserDTO createUser(@NonNull NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail()))
            throw new DataExistException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(newUserRequest.getEmail()));

        if (userRepository.existsByUsername(newUserRequest.getUsername()))
            throw new DataExistException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(newUserRequest.getUsername()));

        // prepare user role
        Role userRole = roleRepository.findByName(IamServiceUserRole.USER.getRole())
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_ROLE_NOT_FOUND.getMessage()));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = userMapper.createUser(newUserRequest);
        user.setPassword(passwordEncoder.encode(newUserRequest.getPassword()));
        user.setRoles(roles);

        User persistedUser = userRepository.save(user);
        snsPublisher.publishUserChangesEventToSns(persistedUser, AwsMessageTypes.USER_UPSERT);

        return userMapper.toDto(persistedUser);
    }

    @Transactional
    public UserDTO updateUser(Integer userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        userRepository.findByUsername(request.getNickname())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new DataExistException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getNickname()));
                    }
                });

        accessValidator.validateAdminOrOwnerAccess(userId);

        userMapper.updateUser(user, request);
        user.setUpdatedAt(LocalDateTime.now());

        User persistedUser = userRepository.save(user);
        snsPublisher.publishUserChangesEventToSns(persistedUser, AwsMessageTypes.USER_UPSERT);

        return userMapper.toDto(persistedUser);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        accessValidator.validateAdminOrOwnerAccess(userId);

        userRepository.delete(user);
        snsPublisher.publishUserChangesEventToSns(user, AwsMessageTypes.USER_DELETE);
    }

    public PaginationResponse<UserSearchDTO> findAllUsers(Pageable pageable) {
        Page<UserSearchDTO> users = userRepository.findAll(pageable)
                .map(userMapper::toUserSearchDTO);

        // Security validation. Only admin can read all users
        accessValidator.validateAdminAccess();

        return new PaginationResponse<>(
                users.getContent(),
                new PaginationResponse.Pagination(
                        users.getTotalElements(),
                        pageable.getPageSize(),
                        users.getNumber() + 1,
                        users.getTotalPages()
                )
        );
    }

    public PaginationResponse<UserSearchDTO> searchUsers(UserSearchRequest request, Pageable pageable) {
        Specification<User> specification = new UserSearchCriteria(request);

        // Security validation. Only admin can read all users
        accessValidator.validateAdminAccess();

        Page<UserSearchDTO> usersPage = userRepository.findAll(specification, pageable)
                .map(userMapper::toUserSearchDTO);

        return new PaginationResponse<>(
                usersPage.getContent(),
                new PaginationResponse.Pagination(
                        usersPage.getTotalElements(),
                        pageable.getPageSize(),
                        usersPage.getNumber() + 1,
                        usersPage.getTotalPages()
                )
        );
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getUserDetails(email, userRepository);
    }

    static UserDetails getUserDetails(String email, UserRepository userRepository) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.EMAIL_NOT_FOUND.getMessage(email)));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }
}
