package edu.java.dto.github.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record GitHubErrorResponse(
    @NotBlank
    String message,

    @NotBlank
    @JsonProperty("documentation_url")
    String documentationUrl
) {
}
