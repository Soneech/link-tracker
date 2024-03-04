package edu.java.dto.bot;

import jakarta.validation.constraints.NotBlank;

public record LinkUpdateResponse(
    @NotBlank
    String message
) {
}
