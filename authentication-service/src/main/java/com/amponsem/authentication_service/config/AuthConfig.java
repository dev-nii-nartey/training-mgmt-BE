package com.amponsem.authentication_service.config;

import com.amponsem.authentication_service.filter.JwtAuthenticationFilter;
import com.amponsem.authentication_service.model.Role;
import com.amponsem.authentication_service.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthConfig {


    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public void SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/v1/auth/login").permitAll()
                        .requestMatchers("api/v1/auth/test/unprotected/endpoint").permitAll()
                        .requestMatchers("api/v1/auth/send-otp").permitAll()
                        .requestMatchers("api/v1/auth/test/unprotected/endpoint").permitAll()
                        .requestMatchers("api/v1/auth/verify-otp").permitAll()
                        .requestMatchers("api/v1/auth/register").permitAll()
                        .requestMatchers("api/v1/auth/reset-password").permitAll()
                        .requestMatchers("/admin/add").permitAll()
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/trainer/**").hasRole(Role.TRAINER.name())
                        .requestMatchers("/trainee/**").hasRole(Role.TRAINEE.name())
                        .anyRequest().authenticated()
                )
                 .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(12);
    }
}
