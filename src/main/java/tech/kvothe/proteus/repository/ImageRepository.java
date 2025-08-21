package tech.kvothe.proteus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.kvothe.proteus.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
