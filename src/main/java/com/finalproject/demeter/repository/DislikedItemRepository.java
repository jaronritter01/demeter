package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.DislikedItem;
import com.finalproject.demeter.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DislikedItemRepository extends JpaRepository<DislikedItem, Long> {
    Optional<List<DislikedItem>> findByUser(User user);

    @Query(
            value = "SELECT food_item_id FROM disliked_items m WHERE m.user_id = ?1",
            nativeQuery = true
    )
    Set<Long> findDislikedItemFoodIdsByUserId(Long userId);
}
