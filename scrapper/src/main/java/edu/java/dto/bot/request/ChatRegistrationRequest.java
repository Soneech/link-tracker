package edu.java.dto.bot.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRegistrationRequest(
    @NotNull
    Long id,

    @NotBlank
    String name
) {
}
