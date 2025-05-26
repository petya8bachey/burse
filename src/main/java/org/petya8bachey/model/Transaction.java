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

    // "Совершает" (М:1) Сделка -> Клиент
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false) // Foreign key column
    private Client client;

    // "Осуществляется в" (М:1) Сделка -> Торговая сессия
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false) // Foreign key column
    private Session session;

    // "Фиксирует" (1:М) Репозиторий -> Сделка (Transaction is on the 'Many' side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false) // Foreign key column
    private Repository repository;

    // A transaction involves a specific stock
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false) // Foreign key column
    private Stock stock;
}

