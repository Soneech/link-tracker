package edu.java.dto.update;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

public record Update(
    @NotBlank
    String description,

    OffsetDateTime updateTime
) {
}
