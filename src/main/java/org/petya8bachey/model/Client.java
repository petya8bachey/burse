package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode; // Import this
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.petya8bachey.enums.ClientType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id", columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID clientId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "tax_id", unique = true, nullable = false, length = 20)
    private String taxId; // ИНН

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType = ClientType.PHYSICAL;

    @Column(name = "registration_date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate registrationDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", unique = true)
    @EqualsAndHashCode.Exclude // ADD THIS LINE
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "broker_id")
    @EqualsAndHashCode.Exclude // ADD THIS LINE
    private Broker broker;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_session",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // ADD THIS LINE
    private Set<Session> participatingSessions = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_stock",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // ADD THIS LINE
    private Set<Stock> tradedStocks = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // ADD THIS LINE
    private Set<Transaction> transactions = new HashSet<>();
}
