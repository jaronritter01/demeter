package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.FavoriteRecipe;
import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dao.User;

public class FavoriteRecipeBuilder {
    private final FavoriteRecipe favoriteRecipe = new FavoriteRecipe();

    public FavoriteRecipeBuilder id(long id) {
        favoriteRecipe.setId(id);
        return this;
    }

    public FavoriteRecipeBuilder user(User user){
        favoriteRecipe.setUser(user);
        return this;
    }

    public FavoriteRecipeBuilder recipe(Recipe recipe){
        favoriteRecipe.setRecipe(recipe);
        return this;
    }

    public FavoriteRecipe build(){
        return favoriteRecipe;
    }
}
