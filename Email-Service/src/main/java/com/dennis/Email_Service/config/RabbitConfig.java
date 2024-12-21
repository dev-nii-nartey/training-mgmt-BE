package com.dennis.Email_Service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EMAIL_QUEUE = "emailQueue";
    public static final String OTP_EMAIL_QUEUE = "OtpEmailQueue";
    public static final String USER_SERVICE_EXCHANGE = "userServiceExchange";

    
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue emailOTPQueue() {
        return new Queue(OTP_EMAIL_QUEUE, true);
    }

    @Bean
    public Exchange userServiceExchange() {
        return ExchangeBuilder.directExchange(USER_SERVICE_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding emailQueueBinding(Queue emailQueue, Exchange userServiceExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(userServiceExchange)
                .with("email.notification")
                .noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
