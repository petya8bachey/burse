package org.petya8bachey.model;

import org.petya8bachey.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id", columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID sessionId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.PLANNED; // Default value

    // Relationships

    // "Участвует в" (М:М) Клиент -> Торговая сессия (Session is on the 'Many' side)
    @ManyToMany(mappedBy = "participatingSessions")
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Client> participatingClients = new HashSet<>();

    // "Осуществляется в" (М:1) Сделка -> Торговая сессия (Session is on the '1' side)
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Transaction> transactions = new HashSet<>();
}
