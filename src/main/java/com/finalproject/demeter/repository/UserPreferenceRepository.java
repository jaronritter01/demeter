package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.dao.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUser(User user);
}
