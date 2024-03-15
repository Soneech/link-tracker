package edu.java.dto.bot.response;

import jakarta.validation.constraints.NotBlank;

public record LinkUpdateResponse(
    @NotBlank
    String message
) {
}
