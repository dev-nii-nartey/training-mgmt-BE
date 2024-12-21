package com.dennis.user.service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String TRAINEE_PROFILE_QUEUE = "profileTrainee";
    public static final String TRAINER_PROFILE_QUEUE = "profileTrainer";
    public static final String EMAIL_QUEUE = "emailQueue";
    public static final String USER_SERVICE_EXCHANGE = "userServiceExchange";

    @Bean
    public Queue traineeProfileQueue() {
        return new Queue(TRAINEE_PROFILE_QUEUE, true);
    }

    @Bean
    public Queue trainerProfileQueue() {
        return new Queue(TRAINER_PROFILE_QUEUE, true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Exchange userServiceExchange() {
        return ExchangeBuilder.directExchange(USER_SERVICE_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding traineeProfileBinding(Queue traineeProfileQueue, Exchange userServiceExchange) {
        return BindingBuilder.bind(traineeProfileQueue)
                .to(userServiceExchange)
                .with("profile.trainee")
                .noargs();
    }

    @Bean
    public Binding trainerProfileBinding(Queue trainerProfileQueue, Exchange userServiceExchange) {
        return BindingBuilder.bind(trainerProfileQueue)
                .to(userServiceExchange)
                .with("profile.trainer")
                .noargs();
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, Exchange userServiceExchange) {
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
