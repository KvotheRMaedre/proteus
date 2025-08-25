package tech.kvothe.proteus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import tech.kvothe.proteus.dataModels.TransformationData;
import tech.kvothe.proteus.entity.Image;
import tech.kvothe.proteus.exception.ImageFormatNotAvailableException;
import tech.kvothe.proteus.exception.NotAuthorizedImageTransformationException;
import tech.kvothe.proteus.repository.ImageRepository;
import tech.kvothe.proteus.repository.UserRepository;
import tech.kvothe.proteus.wrapper.ImageRabbitWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    private static final String DIRECTORY_PATH = System.getenv("UPLOAD_DIRECTORY");
    public static final String[] allowedFormat = {"JPG", "JPEG", "PNG", "BMP", "WBMP" , "GIF"};

    public ImageService(ImageRepository imageRepository, UserRepository userRepository, RabbitMqService rabbitMqService) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public void saveImage(MultipartFile multipartFile, String userEmail) throws IOException {

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String originalName = FilenameUtils.getBaseName(HtmlUtils.htmlEscape(multipartFile.getOriginalFilename()));

        File thumbnailFile = new File(DIRECTORY_PATH + user.getId() + "/" + originalName + "." + extension);
        ImageIO.write(originalImage, extension, thumbnailFile);

        var image = new Image(
                user,
                Instant.now(),
                extension,
                originalName
        );

        imageRepository.save(image);

    }

    public void saveTransformedImage(BufferedImage image, String userEmail, String extension, String fileName) throws IOException {

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        File thumbnailFile = new File(DIRECTORY_PATH + user.getId() + "/" + fileName + "." + extension);
        ImageIO.write(image, extension, thumbnailFile);

        var savedImage = new Image(
                user,
                Instant.now(),
                extension,
                fileName
        );

        imageRepository.save(savedImage);

    }

    public void addToQueue(TransformationData transformationData, Long imageId, String userEmail) throws JsonProcessingException {
        String message = transformationToJson(transformationData, imageId, userEmail);
        RabbitMqService.sendMessage(message);
    }

    private static String transformationToJson(TransformationData transformationData, Long imageId, String userEmail) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        var transformations = transformationData.getTransformations();

        var dataWrapper = new ImageRabbitWrapper(
                transformations.getResize(),
                transformations.getCrop(),
                transformations.getRotate(),
                transformations.getFormat(),
                transformations.getFilters(),
                imageId,
                userEmail
        );

        return objectMapper.writeValueAsString(dataWrapper);
    }

    public void transformImage(TransformationData transformationData, Long imageId, String userEmail) throws IOException {

        var imageDB = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(imageDB.getUser().getId(), user.getId())) {
            throw new NotAuthorizedImageTransformationException("This image is not yours to transform!");
        }

        String filePath = DIRECTORY_PATH + user.getId() + "/" + imageDB.getFileName() + "." +imageDB.getExtension();
        var extensionToSave = imageDB.getExtension();
        BufferedImage imageTransformed = ImageIO.read(new File(filePath));

        if (wantResize(transformationData)){
            var resize = transformationData.getTransformations().getResize();
            imageTransformed = resize(imageTransformed, resize.getWidth(), resize.getHeight());
        }

        if (wantCrop(transformationData)){
            var crop = transformationData.getTransformations().getCrop();
            imageTransformed = crop(imageTransformed, crop.getWidth(), crop.getHeight(), crop.getX(), crop.getY());
        }

        if (transformationData.getTransformations().getRotate() != null){
            imageTransformed = Scalr.rotate(imageTransformed, transformationData.getTransformations().getRotate());
        }

        if (validateFormat(transformationData)) {
            extensionToSave = transformationData.getTransformations().getFormat().toLowerCase();
        } else {
            throw new ImageFormatNotAvailableException("Formats supported:"+ StringUtils.join(allowedFormat));
        }

        if (transformationData.getTransformations().getFilters().getGrayscale()) {
            imageTransformed = Scalr.apply(imageTransformed, Scalr.OP_GRAYSCALE);
        }

        if (transformationData.getTransformations().getFilters().getSepia()) {
            imageTransformed = applySepia(imageTransformed);
        }

        if (transformationData.getTransformations().getFilters().getAntialias()) {
            imageTransformed = Scalr.apply(imageTransformed, Scalr.OP_ANTIALIAS);
        }

        var newName = imageDB.getFileName() + "-transformed";
        String transformedFilePath = DIRECTORY_PATH + user.getId() + "/" + newName + "." + extensionToSave;

        ImageIO.write(imageTransformed, extensionToSave, new File(transformedFilePath));
        saveTransformedImage(imageTransformed, userEmail, extensionToSave, newName);

    }

    private boolean validateFormat(TransformationData transformationData) {
        if (transformationData.getTransformations().getFormat() != null) {
            var format = transformationData.getTransformations().getFormat();
            return Arrays.asList(allowedFormat).contains(format.toUpperCase());
        }
        return false;
    }

    private boolean wantResize(TransformationData transformationData) {
        if (transformationData.getTransformations().getResize() != null) {
            var resize = transformationData.getTransformations().getResize();
            return resize.getHeight() != 0 && resize.getWidth() != 0;
        }
        return false;
    }

    public  BufferedImage resize(BufferedImage img, int width, int height) {
        return Scalr.resize(img, Scalr.Method.QUALITY, width, height);
    }

    private boolean wantCrop(TransformationData transformationData) {
        if (transformationData.getTransformations().getCrop() != null) {
            var crop = transformationData.getTransformations().getCrop();
            return crop.getHeight() != 0 && crop.getWidth() != 0;
        }
        return false;
    }

    public BufferedImage crop(BufferedImage img, int width, int height, int x, int y) {
        return Scalr.crop(img, x, y, width, height);
    }

    public BufferedImage applySepia(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalRGB = image.getRGB(x, y);

                int a = (originalRGB >> 24) & 0xff;
                int R = (originalRGB >> 16) & 0xff;
                int G = (originalRGB >> 8) & 0xff;
                int B = originalRGB & 0xff;

                // calculate newRed, newGreen, newBlue
                int newRed = (int) (0.393 * R + 0.769 * G
                        + 0.189 * B);
                int newGreen = (int) (0.349 * R + 0.686 * G
                        + 0.168 * B);
                int newBlue = (int) (0.272 * R + 0.534 * G
                        + 0.131 * B);

                if (newRed > 255)
                    R = 255;
                else
                    R = newRed;

                if (newGreen > 255)
                    G = 255;
                else
                    G = newGreen;

                if (newBlue > 255)
                    B = 255;
                else
                    B = newBlue;

                int newRGB = (a << 24) | (R << 16) | (G << 8) | B;

                image.setRGB(x, y, newRGB);
            }
        }
        return image;
    }

    public BufferedImage findImageById(Long imageId) throws IOException {
        var imageDB = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String filePath = DIRECTORY_PATH + imageDB.getUser().getId() + "/" + imageDB.getFileName() + "." +imageDB.getExtension();
        var extensionToSave = imageDB.getExtension();
        return ImageIO.read(new File(filePath));

    }
}
