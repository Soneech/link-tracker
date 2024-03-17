package edu.java.dto.github.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record EventResponse(
    @NotBlank
    String type,

    @NotNull
    Actor actor,

    @NotNull
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @NotNull
    Payload payload
) {
    public record Actor(@NotBlank String login){ }

    public record Payload(
        String ref,

        @JsonProperty("ref_type")
        String refType,

        String action
    ) {
    }
}
