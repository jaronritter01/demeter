package com.finalproject.demeter.repository;

import com.finalproject.demeter.dao.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findFoodItemsByReusable(Boolean isResuable);

    FoodItem findFoodItemById(long id);
}
