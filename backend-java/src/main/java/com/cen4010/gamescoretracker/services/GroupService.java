package com.cen4010.gamescoretracker.services;

import com.cen4010.gamescoretracker.dto.group.GroupDTO;
import com.cen4010.gamescoretracker.models.Group;
import com.cen4010.gamescoretracker.models.User;
import com.cen4010.gamescoretracker.repositories.GroupRepository;
import com.cen4010.gamescoretracker.repositories.UserRepository;
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
