package com.cen4010.gamescoretracker.api.group;

import com.cen4010.gamescoretracker.api.group.dto.GroupDTO;
import com.cen4010.gamescoretracker.api.group.database.Group;
import com.cen4010.gamescoretracker.api.group.dto.GroupEditNameRequest;
import com.cen4010.gamescoretracker.api.group.dto.GroupManageRequest;
import com.cen4010.gamescoretracker.api.group.dto.GroupVisibilityRequest;
import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.group.database.GroupRepository;
import com.cen4010.gamescoretracker.api.user.database.UserRepository;
import com.cen4010.gamescoretracker.utils.exceptions.ForbiddenAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private static final String ALLOWED = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    // Generate new group for new Admin user
    public void createGroupForAdmin(User adminUser) {
        String groupCode = generateUniqueGroupCode();

        Group group = Group.builder()
                .groupCode(groupCode)
                .groupName(adminUser.getUsername() + "’s Group")
                .admin(adminUser)
                .build();

        Group savedGroup = groupRepository.save(group);

        // Update admin user to link group
        adminUser.setGroup(savedGroup);
        adminUser.setGroupCode(groupCode);

        log.info("Group created: {} for admin {}", group.getGroupCode(), adminUser.getUsername());

        userRepository.save(adminUser);
    }

    // Regular user join group request
    public User joinGroup(User user, String groupCode) {
        Group group = groupRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new IllegalArgumentException("Group with code " + groupCode + " does not exist"));

        // Check if user is already in a group
        if (user.getGroup() != null) {
            throw new IllegalArgumentException("User is already part of a group");
        }

        //check if group is accepting new members
        if(!group.isOpenForNewMembers()){
            throw new ForbiddenAccessException("Group is not accepting new members at the moment.");
        }

        // Link user to the group
        user.setGroup(group);
        user.setGroupCode(groupCode);

        // Add user to group's user set (optional, but good for bidirectional consistency)
        group.getUsers().add(user);

        // Save the updated user
        return userRepository.save(user);
    }

    // Toggle column visibilities
    public Group toggleVisibility(User admin, GroupVisibilityRequest request) {
        Group group = requireAdminGroup(admin);

        //WinPercentageVisibility
        if (request.getWinPercentageVisibility() != null)
            group.setWinPercentageVisibility(request.getWinPercentageVisibility());
        //MatchesPlayedVisibility
        if (request.getMatchesPlayedVisibility() != null)
            group.setMatchesPlayedVisibility(request.getMatchesPlayedVisibility());
        //VictoriesVisibility
        if (request.getVictoriesVisibility() != null)
            group.setVictoriesVisibility(request.getVictoriesVisibility());
        //DefeatsVisibility
        if (request.getDefeatsVisibility() != null)
            group.setDefeatsVisibility(request.getDefeatsVisibility());
        //CumulativeScoreVisibility
        if (request.getCumulativeScoreVisibility() != null)
            group.setCumulativeScoreVisibility(request.getCumulativeScoreVisibility());
        //HighestScoreVisibility
        if (request.getHighestScoreVisibility() != null)
            group.setHighestScoreVisibility(request.getHighestScoreVisibility());


        groupRepository.save(group);
        return group;
    }

    // Edit group name
    public Group editGroupName(User admin, GroupEditNameRequest request) {
        Group group = requireAdminGroup(admin);

        group.setGroupName(request.getGroupName());
        groupRepository.save(group);
        return group;
    }

    // Open/close group for new members
    public Group manageGroup(User admin, GroupManageRequest request) {
        Group group = requireAdminGroup(admin);

        group.setOpenForNewMembers(request.getOpenForNewMembers());
        groupRepository.save(group);
        return group;
    }


    private Group requireAdminGroup(User user) {
        Group group = user.getGroup();
        if (group == null || !group.getAdmin().getUserId().equals(user.getUserId())) {
            throw new ForbiddenAccessException("User is not admin of the group");
        }
        return group;
    }

    private String generateUniqueGroupCode() {
        String code;
        do {
            code = random.ints(6, 0, ALLOWED.length())
                    .mapToObj(i -> String.valueOf(ALLOWED.charAt(i)))
                    .reduce("", String::concat);
        } while (groupRepository.existsByGroupCode(code));
        return code;
    }


    public List<GroupDTO> getAllGroupDTOs() {
        return groupRepository.findAll().stream()
                .map(group -> GroupDTO.builder()
                        .groupId(group.getGroupId())
                        .groupCode(group.getGroupCode())
                        .groupName(group.getGroupName())
                        .usernames(
                                group.getUsers().stream()
                                        .map(User::getUsername)
                                        .toList()
                        )
                        .build()
                )
                .toList();
    }

    public Group getGroupForUser(User user) {
        if (user.getGroup() == null) {
            throw new ForbiddenAccessException("User does not belong to a group");
        }
        return user.getGroup();
    }
}
