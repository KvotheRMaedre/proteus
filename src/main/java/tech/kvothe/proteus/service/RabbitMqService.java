package tech.kvothe.proteus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static tech.kvothe.proteus.config.RabbitMqController.IMAGE_TRANSFORM_QUEUE;

@Service
public class RabbitMqService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqService.class);

    private static final String RABBIT_HOST = System.getenv("RABBIT_HOST");
    private static final String RABBIT_PORT = System.getenv("RABBIT_PORT");
    private static final String RABBIT_USERNAME = System.getenv("RABBIT_USERNAME");
    private static final String RABBIT_PASSWORD = System.getenv("RABBIT_PASSWORD");

    public static void sendMessage(String message) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBIT_HOST);
        factory.setPort(Integer.parseInt(RABBIT_PORT));
        factory.setUsername(RABBIT_USERNAME);
        factory.setPassword(RABBIT_PASSWORD);

        try (Connection connection = factory.newConnection()){
            Channel channel = connection.createChannel();
            channel.queueDeclare(IMAGE_TRANSFORM_QUEUE, true, false, false, null);
            channel.basicPublish("", IMAGE_TRANSFORM_QUEUE, null, message.getBytes());
            logger.info("Message send to queue");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
