package com.dmytrocherkes.iamservice.mapper;

import com.dmytrocherkes.iamservice.model.dto.role.RoleDTO;
import com.dmytrocherkes.iamservice.model.dto.user.UserDTO;
import com.dmytrocherkes.iamservice.model.dto.user.UserProfileDTO;
import com.dmytrocherkes.iamservice.model.dto.user.UserSearchDTO;
import com.dmytrocherkes.iamservice.model.entity.Role;
import com.dmytrocherkes.iamservice.model.entity.User;
import com.dmytrocherkes.iamservice.model.enums.RegistrationStatus;
import com.dmytrocherkes.iamservice.model.request.NewUserRequest;
import com.dmytrocherkes.iamservice.model.request.RegistrationUserRequest;
import com.dmytrocherkes.iamservice.model.request.UpdateUserRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {DateTimeUtils.class, RegistrationStatus.class, Object.class}
)
public interface UserMapper {

    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.ACTIVE)")
    User createUser(NewUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUserRequest request);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserSearchDTO toUserSearchDTO(User user);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    UserProfileDTO toUserProfileDTO(User user, String token, String refreshToken);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.ACTIVE)")
    User fromDto(RegistrationUserRequest request);

    default List<RoleDTO> mapRoles(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .toList();
    }

}
