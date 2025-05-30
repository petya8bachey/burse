package org.petya8bachey.service;

import org.petya8bachey.model.Client;
import org.petya8bachey.model.User;
import org.petya8bachey.repository.ClientRepository;
import org.petya8bachey.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    public Client findClientById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Client not found with ID: " + id));
    }
}
