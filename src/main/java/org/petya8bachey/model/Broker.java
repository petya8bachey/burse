package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    private BrokerStatus status = BrokerStatus.ACTIVE;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", unique = true)
    @EqualsAndHashCode.Exclude
    private User user;

    @OneToMany(mappedBy = "broker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Client> clients = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "broker_stock",
            joinColumns = @JoinColumn(name = "broker_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Stock> registeredStocks = new HashSet<>();
}
