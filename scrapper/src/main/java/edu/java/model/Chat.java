package edu.java.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    private Long id;
    private String name;
    private List<Link> trackingLinks;

    public Chat(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
