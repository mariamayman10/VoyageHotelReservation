package org.example.voyage.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${app.rabbitmq.queues.email}")
    private String emailQueue;
    @Value("${app.rabbitmq.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.routing-keys.confirmed}")
    private String confirmedKey;
    @Value("${app.rabbitmq.routing-keys.cancelled}")
    private String cancelledKey;

    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange(exchange, true, false);
    }

    // --- Queues ---
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueue)
                .withArgument("x-dead-letter-exchange", exchange + ".dlx")
                .build();
    }

    // --- Bindings (routing key → queue) ---
    @Bean
    public Binding emailConfirmedBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(bookingExchange())
                .with(confirmedKey);
    }

    @Bean
    public Binding emailCancelledBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(bookingExchange())
                .with(cancelledKey);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonJsonMessageConverter());
        return template;
    }
}
