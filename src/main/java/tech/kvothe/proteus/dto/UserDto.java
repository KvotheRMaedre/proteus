package tech.kvothe.proteus.dto;

import jakarta.validation.constraints.NotNull;

public record UserDto(@NotNull String email,
                      @NotNull String password) {
}
