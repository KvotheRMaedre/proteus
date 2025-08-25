package tech.kvothe.proteus.dto;

import tech.kvothe.proteus.entity.Image;

import java.time.Instant;

public record ImageResponse(Long id,
                            String fileName,
                            String extension,
                            Instant createdAt,
                            Long userId){

    public static ImageResponse fromEntity(Image image){
        return new ImageResponse(
                image.getId(),
                image.getFileName(),
                image.getExtension(),
                image.getCreatedAt(),
                image.getUser().getId()
        );
    }
}
