package edu.java.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

public record Update(
    @NotNull
    Long linkId,

    @NotBlank
    String url,

    @NotBlank
    String description,

    @NotNull
    HttpStatus httpStatus,

    OffsetDateTime updateTime,

    @NotEmpty
    List<Long> tgChatIds
) {
}
