package edu.java.bot.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ApiErrorResponse(
    @NotBlank
    String description,

    @NotBlank
    String code,

    @NotBlank
    String exceptionName,

    @NotBlank
    String exceptionMessage,

    @NotEmpty
    List<String> stackTrace
) {
}
