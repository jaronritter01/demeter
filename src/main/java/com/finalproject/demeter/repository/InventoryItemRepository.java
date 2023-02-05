package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findInventoryItemByUserId(User user);
}
