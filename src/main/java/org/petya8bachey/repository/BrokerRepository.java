package org.petya8bachey.repository;

import org.petya8bachey.model.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BrokerRepository extends JpaRepository<Broker, UUID> {
}
