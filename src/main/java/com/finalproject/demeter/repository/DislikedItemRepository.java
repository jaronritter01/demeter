package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.DislikedItem;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DislikedItemRepository extends JpaRepository<DislikedItem, Long> {
    Optional<List<DislikedItem>> findByUser(User user);
}
