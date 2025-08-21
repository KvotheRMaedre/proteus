package tech.kvothe.proteus.service;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import tech.kvothe.proteus.dataModels.TransformationData;
import tech.kvothe.proteus.entity.Image;
import tech.kvothe.proteus.exception.NotAuthorizedImageTransformationException;
import tech.kvothe.proteus.repository.ImageRepository;
import tech.kvothe.proteus.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    private static final String DIRECTORY_PATH = System.getenv("UPLOAD_DIRECTORY");

    public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
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

    public void transformImage(TransformationData transformationData, Long imageId, String userEmail) throws IOException {

        var imageDB = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(imageDB.getUser().getId(), user.getId())) {
            throw new NotAuthorizedImageTransformationException("This image is not yours to transform!");
        }

        String filePath = DIRECTORY_PATH + user.getId() + "/" + imageDB.getFileName() + "." +imageDB.getExtension();
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

        ImageIO.write(imageTransformed,imageDB.getExtension(),new File(filePath));
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

}
