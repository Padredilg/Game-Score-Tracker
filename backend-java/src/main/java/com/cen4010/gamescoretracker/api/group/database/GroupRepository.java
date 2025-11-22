package com.cen4010.gamescoretracker.api.group.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    Optional<Group> findByGroupCode(String groupCode);

    boolean existsByGroupCode(String groupCode);
}