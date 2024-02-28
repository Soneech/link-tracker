package edu.java.model;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.Data;

@Data
public class Link {
    private Long id;

    @NotNull
    private URI uri;

    public Link(URI uri) {
        this.uri = uri;
    }
}
