package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.dao.Substitution;
import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.repository.FoodItemRepository;
import com.finalproject.demeter.repository.InventoryRepository;
import com.finalproject.demeter.repository.SubstitutionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodService {
    private FoodItemRepository foodItemRepository;
    private SubstitutionsRepository substitutionsRepository;
    private InventoryRepository inventoryRepository;

    @Autowired
    public FoodService (FoodItemRepository foodItemRepository, InventoryRepository inventoryRepository,
                        SubstitutionsRepository substitutionsRepository) {
        this.foodItemRepository = foodItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.substitutionsRepository = substitutionsRepository;
    }

    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    /**
     * This function uses a user and missingFoodItemId to determine what substitutions are
     * available for a foodItem and whether it is in the users inventory.
     * @param user
     * @param missingFoodItemId
     * @return - empty array or list of 1 or more possible subsitutions
     */
    public List<FoodItem> getSubItems(User user, long missingFoodItemId) {
        List<Substitution> possibleSubs = substitutionsRepository.findByMissingItemId(missingFoodItemId);
        List<Long> foodItemIds = inventoryRepository.getFoodItemsByUserId(user.getId());
        List<FoodItem> foundSubFoodItems = new ArrayList<>();

        try {
            for (Substitution sub : possibleSubs) {
                if (foodItemIds.contains(sub.getId())) {
                    foundSubFoodItems.add(foodItemRepository.findFoodItemById(sub.getId()));
                }
            }
        } catch(Exception e) {
            return new ArrayList<>();
        }

        return foundSubFoodItems;
    }
}
