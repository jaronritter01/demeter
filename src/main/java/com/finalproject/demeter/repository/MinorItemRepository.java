package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.MinorItem;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MinorItemRepository extends JpaRepository<MinorItem, Long> {
    List<MinorItem> findMinorItemsByUser(User user);
}
