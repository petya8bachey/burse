// src/main/java/org/petya8bachey/model/Transaction.java

package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.petya8bachey.enums.TransactionDirection;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID transactionId;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private TransactionDirection direction;

    // Relationships

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // CHANGE THIS LINE: from FetchType.LAZY to FetchType.EAGER
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    // A transaction involves a specific stock
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
}
