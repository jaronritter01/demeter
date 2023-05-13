package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.MinorItem;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MinorItemRepository extends JpaRepository<MinorItem, Long> {
    List<MinorItem> findMinorItemsByUser(User user);

    @Query(
            value = "SELECT food_item_id FROM minor_items m WHERE m.user_id = ?1",
            nativeQuery = true
    )
    Set<Long> findMinorItemFoodIdsByUserId(Long userId);
}
