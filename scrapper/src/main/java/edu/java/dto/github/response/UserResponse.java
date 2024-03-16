package edu.java.dto.github.response;

import jakarta.validation.constraints.NotBlank;

public record UserResponse(
    @NotBlank
    String login
) {
}
