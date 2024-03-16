package edu.java.dto.github.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record PushEventResponse(
    @NotNull
    Long id,

    @NotNull
    @JsonProperty("name")
    String repositoryName,

    @NotNull
    @JsonProperty("pushed_at")
    OffsetDateTime pushedAt
) {
}
