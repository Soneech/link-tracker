package edu.java.dto.response;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkResponse(
    Long id,

    @NotNull
    URI uri
) {
}
