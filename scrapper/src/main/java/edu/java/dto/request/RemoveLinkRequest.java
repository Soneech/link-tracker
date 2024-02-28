package edu.java.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RemoveLinkRequest(
    @NotBlank
    String link
) {
}
