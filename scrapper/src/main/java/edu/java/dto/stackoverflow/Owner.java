package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record Owner(
    @NotNull
    @JsonProperty("display_name")
    String name
) {
}
