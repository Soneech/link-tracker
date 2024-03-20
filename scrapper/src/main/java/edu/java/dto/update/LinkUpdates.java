package edu.java.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkUpdates {
    @NotNull
    private Long linkId;

    @NotBlank
    private String url;

    @NotNull
    private HttpStatus httpStatus;

    @NotNull
    OffsetDateTime lastUpdateTime;

    @NotEmpty
    private List<Update> updates;

    @NotEmpty
    List<Long> tgChatIds;
}
