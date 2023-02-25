package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.Recipe;

public class RecipeBuilder {
    private Recipe recipe = new Recipe();

    public RecipeBuilder id(long id){
        recipe.setId(id);
        return this;
    }

    public RecipeBuilder name(String name) {
        recipe.setName(name);
        return this;
    }

    public RecipeBuilder description(String desc){
        recipe.setDescription(desc);
        return this;
    }

    public RecipeBuilder cookTime(Integer time) {
        recipe.setCookTime(time);
        return this;
    }

    public Recipe build() {
        return recipe;
    }
}
