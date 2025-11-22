package com.cen4010.gamescoretracker.api.match.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {

    List<Match> findByGroupGroupId(UUID groupId);
}
