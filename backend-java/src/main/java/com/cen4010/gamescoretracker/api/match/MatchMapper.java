package com.cen4010.gamescoretracker.api.match;

import com.cen4010.gamescoretracker.api.match.database.Match;
import com.cen4010.gamescoretracker.api.match.database.MatchScore;
import com.cen4010.gamescoretracker.api.match.dto.MatchReportDTO;
import com.cen4010.gamescoretracker.api.match.dto.UserMatchesDTO;
import com.cen4010.gamescoretracker.api.user.UserMapper;
import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.user.dto.UserDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MatchMapper {

    public static MatchReportDTO toReportDto(
            Match match,
            List<MatchScore> scores,
            List<User> updatedUsers
    ) {

        return MatchReportDTO.builder()
                .match(toMatchDto(match))
                .scores(scores.stream()
                        .map(MatchMapper::toScoreDto)
                        .collect(Collectors.toList()))
                .updatedUsers(updatedUsers.stream()
                        .map(UserMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private static MatchReportDTO.MatchDTO toMatchDto(Match m) {
        return MatchReportDTO.MatchDTO.builder()
                .matchId(m.getMatchId())
                .matchDate(m.getMatchDate())
                .result(m.getResult().name())
                .createdBy(m.getCreatedBy() != null ? m.getCreatedBy().getUserId() : null)
                .createdByUsername(m.getCreatedBy() != null ? m.getCreatedBy().getUsername() : null)
                .groupId(m.getGroup().getGroupId())
                .groupName(m.getGroup().getGroupName())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private static MatchReportDTO.MatchScoreDTO toScoreDto(MatchScore s) {
        return MatchReportDTO.MatchScoreDTO.builder()
                .matchScoreId(s.getMatchScoreId())
                .userId(s.getUser().getUserId())
                .username(s.getUser().getUsername())
                .score(s.getScore())
                .role(s.getRole().name())
                .build();
    }

    public static UserMatchesDTO.UserMatchItemDTO toUserMatchItemDTO(
            Match match,
            UUID targetUserId,
            List<MatchScore> allScores
    ) {

        // Find this user's specific row
        MatchScore myScore = allScores.stream()
                .filter(ms -> ms.getUser().getUserId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User did not participate in this match"));

        // Build participant list with full match context
        List<UserMatchesDTO.MatchUserEntryDTO> participants = allScores.stream()
                .map(ms -> UserMatchesDTO.MatchUserEntryDTO.builder()
                        .userId(ms.getUser().getUserId())
                        .username(ms.getUser().getUsername())
                        .score(ms.getScore())
                        .role(ms.getRole().name())
                        .build()
                )
                .toList();

        return UserMatchesDTO.UserMatchItemDTO.builder()
                .matchId(match.getMatchId())
                .matchDate(match.getMatchDate())
                .result(match.getResult().name())
                .playerRole(myScore.getRole().name())  // THIS user's role
                .participants(participants)
                .build();
    }


}

