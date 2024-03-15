package edu.java.model;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    private Long id;

    private OffsetDateTime registeredAt;

    private List<Link> trackingLinks;

    public Chat(long id, OffsetDateTime registeredAt) {
        this.id = id;
        this.registeredAt = registeredAt;
    }
}
