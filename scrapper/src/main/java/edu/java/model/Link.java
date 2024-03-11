package edu.java.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Link {
    private Long id;

    @NotNull
    private String url;

    public Link(String url) {
        this.url = url;
    }
}
