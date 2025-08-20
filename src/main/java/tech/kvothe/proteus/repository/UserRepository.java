package tech.kvothe.proteus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.kvothe.proteus.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
