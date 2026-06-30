package com.atharva.nutricheckai.repository;

import com.atharva.nutricheckai.entity.User;
import com.atharva.nutricheckai.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    Optional<UserProfile> findByUser(User user);
}
