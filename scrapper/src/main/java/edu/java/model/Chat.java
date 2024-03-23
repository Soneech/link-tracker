package edu.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "trackingLinks")
@EqualsAndHashCode(exclude = "trackingLinks")
public class Chat {
    @Id
    private Long id;

    @Column(name = "registered_at")
    private OffsetDateTime registeredAt;

    @ManyToMany
    @JoinTable(
        name = "chat_link",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private List<Link> trackingLinks;

    public Chat(Long id) {
        this.id = id;
    }

    public Chat(long id, OffsetDateTime registeredAt) {
        this.id = id;
        this.registeredAt = registeredAt;
    }
}
