package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.petya8bachey.enums.StockSector;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @Column(name = "stock_id", nullable = false, length = 10)
    private String stockId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sector")
    private StockSector sector;

    @Column(name = "current_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentPrice = BigDecimal.ZERO;

    @ManyToMany(mappedBy = "tradedStocks")
    @ToString.Exclude
    private Set<Client> tradingClients = new HashSet<>();

    @ManyToMany(mappedBy = "registeredStocks")
    @ToString.Exclude
    private Set<Broker> registeringBrokers = new HashSet<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Transaction> transactions = new HashSet<>();
}
