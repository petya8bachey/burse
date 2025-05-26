package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    private ClientType clientType = ClientType.PHYSICAL; // Default value

    @Column(name = "registration_date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate registrationDate; // Default value set by DB

    // NEW: One-to-one relationship with User for login credentials
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", unique = true) // user_id is foreign key and unique
    private User user;

    // Relationships

    // "Заключает договор с" (1:M) Брокер -> Клиент (Client is on the 'Many' side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id") // Foreign key column
    private Broker broker;

    // "Участвует в" (М:М) Клиент -> Торговая сессия
    @ManyToMany
    @JoinTable(
            name = "client_session",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Session> participatingSessions = new HashSet<>();

    // "Торгует" (М:М) Клиент -> Акция
    @ManyToMany
    @JoinTable(
            name = "client_stock",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Stock> tradedStocks = new HashSet<>();

    // "Совершает" (М:1) Сделка -> Клиент (Client is on the '1' side)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Transaction> transactions = new HashSet<>();
}
