package com.cen4010.gamescoretracker.api.group.dto;

import com.cen4010.gamescoretracker.api.user.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupDetailsResponse {
    private GroupDTO group;
    private List<UserDTO> members;
}
