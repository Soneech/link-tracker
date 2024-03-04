package edu.java.dto.api.request;

import jakarta.validation.constraints.NotBlank;

public record RemoveLinkRequest(
    @NotBlank
    String link
) {
}
