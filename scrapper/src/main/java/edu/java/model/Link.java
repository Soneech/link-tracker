package edu.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "url")
    private String url;

    @NotNull
    @Column(name = "last_check_time")
    private OffsetDateTime lastCheckTime;

    @NotNull
    @Column(name = "last_update_time")
    private OffsetDateTime lastUpdateTime;

    @ManyToMany(mappedBy = "trackingLinks")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Chat> tgChats;

    public Link(String url) {
        this.url = url;
    }
}
