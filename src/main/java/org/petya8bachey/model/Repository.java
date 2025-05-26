package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "repositories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repository { // Renamed from "Репозиторий" to "Repository"

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "repo_id", columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID repoId;

    @UpdateTimestamp // Automatically updates on entity update
    @Column(name = "last_update", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lastUpdate;

    @Column(name = "storage_size_gb") // Nullable
    private Integer storageSizeGb;

    // Relationships

    // "Фиксирует" (1:М) Репозиторий -> Сделка
    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Transaction> transactions = new HashSet<>();
}
