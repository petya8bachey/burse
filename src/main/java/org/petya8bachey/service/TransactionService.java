package org.petya8bachey.service;

import jakarta.transaction.Transactional; // Import Transactional
import org.petya8bachey.model.Client;
import org.petya8bachey.model.Repository; // Import Repository
import org.petya8bachey.model.Session; // Import Session
import org.petya8bachey.model.Stock; // Import Stock
import org.petya8bachey.model.Transaction;
import org.petya8bachey.enums.TransactionDirection; // Import TransactionDirection
import org.petya8bachey.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal; // Import BigDecimal
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction findTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found with ID: " + id));
    }

    public List<Transaction> getTransactionsByClient(Client client) {
        return transactionRepository.findByClient(client);
    }

    @Transactional
    public Transaction createTransaction(Client client, Stock stock, Session session, Repository repository,
                                         BigDecimal price, int volume, TransactionDirection direction) {
        Transaction tx = new Transaction();
        tx.setClient(client);
        tx.setStock(stock);
        tx.setSession(session);
        tx.setRepository(repository);
        tx.setPrice(price);
        tx.setVolume(volume);
        tx.setDirection(direction);
        return transactionRepository.save(tx);
    }
}
