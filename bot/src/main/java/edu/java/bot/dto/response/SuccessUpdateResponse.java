package edu.java.bot.dto.response;

import jakarta.validation.constraints.NotBlank;

public record SuccessUpdateResponse(
    @NotBlank
    String message
) {
}
