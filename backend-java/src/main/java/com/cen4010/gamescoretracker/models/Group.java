package com.cen4010.gamescoretracker.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID groupId;

    @Column(nullable = false, unique = true)
    private String groupCode;

    @Column(nullable = false)
    private String groupName;

    // Admin user who created the group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    @ToString.Exclude
    private User admin;

    // Visibility settings – all default to true
    @Builder.Default
    private boolean winPercentageVisibility = true;

    @Builder.Default
    private boolean matchesPlayedVisibility = true;

    @Builder.Default
    private boolean victoriesVisibility = true;

    @Builder.Default
    private boolean defeatsVisibility = true;

    @Builder.Default
    private boolean cumulativeScoreVisibility = true;

    @Builder.Default
    private boolean highestScoreVisibility = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // --- Relationships ---

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Match> matches = new HashSet<>();
}
