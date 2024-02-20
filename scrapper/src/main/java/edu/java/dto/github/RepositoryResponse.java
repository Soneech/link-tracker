package edu.java.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record RepositoryResponse(
    @NotNull
    Long id,

    @NotNull
    @JsonProperty("name")
    String repositoryName,

    @NotNull
    Owner owner,

    @NotNull
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt,

    @NotNull
    @JsonProperty("pushed_at")
    OffsetDateTime pushedAt,

    @NotNull
    Long someFieldSomeField
) {
}
