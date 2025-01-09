package com.dennis.Email_Service.service;

import com.dennis.Email_Service.model.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendGenericMail(Email email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getTo());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        mailSender.send(message);
    }

    public void sendTemplateMail(Email email, Map<String, Object> templateVariables) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());

        Context context = new Context();
        context.setVariables(templateVariables);

        String htmlContent = templateEngine.process("email-template", context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}