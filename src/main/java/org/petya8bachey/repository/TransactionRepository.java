package org.petya8bachey.repository;

import org.petya8bachey.model.Client;
import org.petya8bachey.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByClient(Client client);
}
