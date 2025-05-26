package org.petya8bachey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.petya8bachey.enums.UserRole;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID userId;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255) // Store hashed password
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true; // For account activation/deactivation

    // Optional: One-to-one relationship to link to specific business entities
    // A user can be linked to either a Broker or a Client, or neither (if it's an Admin)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    @ToString.Exclude
    private Broker brokerProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    @ToString.Exclude
    private Client clientProfile;
}
