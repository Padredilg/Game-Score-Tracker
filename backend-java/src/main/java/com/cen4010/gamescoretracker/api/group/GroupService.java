package com.cen4010.gamescoretracker.api.group;

import com.cen4010.gamescoretracker.api.group.dto.GroupDTO;
import com.cen4010.gamescoretracker.api.group.database.Group;
import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.api.group.database.GroupRepository;
import com.cen4010.gamescoretracker.api.user.database.UserRepository;
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

    public void joinGroup(User user, String groupCode) {
        Group group = groupRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new IllegalArgumentException("Group with code " + groupCode + " does not exist"));

        // Check if user is already in a group
        if (user.getGroup() != null) {
            throw new IllegalArgumentException("User is already part of a group");
        }

        // Link user to the group
        user.setGroup(group);
        user.setGroupCode(groupCode);

        // Add user to group's user set (optional, but good for bidirectional consistency)
        group.getUsers().add(user);

        // Save the updated user
        userRepository.save(user);

        log.info("User {} joined group {}", user.getUsername(), groupCode);
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


    private String generateUniqueGroupCode() {
        String code;
        do {
            code = random.ints(6, 0, ALLOWED.length())
                    .mapToObj(i -> String.valueOf(ALLOWED.charAt(i)))
                    .reduce("", String::concat);
        } while (groupRepository.existsByGroupCode(code));
        return code;
    }
}
