package com.finalproject.demeter.controller;

import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dto.RecipeQuery;
import com.finalproject.demeter.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/recipes")
public class RecipeController {
    private RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @PostMapping("/queryRecipes")
    ResponseEntity<?> queryRecipes(@RequestBody RecipeQuery query) {
        return recipeService.getQueriedRecipes(query);
    }
}
