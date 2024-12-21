package com.dennis.user.service.dto;

import com.dennis.user.service.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserDTO {
    private String email;
    private String password;
    private boolean isFirstTime;
    private Role role;
}