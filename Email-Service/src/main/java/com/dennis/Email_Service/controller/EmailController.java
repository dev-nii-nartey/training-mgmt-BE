package com.dennis.Email_Service.controller;

import com.dennis.Email_Service.model.Email;
import com.dennis.Email_Service.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RestControllerAdvice
@RequestMapping(path = "/api/v1/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public void sendEmail(@RequestBody Email email) {
        emailService.sendGenericMail(email);
    }

    @GetMapping("/test")
    public String returnString() {
        return  "Hello, this is a test response! for services";
    }
}
