package com.dennis.user.service.dto;

import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import lombok.Data;

@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Status status;
}
