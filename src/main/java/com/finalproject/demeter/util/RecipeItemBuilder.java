package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dao.RecipeItem;

public class RecipeItemBuilder {
    private RecipeItem item = new RecipeItem();

    public RecipeItemBuilder id(long id){
        item.setId(id);
        return this;
    }

    public RecipeItemBuilder foodItem(FoodItem foodItem){
        item.setFoodItem(foodItem);
        return this;
    }

    public RecipeItemBuilder recipe(Recipe recipe){
        item.setRecipe(recipe);
        return this;
    }

    public RecipeItemBuilder measurementUnit(String unit) {
        item.setMeasurementUnit(unit);
        return this;
    }

    public RecipeItemBuilder quantity(Float quantity) {
        item.setQuantity(quantity);
        return this;
    }

    public RecipeItem build() {
        return item;
    }
}
