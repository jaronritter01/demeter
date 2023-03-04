package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;

public class InventoryItemBuilder {
    private InventoryItem item = new InventoryItem();

    public InventoryItemBuilder id(long id){
        item.setId(id);
        return this;
    }
    public InventoryItemBuilder userId(User user){
        item.setUserId(user);
        return this;
    }
    public InventoryItemBuilder foodItem(FoodItem foodItem){
        item.setFoodId(foodItem);
        return this;
    }
    public InventoryItemBuilder quantity(Float quantity){
        item.setQuantity(quantity);
        return this;
    }
    public InventoryItemBuilder unit(String units){
        item.setUnit(units);
        return this;
    }
    public InventoryItem build() {
        return item;
    }
}
