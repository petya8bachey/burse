package org.petya8bachey.service;

import org.petya8bachey.enums.SessionStatus;
import org.petya8bachey.model.Session;
import org.petya8bachey.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<Session> findAllSessions() {
        return sessionRepository.findAll();
    }

    public Optional<Session> findSessionById(UUID id) {
        return sessionRepository.findById(id);
    }

    public Optional<Session> findActiveSession() {
        List<Session> activeSessions = sessionRepository.findByStatus(SessionStatus.ACTIVE);
        if (activeSessions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(activeSessions.get(0));
    }
}
