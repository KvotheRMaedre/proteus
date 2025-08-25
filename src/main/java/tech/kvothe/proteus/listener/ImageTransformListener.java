package tech.kvothe.proteus.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import tech.kvothe.proteus.dataModels.TransformationData;
import tech.kvothe.proteus.dto.ImageTransformEvent;
import tech.kvothe.proteus.service.ImageService;

import java.io.IOException;

import static tech.kvothe.proteus.config.RabbitMqController.IMAGE_TRANSFORM_QUEUE;

@Component
public class ImageTransformListener {
    private static final Logger logger = LoggerFactory.getLogger(ImageTransformListener.class);

    private final ImageService imageService;

    public ImageTransformListener(ImageService imageService) {
        this.imageService = imageService;
    }

    @RabbitListener(queues = IMAGE_TRANSFORM_QUEUE)
    public void listen(Message<ImageTransformEvent> message) throws IOException {
        logger.info("Message consumed: {}", message);

        var messageData = message.getPayload();

        var transformationData = new TransformationData();
        var transformations = new TransformationData.Transformations();

        transformations.setResize(messageData.resize());
        transformations.setCrop(messageData.crop());
        transformations.setRotate(messageData.rotate());
        transformations.setFormat(messageData.format());
        transformations.setFilters(messageData.filters());
        transformationData.setTransformations(transformations);

        imageService.transformImage(transformationData, messageData.imageId(), messageData.userEmail());
    }
}
