package edu.java.bot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ApiErrorResponse(
    @NotBlank
    String description,

    @NotBlank
    String code,

    @NotBlank
    @JsonProperty("exception_name")
    String exceptionName,

    @NotBlank
    String message,

    @NotEmpty
    @JsonProperty("stack_trace")
    List<String> stackTrace
) {
}
