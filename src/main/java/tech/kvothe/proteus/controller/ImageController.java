package tech.kvothe.proteus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.kvothe.proteus.dataModels.TransformationData;
import tech.kvothe.proteus.service.ImageService;

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

        imageService.transformImage(transformationData, imageId, currentPrincipalName);

        return null;
    }
}
