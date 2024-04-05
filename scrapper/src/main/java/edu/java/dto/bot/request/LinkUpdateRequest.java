package edu.java.dto.bot.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record LinkUpdateRequest(
    @NotNull
    Long id,

    @NotBlank
    String url,

    @NotEmpty
    @JsonProperty("updates_description")
    String updatesDescription,

    @NotEmpty
    @JsonProperty("tg_chat_ids")
    List<Long> telegramChatIds
) {
}
