package com.amponsem.authentication_service.services;

import com.amponsem.authentication_service.config.RabbitMQConfig;
import com.amponsem.authentication_service.exception.InvalidOTPException;
import com.amponsem.authentication_service.exception.UserNotFoundException;
import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.model.Email;
import com.amponsem.authentication_service.repository.AuthRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.util.Random;

@Service
public class OtpService {

   private final RabbitTemplate rabbitTemplate;

   private final JwtService jwtService;

    private final AuthRepository authRepository;

    private final Random random = new Random();

    public OtpService(RabbitTemplate rabbitTemplate, AuthRepository authRepository, JwtService jwtService) {
        this.rabbitTemplate = rabbitTemplate;
        this.authRepository = authRepository;
        this.jwtService = jwtService;
    }

    private String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }

    public String sendOtp(String email)  {
        String otp = generateOtp();
        String message = String.format("Your OTP code is: %s%n%nThis code is " +
                "valid for the next 10 minutes only.", otp);
        AuthUser authUser = authRepository.findByEmail(email).orElseThrow(
                ()-> new UserNotFoundException("User with email " + email + " not found"));

        Email emailMessage = new Email(authUser.getEmail(),
                "Confirmation OTP", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.OTP_EMAIL_QUEUE, emailMessage);

        authUser.setOtp(otp);
        authRepository.save(authUser);

        return otp;
    }

    public String confirmOtp(String otp) {
        AuthUser authUser = authRepository.findByOtp(otp).orElseThrow(
                ()-> new InvalidOTPException("invalid OTP"));
        authUser.setOtp(null);
        authRepository.save(authUser);
        return jwtService.generateToken(authUser.getEmail(), null);
    }
}
