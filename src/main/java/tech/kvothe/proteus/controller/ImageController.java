package tech.kvothe.proteus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.kvothe.proteus.dataModels.TransformationData;
import tech.kvothe.proteus.dto.ApiResponse;
import tech.kvothe.proteus.dto.ImageResponse;
import tech.kvothe.proteus.dto.PaginationResponse;
import tech.kvothe.proteus.service.ImageService;

import java.awt.image.BufferedImage;
import java.io.IOException;


@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<Void> uploadImage(@RequestHeader("Authorization") String token,
                                            @RequestParam("file") MultipartFile multipartFile) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        imageService.saveImage(multipartFile, currentPrincipalName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/transform")
    public ResponseEntity<Void> transformImage(@RequestHeader("Authorization") String token,
                                               @PathVariable("id") Long imageId,
                                               @RequestBody TransformationData transformationData) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        imageService.addToQueue(transformationData, imageId, currentPrincipalName);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<BufferedImage> getImage(@PathVariable Long imageId) throws IOException {
        var image = imageService.findImageById(imageId);
        return ResponseEntity.ok(image);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ImageResponse>> getAllImages(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) throws IOException {

        var pageResponse = imageService.findAllImages(PageRequest.of(page, pageSize));

        return ResponseEntity.ok(new ApiResponse<>(
                pageResponse.getContent(),
                PaginationResponse.fromPage(pageResponse)
        ));
    }
}
