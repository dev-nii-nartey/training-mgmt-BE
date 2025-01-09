package com.dennis.assessment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/assessments/**").hasRole("TRAINER")
                        .requestMatchers("/api/v1/quizzes/all/**").hasAnyRole("TRAINER", "TRAINEE")
                        .requestMatchers("/api/v1/quizzes/trainer/**").hasRole("TRAINER")
                        .requestMatchers("/api/v1/quiz-submissions/trainee/**").hasRole("TRAINEE")
                        .requestMatchers("/api/v1/quiz-submissions/all/**").hasAnyRole("TRAINEE", "TRAINER")
                        .requestMatchers("/api/v1/assignments/**").hasAnyRole("TRAINER", "TRAINEE")
                        .requestMatchers("/api/v1/lab-submissions/trainee/**").hasRole("TRAINEE")
                        .requestMatchers("/api/v1/lab-submissions/trainer/**").hasRole("TRAINER")
                        .requestMatchers("/api/v1/lab-submissions/all/**").hasAnyRole("TRAINEE", "TRAINER")
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}