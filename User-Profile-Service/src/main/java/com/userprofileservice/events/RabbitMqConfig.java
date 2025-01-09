package com.userprofileservice.events;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String TRAINER_PROFILE_QUEUE = "profileTrainer";
    public static final String TRAINEE_PROFILE_QUEUE = "profileTrainee";
    public static final String SPECIALIZATION_QUEUE = "specializationQueue";
    public static final String COHORT_QUEUE = "cohortQueue";



    @Bean
    public Queue trainerProfileQueue() {
        return new Queue(TRAINER_PROFILE_QUEUE, true);
    }

    @Bean
    public Queue traineeProfileQueue() {
        return new Queue(TRAINEE_PROFILE_QUEUE, true);
    }


    @Bean
    public Queue SpecializationQueue() {
        return new Queue(SPECIALIZATION_QUEUE, true);
    }

    @Bean
    public Queue CohortQueue() {
        return new Queue(COHORT_QUEUE, true);
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