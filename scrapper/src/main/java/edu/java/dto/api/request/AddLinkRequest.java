package edu.java.dto.api.request;

import jakarta.validation.constraints.NotBlank;

public record AddLinkRequest(
    @NotBlank
    String link
) {
}
