package com.amponsem.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email implements Serializable {
    private String to;
    private String subject;
    private String body;
}
