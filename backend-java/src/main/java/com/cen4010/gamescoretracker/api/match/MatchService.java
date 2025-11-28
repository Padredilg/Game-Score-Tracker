package com.cen4010.gamescoretracker.api.match;

import com.cen4010.gamescoretracker.api.group.GroupService;
import com.cen4010.gamescoretracker.api.group.database.Group;
import com.cen4010.gamescoretracker.api.match.database.Match;
import com.cen4010.gamescoretracker.api.match.database.MatchScore;
import com.cen4010.gamescoretracker.api.match.database.MatchRepository;
import com.cen4010.gamescoretracker.api.match.database.MatchScoreRepository;
import com.cen4010.gamescoretracker.api.match.dto.MatchCreateRequest;
import com.cen4010.gamescoretracker.api.match.dto.MatchReportDTO;
import com.cen4010.gamescoretracker.api.user.UserService;
import com.cen4010.gamescoretracker.api.user.database.User;

import com.cen4010.gamescoretracker.api.user.database.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserService userService;
    private final GroupService groupService;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchScoreRepository matchScoreRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Transactional
    public MatchReportDTO recordMatch(MatchCreateRequest request) {

        User currentUser = userService.getCurrentUser();
        Group group = groupService.getGroupForUser(currentUser);

        LocalDateTime matchDate = parseMatchDate(request.getDate());
        Match.MatchResult matchResult = parseMatchResult(request.getResult());

        // 1. Create Match entity
        Match match = createMatch(group, currentUser, matchDate, matchResult);

        // 2. Persist match BEFORE linking scores (so FK is valid)
        Match savedMatch = matchRepository.save(match);

        // 3. Attach all player scores
        List<MatchScore> scores = buildMatchScoreList(match, request);

        // 4. Save match scores
        List<MatchScore> savedScores = matchScoreRepository.saveAll(scores);

        // 5. Update player statistics
        updatePlayerStatistics(scores);

        // 6. Save updated players
        List<User> updatedUsers = userRepository.saveAll(
                scores.stream().map(MatchScore::getUser).toList()
        );

        return MatchMapper.toReportDto(savedMatch, savedScores, updatedUsers);
    }

    // --------------------------------------------------------------
    // --------------------- Helper Methods -------------------------
    // --------------------------------------------------------------

    /** Parses MM/dd/yyyy → LocalDateTime */
    private LocalDateTime parseMatchDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER).atStartOfDay();
    }

    /** Converts "win", "loss", "draw" → MatchResult enum */
    private Match.MatchResult parseMatchResult(String result) {
        try {
            return Match.MatchResult.valueOf(result.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid match result: " + result);
        }
    }

    /** Builds the Match entity */
    private Match  createMatch(Group group, User createdBy, LocalDateTime date, Match.MatchResult result) {
        return Match.builder()
                .group(group)
                .createdBy(createdBy)
                .matchDate(date)
                .result(result)
                .build();
    }

    /** Builds list of MatchScore entities from winners + losers maps */
    private List<MatchScore> buildMatchScoreList(Match match, MatchCreateRequest request) {

        List<MatchScore> scores = new ArrayList<>();

        // Winners
        if (request.getWinners() != null) {
            for (var entry : request.getWinners().entrySet()) {
                User user = getUser(entry.getKey());
                // If match is a draw, winners get TIE
                MatchScore.PlayerRole role = request.getResult().equalsIgnoreCase("draw")
                        ? MatchScore.PlayerRole.TIE
                        : MatchScore.PlayerRole.WINNER;
                scores.add(buildMatchScore(match, user, entry.getValue(), role));
            }
        }

        // Losers
        if (request.getLosers() != null) {
            for (var entry : request.getLosers().entrySet()) {
                User user = getUser(entry.getKey());
                // Losers always remain LOSER, even in a draw
                scores.add(buildMatchScore(match, user, entry.getValue(), MatchScore.PlayerRole.LOSER));
            }
        }

        return scores;
    }

    /** Loads User by UUID */
    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    /** Builds a single MatchScore entity */
    private MatchScore buildMatchScore(Match match, User user, Integer score, MatchScore.PlayerRole role) {
        return MatchScore.builder()
                .match(match)
                .user(user)
                .score(score)
                .role(role)
                .build();
    }

    /** Updates stats for each player depending on WIN/LOSS/DRAW */
    private void updatePlayerStatistics(List<MatchScore> scores) {

        for (MatchScore ms : scores) {

            User u = ms.getUser();

            u.setMatchesPlayed(u.getMatchesPlayed() + 1);
            u.setCumulativeScore(u.getCumulativeScore() + ms.getScore());

            if (ms.getScore() > u.getHighestScore()) {
                u.setHighestScore(ms.getScore());
            }

            switch (ms.getRole()) {
                case WINNER -> u.setVictories(u.getVictories() + 1);
                case LOSER -> u.setDefeats(u.getDefeats() + 1);
                case TIE -> u.setDraws(u.getDraws() + 1);
            }
        }
    }
}
