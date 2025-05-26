package org.petya8bachey.repository;

import org.petya8bachey.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RepositoryRepository extends JpaRepository<Repository, UUID> {
}
