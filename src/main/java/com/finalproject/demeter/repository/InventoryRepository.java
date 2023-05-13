package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findInventoryItemByUserId(User user);

    @Query(
            value = "SELECT food_id_id FROM inventory_items i WHERE i.user_id_id = ?1",
            nativeQuery = true
    )
    List<Long> getUserInventoryFoodItemIdsByUserId(long user_id);

    @Query(
            value = "SELECT food_id_id FROM inventory_items i WHERE i.user_id_id = ?1",
            nativeQuery = true
    )
    Set<Long> findInventoryItemsIdByUser(long user_id);
}
