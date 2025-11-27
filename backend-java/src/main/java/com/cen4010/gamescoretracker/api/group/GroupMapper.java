package com.cen4010.gamescoretracker.api.group;

import com.cen4010.gamescoretracker.api.group.database.Group;
import com.cen4010.gamescoretracker.api.group.dto.GroupDTO;
import com.cen4010.gamescoretracker.api.user.database.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class GroupMapper {

    public static GroupDTO toDTO(Group group) {
        if (group == null) return null;

        return GroupDTO.builder()
                .groupId(group.getGroupId())
                .groupCode(group.getGroupCode())
                .groupName(group.getGroupName())
                .usernames(group.getUsers()
                        .stream()
                        .map(User::getUsername)
                        .collect(Collectors.toList()))
                .openForNewMembers(group.isOpenForNewMembers())
                .winPercentageVisibility(group.isWinPercentageVisibility())
                .matchesPlayedVisibility(group.isMatchesPlayedVisibility())
                .victoriesVisibility(group.isVictoriesVisibility())
                .defeatsVisibility(group.isDefeatsVisibility())
                .cumulativeScoreVisibility(group.isCumulativeScoreVisibility())
                .highestScoreVisibility(group.isHighestScoreVisibility())
                .build();
    }
}
