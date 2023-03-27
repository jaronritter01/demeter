package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.DislikedItem;
import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.dao.User;

public class DislikedItemBuilder {
    private DislikedItem dislikedItem = new DislikedItem();

    public DislikedItemBuilder id(long id){
        dislikedItem.setId(id);
        return this;
    }

    public DislikedItemBuilder user(User user){
        dislikedItem.setUser(user);
        return this;
    }

    public DislikedItemBuilder foodItem(FoodItem foodItem){
        dislikedItem.setFoodItem(foodItem);
        return this;
    }

    public DislikedItem build() {
        return dislikedItem;
    }
}
