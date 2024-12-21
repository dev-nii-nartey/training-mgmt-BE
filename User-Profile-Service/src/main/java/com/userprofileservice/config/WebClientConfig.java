package com.userprofileservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@EnableRetry
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webclientBuilder(){
        return  WebClient.builder();
    }
}
