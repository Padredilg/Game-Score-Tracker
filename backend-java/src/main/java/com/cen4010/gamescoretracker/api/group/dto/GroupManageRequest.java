package com.cen4010.gamescoretracker.api.group.dto;

import lombok.Data;

@Data
public class GroupManageRequest {
    private Boolean openForNewMembers; // true = open, false = closed
}