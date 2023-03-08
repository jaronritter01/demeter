package com.finalproject.demeter.util;

import com.finalproject.demeter.dao.PersonalRecipe;
import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dao.User;

public class PersonalRecipeBuilder {
    private PersonalRecipe personalRecipe = new PersonalRecipe();

    public PersonalRecipeBuilder id(long id){
        personalRecipe.setId(id);
        return this;
    }

    public PersonalRecipeBuilder user(User user){
        personalRecipe.setUser(user);
        return this;
    }

    public PersonalRecipeBuilder recipe(Recipe recipe) {
        personalRecipe.setRecipe(recipe);
        return this;
    }

    public PersonalRecipe build() {
        return personalRecipe;
    }
}
