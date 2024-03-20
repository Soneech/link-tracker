package edu.java.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Link {
    private Long id;

    @NotNull
    private String url;

    @NotNull
    private OffsetDateTime lastCheckTime;

    private OffsetDateTime lastUpdateTime;

    public Link(String url) {
        this.url = url;
    }
}
