package tech.kvothe.proteus.config;

import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqController {

    public static final String IMAGE_TRANSFORM_QUEUE = "proteus-image-transform";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Declarable imageTransformQueue() {
        return new Queue(IMAGE_TRANSFORM_QUEUE);
    }
}
