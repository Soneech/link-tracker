package edu.java.dto.bot.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record LinkUpdateRequest(
    @NotNull
    Long id,

    @NotBlank
    String url,

    String description,

    @NotEmpty
    @JsonProperty("tgChatIds")
    List<Long> telegramChatIds
) {
}
