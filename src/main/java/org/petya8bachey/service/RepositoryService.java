package org.petya8bachey.service;

import org.petya8bachey.model.Repository;
import org.petya8bachey.repository.RepositoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;

    public RepositoryService(RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    public Optional<Repository> findRepositoryById(UUID id) {
        return repositoryRepository.findById(id);
    }

    // Method to find the main repository (assuming there's one or we take the first)
    public Optional<Repository> findMainRepository() {
        // For simplicity, find the first repository found
        return repositoryRepository.findAll().stream().findFirst();
    }
}
