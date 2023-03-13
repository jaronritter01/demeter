package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.dao.MinorItem;
import com.finalproject.demeter.dao.User;

public class MinorItemBuilder {
    private MinorItem item = new MinorItem();

    public MinorItemBuilder id (long id) {
        item.setId(id);
        return this;
    }
    public MinorItemBuilder user (User user) {
        item.setUser(user);
        return this;
    }

    public MinorItemBuilder foodItem (FoodItem foodItem) {
        item.setFoodItem(foodItem);
        return this;
    }

    public MinorItem build () {
        return item;
    }
}
