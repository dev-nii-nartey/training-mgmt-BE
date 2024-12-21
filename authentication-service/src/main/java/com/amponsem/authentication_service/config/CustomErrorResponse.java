package com.amponsem.authentication_service.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomErrorResponse {
    private String message;

    public CustomErrorResponse(String message) {
        this.message = message;
    }

}

