package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.petya8bachey.enums.BrokerStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "brokers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "broker_id", columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID brokerId;

    @Column(name = "license_number", unique = true, nullable = false, length = 30)
    private String licenseNumber;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BrokerStatus status = BrokerStatus.ACTIVE; // Default value

    // NEW: One-to-one relationship with User for login credentials
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", unique = true) // user_id is foreign key and unique
    private User user;

    // Relationships

    // "Заключает договор с" (1:M) Брокер -> Клиент
    @OneToMany(mappedBy = "broker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Client> clients = new HashSet<>();

    // "Регистрирует" (М:М) Брокер -> Акция
    @ManyToMany
    @JoinTable(
            name = "broker_stock",
            joinColumns = @JoinColumn(name = "broker_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    @ToString.Exclude // Avoid infinite loop in toString()
    private Set<Stock> registeredStocks = new HashSet<>();
}
