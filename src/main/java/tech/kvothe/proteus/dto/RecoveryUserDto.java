package tech.kvothe.proteus.dto;

import jakarta.validation.constraints.NotNull;

public record RecoveryUserDto(@NotNull Long id,
                              @NotNull String email) {
}
