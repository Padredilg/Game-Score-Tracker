package com.cen4010.gamescoretracker.api.match;

import com.cen4010.gamescoretracker.api.group.GroupService;
import com.cen4010.gamescoretracker.api.group.database.Group;
import com.cen4010.gamescoretracker.api.match.database.Match;
import com.cen4010.gamescoretracker.api.match.database.MatchScore;
import com.cen4010.gamescoretracker.api.match.database.MatchRepository;
import com.cen4010.gamescoretracker.api.match.database.MatchScoreRepository;
import com.cen4010.gamescoretracker.api.match.dto.MatchCreateRequest;
import com.cen4010.gamescoretracker.api.match.dto.MatchReportDTO;
import com.cen4010.gamescoretracker.api.match.dto.UserMatchesDTO;
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

    //Save Match and Scores to DB
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

    //retrieve matches by user id
    public UserMatchesDTO getUserMatches(UUID userId) {

        // Fetch all MatchScore records for this user.
        List<MatchScore> userScores =
                matchScoreRepository.findByUserUserIdOrderByMatchMatchDateDesc(userId);

        // Convert each MatchScore into a UserMatchItemDTO.
        // Because one MatchScore = the user's participation in a single match,
        // we can derive the match + all participants from it.
        List<UserMatchesDTO.UserMatchItemDTO> dtoList = userScores.stream()
                .map(ms -> {
                    Match match = ms.getMatch();
                    List<MatchScore> allScores = match.getMatchScores().stream().toList();

                    return MatchMapper.toUserMatchItemDTO(
                            match,
                            userId,
                            allScores
                    );
                })
                .toList();

        // Wrap the results in the parent DTO object
        return UserMatchesDTO.builder()
                .matches(dtoList)
                .build();
    }

    @Transactional
    public void deleteMatch(UUID matchId) {

        // Load the match
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        // Load ALL MatchScores for this match
        List<MatchScore> scores = matchScoreRepository.findByMatchMatchId(matchId);

        if (scores.isEmpty()) {
            matchRepository.delete(match);
            return;
        }

        // Remove all MatchScores
        matchScoreRepository.deleteAll(scores);

        // Remove the match
        matchRepository.delete(match);


        // Track affected users
        Set<User> affectedUsers = getReversedStatsUsers(scores);

        // Save updated users first
        userRepository.saveAll(affectedUsers);
    }

    private Set<User> getReversedStatsUsers(List<MatchScore> scores) {
        Set<User> affectedUsers = new HashSet<>();

        for (MatchScore ms : scores) {
            User user = ms.getUser();
            affectedUsers.add(user);

            // Reverse stats ----------------------------

            user.setMatchesPlayed(user.getMatchesPlayed() - 1);
            user.setCumulativeScore(user.getCumulativeScore() - ms.getScore());

            switch (ms.getRole()) {
                case WINNER -> user.setVictories(user.getVictories() - 1);
                case LOSER -> user.setDefeats(user.getDefeats() - 1);
                case TIE -> user.setDraws(user.getDraws() - 1);
            }

            // Highest score recalculation --------------
            if (ms.getScore() == user.getHighestScore()) {
                System.out.println("Recalculating highest score for user: " + user.getUsername());

                // Query all remaining MatchScores for this user (AFTER this match)
                List<MatchScore> remainingScores =
                        matchScoreRepository.findByUserUserId(user.getUserId());

                if (remainingScores.isEmpty()) {
                    // User now has no remaining match history
                    user.setHighestScore(0);
                } else {
                    // Compute the maximum score from remaining match scores
                    int newHighest = remainingScores.stream()
                            .mapToInt(MatchScore::getScore)
                            .max()
                            .orElse(0);

                    user.setHighestScore(newHighest);
                }
            }
        }
        return affectedUsers;
    }


    private static User removeUserScoreStats(MatchScore ms) {
        User user = ms.getUser();

        // Subtract matches played
        user.setMatchesPlayed(user.getMatchesPlayed() - 1);

        // Subtract cumulative score
        user.setCumulativeScore(user.getCumulativeScore() - ms.getScore());

        // Reverse role-based stats
        switch (ms.getRole()) {
            case WINNER -> user.setVictories(user.getVictories() - 1);
            case LOSER -> user.setDefeats(user.getDefeats() - 1);
            case TIE -> user.setDraws(user.getDraws() - 1);
        }

        // Highest score consideration (just log for now)
        if (ms.getScore() == user.getHighestScore()) {
            System.out.println("Highest score removed for user " + user.getUsername()
                    + ". Recalculation needed in future.");
            // Future fix: recompute highest score across user's remaining MatchScores
        }

        return user;
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
