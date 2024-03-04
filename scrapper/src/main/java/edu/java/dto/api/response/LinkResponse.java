package edu.java.dto.api.response;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkResponse(
    Long id,

    @NotNull
    URI uri
) {
}
