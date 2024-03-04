package edu.java.dto.api.response;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ListLinksResponse(
    @NotEmpty
    List<LinkResponse> links,
    int size
) {
}
