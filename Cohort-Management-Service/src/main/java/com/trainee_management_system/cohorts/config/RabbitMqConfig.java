package com.trainee_management_system.cohorts.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public Exchange cohortStatusExchange() {
        return ExchangeBuilder.directExchange("cohort-status-exchange").durable(true).build();
    }

    @Bean
    public Queue cohortStatusQueue() {
        return new Queue("cohort-status-queue", true);
    }

    @Bean
    public Queue cohortStatusSecondaryQueue() {
        return new Queue("cohort-status-secondary-queue", true);
    }

    @Bean
    public Binding cohortStatusBinding(Queue cohortStatusQueue, Exchange cohortStatusExchange) {
        return BindingBuilder.bind(cohortStatusQueue)
                .to(cohortStatusExchange)
                .with("cohort.status.update")
                .noargs();
    }

    @Bean
    public Binding cohortStatusSecondaryBinding(Queue cohortStatusSecondaryQueue, Exchange cohortStatusExchange) {
        return BindingBuilder.bind(cohortStatusSecondaryQueue)
                .to(cohortStatusExchange)
                .with("cohort.status.secondary")
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
