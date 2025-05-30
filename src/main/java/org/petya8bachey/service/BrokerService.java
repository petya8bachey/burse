package org.petya8bachey.service;

import org.petya8bachey.model.Broker;
import org.petya8bachey.model.User;
import org.petya8bachey.repository.BrokerRepository;
import org.petya8bachey.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BrokerService {

    private final BrokerRepository brokerRepository;
    private final UserRepository userRepository;

    public BrokerService(BrokerRepository brokerRepository, UserRepository userRepository) {
        this.brokerRepository = brokerRepository;
        this.userRepository = userRepository;
    }

    public List<Broker> findAllBrokers() {
        return brokerRepository.findAll();
    }

    public Broker findBrokerById(UUID id) {
        return brokerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Broker not found with ID: " + id));
    }
}
