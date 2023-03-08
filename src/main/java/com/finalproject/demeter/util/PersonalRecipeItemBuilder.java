package com.finalproject.demeter.util;

import com.finalproject.demeter.dto.PersonalRecipeItem;

public class PersonalRecipeItemBuilder {
    private PersonalRecipeItem item = new PersonalRecipeItem();

    public PersonalRecipeItemBuilder foodItemId(long id) {
        item.setFoodItemId(id);
        return this;
    }

    public PersonalRecipeItemBuilder unit(String unit){
        item.setUnit(unit);
        return this;
    }

    public PersonalRecipeItemBuilder quantity(Float quantity){
        item.setQuantity(quantity);
        return this;
    }

    public PersonalRecipeItem build(){
        return item;
    }
}
