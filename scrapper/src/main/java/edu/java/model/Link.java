package edu.java.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Link {
    private Long id;

    @NotNull
    private String link;

    public Link(String link) {
        this.link = link;
    }
}
