package tech.kvothe.proteus.service;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import tech.kvothe.proteus.entity.Image;
import tech.kvothe.proteus.repository.ImageRepository;
import tech.kvothe.proteus.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

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

        File thumbnailFile = new File(DIRECTORY_PATH + user.getId() +"/" + originalName + "." + extension);
        ImageIO.write(originalImage, extension, thumbnailFile);

        var image = new Image(
                user,
                Instant.now(),
                extension,
                originalName
        );

        imageRepository.save(image);

    }
}
