package com.cen4010.gamescoretracker.repositories;

import com.cen4010.gamescoretracker.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    Optional<Group> findByGroupCode(String groupCode);

    boolean existsByGroupCode(String groupCode);
}