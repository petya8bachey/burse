package org.petya8bachey.repository;

import org.petya8bachey.enums.SessionStatus;
import org.petya8bachey.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByStatus(SessionStatus sessionStatus);
}
