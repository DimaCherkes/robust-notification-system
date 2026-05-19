package com.dmytrocherkes.iamservice.service;

import com.dmytrocherkes.iamservice.mapper.UserMapper;
import com.dmytrocherkes.iamservice.model.constants.ApiErrorMessage;
import com.dmytrocherkes.iamservice.model.dto.user.UserProfileDTO;
import com.dmytrocherkes.iamservice.model.entity.RefreshToken;
import com.dmytrocherkes.iamservice.model.entity.Role;
import com.dmytrocherkes.iamservice.model.entity.User;
import com.dmytrocherkes.iamservice.model.enums.AwsMessageTypes;
import com.dmytrocherkes.iamservice.model.exception.InvalidDataException;
import com.dmytrocherkes.iamservice.model.exception.NotFoundException;
import com.dmytrocherkes.iamservice.model.request.LoginRequest;
import com.dmytrocherkes.iamservice.model.request.RegistrationUserRequest;
import com.dmytrocherkes.iamservice.repository.RoleRepository;
import com.dmytrocherkes.iamservice.repository.UserRepository;
import com.dmytrocherkes.iamservice.security.JwtTokenProvider;
import com.dmytrocherkes.iamservice.security.validation.AccessValidator;
import com.dmytrocherkes.iamservice.service.model.IamServiceUserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessValidator accessValidator;
    private final SnsPublisher snsPublisher;

    public UserProfileDTO login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidDataException(ApiErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
        }

        User user = userRepository.findUserByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new InvalidDataException(ApiErrorMessage.INVALID_USER_OR_PASSWORD.getMessage()));

        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);
        String token = jwtTokenProvider.generateToken(user);
        UserProfileDTO userProfileDTO = userMapper.toUserProfileDTO(user, token, refreshToken.getToken());
        userProfileDTO.setToken(token);

        return userProfileDTO;
    }

    public UserProfileDTO refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.validateAndRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();
        String accessToken = jwtTokenProvider.generateToken(user);

        return userMapper.toUserProfileDTO(user, accessToken, refreshToken.getToken());
    }

    public UserProfileDTO registerUser(@NotNull RegistrationUserRequest request) {
        accessValidator.validateNewUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getConfirmPassword()
        );

        Role userRole = roleRepository.findByName(IamServiceUserRole.USER.getRole())
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_ROLE_NOT_FOUND.getMessage()));

        User newUser = userMapper.fromDto(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);
        User persistedUser = userRepository.save(newUser);
        snsPublisher.publishUserChangesEventToSns(persistedUser, AwsMessageTypes.USER_UPSERT);

        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(newUser);
        String token = jwtTokenProvider.generateToken(newUser);
        UserProfileDTO userProfileDTO = userMapper.toUserProfileDTO(newUser, token, refreshToken.getToken());
        userProfileDTO.setToken(token);

        return userProfileDTO;
    }

}
