package com.dennis.user.service.dto;

import com.dennis.user.service.model.enums.Status;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProfileUpdateDTO {
    private String email;
    private Status status;
}