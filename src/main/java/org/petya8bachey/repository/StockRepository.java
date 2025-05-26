package org.petya8bachey.repository;

import org.petya8bachey.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> { // Stock ID is String (ticker)
}

