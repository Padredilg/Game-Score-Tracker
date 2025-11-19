package com.cen4010.gamescoretracker.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.REGULAR;

    @Column(unique = true)
    private String groupCode; // Admin receives one automatically; regular receives on join

    @Builder.Default
    @Column(nullable = false)
    private int victories = 0;

    @Builder.Default
    @Column(nullable = false)
    private int matchesPlayed = 0;

    @Builder.Default
    @Column(nullable = false)
    private int defeats = 0;

    @Builder.Default
    @Column(nullable = false)
    private int cumulativeScore = 0;

    @Builder.Default
    @Column(nullable = false)
    private int highestScore = 0;

    private String avatarUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MatchScore> matchScores = new HashSet<>();

    public enum Role {
        ADMIN, REGULAR
    }
}

