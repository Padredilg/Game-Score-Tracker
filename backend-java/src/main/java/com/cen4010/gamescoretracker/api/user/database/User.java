package com.cen4010.gamescoretracker.api.user.database;

import com.cen4010.gamescoretracker.api.group.database.Group;
import com.cen4010.gamescoretracker.api.matchscore.database.MatchScore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.REGULAR;

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
    @ToString.Exclude
    private Group group;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<MatchScore> matchScores = new HashSet<>();

    public enum Role {
        ADMIN, REGULAR;

        public static Role fromString(String roleStr) {
            if (roleStr == null) {
                throw new IllegalArgumentException("Role cannot be null. Valid roles are: ADMIN, REGULAR.");
            }

            try {
                return Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid role: '" + roleStr + "'. Valid roles are: ADMIN, REGULAR."
                );
            }
        }
    }
}

