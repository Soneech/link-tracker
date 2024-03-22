package edu.java.dto.github.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RepositoryInfoResponse(
    @NotNull
    Long id,

    @NotBlank
    @JsonProperty("full_name")
    String fullName
) {
}
