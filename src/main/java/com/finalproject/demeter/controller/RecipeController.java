package com.finalproject.demeter.controller;

import com.finalproject.demeter.dao.Recipe;
import com.finalproject.demeter.dao.RecipeReview;
import com.finalproject.demeter.dto.*;
import com.finalproject.demeter.service.RecipeService;
import org.springframework.http.HttpStatus;
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

    /**
     * Used to get all the recipes for a given page
     * @param pageSettings - pagination settings a user has setup.
     * @return A ResponseEntity that contains a list of recipes
     * */
    @PostMapping
    List<Recipe> getAllRecipes(@RequestBody PaginationSetting pageSettings) {
        return recipeService.getAllRecipes(pageSettings);
    }

    /**
     * Used to add a recipes to a users favorite list.
     * @param jwt: Token needed to authenticate a user.
     * @param recipeId: ID of the recipe the user wants to add.
     * @return Response Entity that is bubbled up from the service layer.
     * */
    @PostMapping("/addFavoriteRecipes")
    public ResponseEntity<?> addFavoriteRecipe(@RequestHeader("AUTHORIZATION") String jwt, @RequestBody Long recipeId) {
        return recipeService.addFavoriteRecipe(jwt, recipeId);
    }

    /**
     * Used to remove a recipes to a users favorite list.
     * @param jwt: Token needed to authenticate a user.
     * @param recipeId: ID of the recipe the user wants to remove.
     * @return Response Entity that is bubbled up from the service layer.
     * */
    @PostMapping("/removeFavoriteRecipes")
    public ResponseEntity<?> removeFavoriteRecipe(@RequestHeader("AUTHORIZATION") String jwt, @RequestBody Long recipeId) {
        return recipeService.removeFavoriteRecipe(jwt, recipeId);
    }

    /**
     * Used to retrieve the favorite list for a user.
     * @param jwt: Token needed to authenticate a user.
     * @return Response Entity that is bubbled up from the service layer.
     * */
    @PostMapping("/getFavoriteRecipes")
    public ResponseEntity<?> getFavoriteRecipe(@RequestHeader("AUTHORIZATION") String jwt) {
        return recipeService.getFavoriteRecipe(jwt);
    }

    /**
     * Used to get all the recipes items for a given recipe id
     * @param id - recipeId
     * @param jwt - Token needed to authenticate a user.
     * @return A ResponseEntity that contains a list of recipes items
     * */
    @GetMapping("/getRecipeItems")
    public ResponseEntity<?> getRecipeItemsByRecipeId(@RequestParam Long id, @RequestHeader("AUTHORIZATION") String jwt)
    {
        return recipeService.getRecipeItemsById(id, jwt);
    }

    /**
     * Used to get all a recipe by its id
     * @param id - recipeId
     * @return A ResponseEntity that contains a recipe
     * */
    @GetMapping("/getRecipe")
    public ResponseEntity<?> getRecipeById(@RequestParam Long id) {
        return recipeService.getRecipeById(id);
    }

    /**
     * Used to add a personal recipe for a user
     * @param jwt - Token needed to authenticate a user.
     * @param newRecipe - dto RecipeUpload with a recipeName, description, and ingredient list
     * @return A ResponseEntity that contains the status of the operation
     * */
    @PostMapping("/uploadPersonalRecipe")
    public ResponseEntity<?> uploadPersonalRecipe(@RequestHeader("AUTHORIZATION") String jwt,
                                                  @RequestBody RecipeUpload newRecipe){
        return recipeService.uploadPersonalRecipe(jwt, newRecipe);
    }

    /**
     * Used to remove a personal recipe for a user
     * @param jwt - Token needed to authenticate a user.
     * @param recipeId - recipeId of a personalRecipe
     * @return A ResponseEntity that contains the status of the operation
     * */
    @PostMapping("/removePersonalRecipe")
    public ResponseEntity<?> removePersonalRecipe(@RequestHeader("AUTHORIZATION") String jwt,
                                                  @RequestBody Long recipeId){
        return recipeService.removePersonalRecipe(jwt, recipeId);
    }

    /**
     * Used to get a list of a users personal recipes
     * @param jwt - Token needed to authenticate a user.
     * @return A ResponseEntity that contains a list of recipes created by the user
     * */
    @PostMapping("/getPersonalRecipes")
    public ResponseEntity<?> getPersonalRecipes(@RequestHeader("AUTHORIZATION") String jwt){
        return recipeService.getPersonalRecipes(jwt);
    }

    /**
     * Used to publish a recipe - adds it to public recipes and removes from personalRecipes
     * @param jwt - Token needed to authenticate a user.
     * @param recipeId - id of personalRecipe to publish
     * @return A ResponseEntity that contains the status of the operation
     * */
    @PostMapping("/publishPersonalRecipe")
    public ResponseEntity<?> publishPersonalRecipe(@RequestHeader("AUTHORIZATION") String jwt,
                                                   @RequestBody long recipeId) {
        return recipeService.publishPersonalRecipe(jwt, recipeId);
    }

    /**
     * Used to get recipes from the database
     * @param requestObject - the object that contains the recipe query data.
     * @return: A ResponseEntity entity with queried recipes
     * */
    @PostMapping("/queryRecipes")
    public ResponseEntity<?> queryRecipes(@RequestBody HashMap<String, HashMap<String, String>> requestObject) {
        PaginationSetting pageSetting = recipeService.getPaginationSettings(requestObject);
        RecipeQuery query = recipeService.getRecipeQuery(requestObject);
        return recipeService.getQueriedRecipes(query, pageSetting);
    }

    /**
     * Used to get recipes based on a users inventory
     * @param jwt - Token needed to authenticate a user.
     * @param paginationSetting - page settings.
     * @return: A ResponseEntity that contains a list of recipes
     * */
    @PostMapping("/recipeWithInventory")
    public ResponseEntity<?> getRecipesWithInventory(@RequestHeader("AUTHORIZATION") String jwt,
                                                     @RequestBody PaginationSetting paginationSetting) {
        List<RecipeWithSub> recipeList = recipeService.getRecipeWithInventory(jwt, paginationSetting);
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
        if (response.getBody().equals("Review was updated")) {
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
    public ResponseEntity<?> addRecipeReview(@RequestHeader("AUTHORIZATION") String jwt, @RequestBody AddRecipeReview reviewItem) {
        RecipeReview recipeReview = new RecipeReview();
        ResponseEntity response = recipeService.addRecipeReview(jwt, reviewItem, recipeReview);
        if (response.getBody().equals("Review was created")) {
            ResponseEntity<?> newRecipeReview = recipeService.getRecipeReview(recipeReview.getId());
            return new ResponseEntity(newRecipeReview, HttpStatus.OK);
        }

        return response;
    }

    /**
     * This is used to get all the reviews for a recipe
     * @param id - recipe id
     * @return list of recipe reviews
     */
    @GetMapping("/getRecipeReviews")
    public ResponseEntity<?> getRecipeReviewByRecipeId(@RequestParam Long id) {
        return recipeService.getRecipeReviewByRecipeId(id);
    }
}
