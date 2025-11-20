package com.cen4010.gamescoretracker.repositories;

import com.cen4010.gamescoretracker.models.MatchScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchScoreRepository extends JpaRepository<MatchScore, UUID> {

    List<MatchScore> findByMatchMatchId(UUID matchId);

    void deleteByMatch_MatchId(UUID matchId);
}
