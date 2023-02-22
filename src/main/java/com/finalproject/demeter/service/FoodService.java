package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {
    private FoodItemRepository foodItemRepository;

    @Autowired
    public FoodService (FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }
}
