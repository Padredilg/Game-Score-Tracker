package com.cen4010.gamescoretracker.repositories;


import com.cen4010.gamescoretracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByGroupCode(String groupCode);

    boolean existsByUsername(String username);
}