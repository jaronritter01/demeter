package com.finalproject.demeter.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecipeUpload {
    private String recipeName;
    private String recipeDescription;
    private List<PersonalRecipeItem> ingredients;
}
