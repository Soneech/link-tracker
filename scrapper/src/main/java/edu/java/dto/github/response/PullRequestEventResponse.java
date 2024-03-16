package edu.java.dto.github.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record PullRequestEventResponse(
    @NotBlank
    String title,

    @NotNull
    UserResponse user,

    @NotNull
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @NotNull
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt
) {
}
