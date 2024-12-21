package com.amponsem.authentication_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;

    @Builder.Default
    private boolean isFirstTime = false;

}