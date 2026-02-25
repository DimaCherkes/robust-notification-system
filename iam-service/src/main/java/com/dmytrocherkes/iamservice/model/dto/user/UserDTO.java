package com.dmytrocherkes.iamservice.model.dto.user;

import com.dmytrocherkes.iamservice.model.dto.role.RoleDTO;
import com.dmytrocherkes.iamservice.model.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private Integer id;
    private String username;
    private String email;
    private LocalDateTime created;
    private LocalDateTime lastLogin;

    private RegistrationStatus registrationStatus;
    private List<RoleDTO> roles;

}