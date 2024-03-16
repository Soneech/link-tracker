package edu.java.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record Update(
    @NotBlank
    String description,

    @NotNull
    OffsetDateTime updateTime
) {
}
