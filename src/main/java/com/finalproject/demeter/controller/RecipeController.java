package com.finalproject.demeter.controller;

import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dao.RecipeReview;
import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.dto.PaginationSetting;
import com.finalproject.demeter.dto.RecipeQuery;
import com.finalproject.demeter.dto.UpdateInventory;
import com.finalproject.demeter.dto.UpdateRecipeReview;
import com.finalproject.demeter.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

    @PostMapping("/recipeWithInventory")
    public ResponseEntity<?> getRecipesWithInventory(@RequestHeader("AUTHORIZATION") String jwt,
                                                     @RequestBody PaginationSetting paginationSetting) {
        List<Recipe> recipeList = recipeService.getRecipeWithInventory(jwt, paginationSetting);
        return new ResponseEntity(recipeList, HttpStatus.OK);
    }

    /**
     * endpoint for updating a recipeReview with an inputted RecipeReview item
     * @param reviewItem
     * @return ResponseEntity with error message or updated recipeReview
     */
    @PostMapping("/updateReview")
    public ResponseEntity<?> updateRecipeReview(@RequestBody UpdateRecipeReview reviewItem) {
        ResponseEntity response = recipeService.updateRecipeReview(reviewItem);
        if (response.getBody().equals("Review was saved")) {
            ResponseEntity<?> recipeReview = recipeService.getRecipeReview(reviewItem.getReviewId());
            return new ResponseEntity(recipeReview, HttpStatus.OK);
        }

        return response;
    }

    /**
     * endpoint for adding a new recipeReview with an inputted RecipeReview item
     * @param reviewItem
     * @return ResponseEntity with error message or new recipeReview
     */
    @PostMapping("/addReview")
    public ResponseEntity<?> addRecipeReview(@RequestBody RecipeReview reviewItem) {
        ResponseEntity response = recipeService.addRecipeReview(reviewItem);
        if (response.getBody().equals("Review was created")) {
            ResponseEntity<?> recipeReview = recipeService.getRecipeReview(reviewItem.getId());
            return new ResponseEntity(recipeReview, HttpStatus.OK);
        }

        return response;
    }
}
