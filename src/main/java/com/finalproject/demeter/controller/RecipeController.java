package com.finalproject.demeter.controller;

import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dto.PaginationSetting;
import com.finalproject.demeter.dto.RecipeQuery;
import com.finalproject.demeter.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/api/recipes")
public class RecipeController {
    private RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    List<Recipe> getAllRecipes(@RequestBody PaginationSetting pageSettings) {
        return recipeService.getAllRecipes(pageSettings);
    }

    @GetMapping("/getRecipeItems")
    public ResponseEntity<?> getRecipeItemsByRecipeId(@RequestParam Long id) {
        return recipeService.getRecipeItemsById(id);
    }
    @GetMapping("/getRecipe")
    public ResponseEntity<?> getRecipeById(@RequestParam Long id) {
        return recipeService.getRecipeById(id);
    }

    @PostMapping("/queryRecipes")
    public ResponseEntity<?> queryRecipes(@RequestBody HashMap<String, HashMap<String, String>> requestObject) {
        PaginationSetting pageSetting = recipeService.getPaginationSettings(requestObject);
        RecipeQuery query = recipeService.getRecipeQuery(requestObject);
        return recipeService.getQueriedRecipes(query, pageSetting);
    }
}
