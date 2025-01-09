package com.dennis.Email_Service.service;

import com.dennis.Email_Service.config.RabbitConfig;
import com.dennis.Email_Service.model.Email;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmailListener {

    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(EmailListener.class);

    public EmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void receiveEmail(Email email) {

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", email.getSubject());
            variables.put("email", email.getTo());
            variables.put("password", email.getBody());
            emailService.sendTemplateMail(email, variables);
        } catch (MessagingException e) {
            log.error("failed to send mail");
        }
    }

    @RabbitListener(queues = RabbitConfig.OTP_EMAIL_QUEUE)
    public void receiveOTPEmail(Email email) {
        try {
            emailService.sendGenericMail(email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", email.getTo(), e);
        }
    }

}
