package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.FoodItem;

public class FoodItemBuilder {
    private FoodItem item = new FoodItem();

    public FoodItemBuilder id(long id){
        item.setId(id);
        return this;
    }

    public FoodItemBuilder name(String name){
        item.setName(name);
        return this;
    }

    public FoodItemBuilder description(String description){
        item.setDescription(description);
        return this;
    }

    public FoodItemBuilder reusable(boolean reusable){
        item.setReusable(reusable);
        return this;
    }

    public FoodItemBuilder picUrl(String picUrl){
        item.setPicUrl(picUrl);
        return this;
    }

    public FoodItem build() {
        return item;
    }
}
