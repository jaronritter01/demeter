package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.*;
import com.finalproject.demeter.dto.*;
import com.finalproject.demeter.repository.*;
import com.finalproject.demeter.util.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Service is used for all functions that deal with Recipes
 * */

@Service
public class RecipeService {
    private RecipeRepository recipeRepository;
    private RecipeItemRepository recipeItemRepository;
    private RecipeRatingRepository recipeRatingRepository;
    private FoodItemRepository foodItemRepository;
    private PersonalRecipeRepository personalRecipeRepository;
    private UserService userService;
    private DislikedItemRepository dislikedItemRepository;
    private MinorItemRepository minorItemRepository;
    private FavoriteRecipeRepository favoriteRecipeRepository;
    private FoodService foodService;
    private final Pattern SPECIALCHARREGEX = Pattern.compile("[$&+:;=?@#|<>.^*()%!]");
    private final Logger LOGGER = LoggerFactory.getLogger(RecipeService.class);
    private static final PaginationSetting DEFAULT_PAGE = new PaginationSettingBuilder()
            .pageNumber(0)
            .pageSize(5)
            .build();

    private static final RecipeQuery DEFAULT_QUERY = new RecipeQueryBuilder()
            .method("default")
            .value("default")
            .build();

    private static final Recipe DEFAULT_RECIPE = new RecipeBuilder()
            .id(0)
            .name("Default Recipe")
            .description("Default Description")
            .avgRating(0F)
            .reviewCount(0L)
            .isPublic(true)
            .build();

    // Usually you should not do this
    private static final List<Recipe> DEFAULT_RECIPE_LIST = new ArrayList<>(){{
        add(DEFAULT_RECIPE);
    }};

    enum QueryMethod {
        NAME,
        DESC,
        DEFAULT
    }

    private final Map<String, QueryMethod> queryMap = new MapBuilder<String, QueryMethod>()
            .put("desc", QueryMethod.DESC)
            .put("name", QueryMethod.NAME)
            .put("default", QueryMethod.DEFAULT)
            .build();

    public RecipeService(RecipeRepository recipeRepository, RecipeItemRepository recipeItemRepository,
                         RecipeRatingRepository recipeRatingRepository, UserService userService,
                         FoodItemRepository foodItemRepository, PersonalRecipeRepository personalRecipeRepository,
                         DislikedItemRepository dislikedItemRepository, MinorItemRepository minorItemRepository,
                         FavoriteRecipeRepository favoriteRecipeRepository, FoodService foodService) {
        this.recipeRepository = recipeRepository;
        this.recipeItemRepository = recipeItemRepository;
        this.recipeRatingRepository = recipeRatingRepository;
        this.userService = userService;
        this.foodItemRepository = foodItemRepository;
        this.personalRecipeRepository = personalRecipeRepository;
        this.dislikedItemRepository = dislikedItemRepository;
        this.minorItemRepository = minorItemRepository;
        this.favoriteRecipeRepository = favoriteRecipeRepository;
        this.foodService = foodService;
    }

    /**
     * Used to allow users to mark Recipes as favorites for easy access.
     * @param jwt: The users JWT
     * @param recipeId: Id of the recipe the user wants to favorite
     * @return A response entity indicating the status of the operation.
     * */
    public ResponseEntity<?> addFavoriteRecipe(String jwt, Long recipeId) {
        Optional<User> userOpt = userService.getUserFromJwtToken(jwt);
        if (userOpt.isPresent()) {
            Optional<Recipe> recipeOpt = recipeRepository.findById(recipeId);
            if (recipeOpt.isPresent()){
                FavoriteRecipe favoriteRecipe = new FavoriteRecipeBuilder()
                        .user(userOpt.get()).recipe(recipeOpt.get()).build();
                favoriteRecipeRepository.save(favoriteRecipe);
                return new ResponseEntity<>("Favorite saved", HttpStatus.OK);
            }
            return new ResponseEntity<>("Recipe not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);
    }

    /**
     * Used to remove a favorited item for a user.
     * @param jwt: User JWT
     * @param recipeId: id of the recipe to be removed
     * @return ResponseEntity indicating the status of the operation.
     * */
    public ResponseEntity<?> removeFavoriteRecipe(String jwt, Long recipeId){
        Optional<User> userOpt = userService.getUserFromJwtToken(jwt);
        if (userOpt.isPresent()) {
            Optional<List<FavoriteRecipe>> favoriteRecipes = favoriteRecipeRepository.findByUser(userOpt.get());
            if (favoriteRecipes.isPresent()){
                for (FavoriteRecipe favoriteRecipe : favoriteRecipes.get()){
                    if (favoriteRecipe.getRecipe().getId() == recipeId) {
                        favoriteRecipeRepository.delete(favoriteRecipe);
                        return new ResponseEntity<>("Removal Successful", HttpStatus.OK);
                    }
                }
                return new ResponseEntity<>("Recipe to remove was not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("No Recipes to Remove", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);
    }

    /**
     * Used to retrieve a users favorited recipes.
     * @param jwt: User JWT
     * @return ResponseEntity indicating the status of the operation.
     * */
    public ResponseEntity<?> getFavoriteRecipe(String jwt){
        Optional<User> userOpt = userService.getUserFromJwtToken(jwt);
        if (userOpt.isPresent()) {
            Optional<List<FavoriteRecipe>> favoriteRecipes = favoriteRecipeRepository.findByUser(userOpt.get());
            if (favoriteRecipes.isPresent()){
                return new ResponseEntity<>(favoriteRecipes.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);
    }

    /**
     * Get a recipe list that takes into account the inventory of a given user.
     * @param jwtToken: The user's jwt
     * @param pageSettings: The pagination setting for the given request
     * @return A list of reicpes that can be made user's given inventory.
     * */
    public List<RecipeWithSub> getRecipeWithInventory(String jwtToken, PaginationSetting pageSettings) {
        // This will likely need to be optimized.
        if (pageSettings.getPageSize() <= 0 || pageSettings.getPageNumber() < 0) {
            return new ArrayList<>();
        }

        List<Recipe> recipeList = recipeRepository.findAllPublic();
        Optional<User> user = userService.getUserFromJwtToken(jwtToken);
        List<InventoryItem> userInventory = null;
        Optional<List<DislikedItem>> userPreferences = Optional.empty();
        Optional<List<MinorItem>> userMinorItems = Optional.empty();

        if (user.isPresent()) {
            userInventory = userService.getInventory(user.get());
            userPreferences = dislikedItemRepository.findByUser(user.get());
            userMinorItems = Optional.ofNullable(minorItemRepository.findMinorItemsByUser(user.get()));
        }

        if (userInventory != null) {
            List<RecipeWithSub> returnList = new ArrayList<>();
            RecipeWithSub recipeWithSub;
            for (Recipe recipe : recipeList) {
                recipeWithSub = new RecipeWithSub();
                Optional<List<RecipeItem>> recipeItems = recipeItemRepository.findRecipeItemsByRecipe(recipe);
                if (recipeItems.isPresent()) {
                    // check to see what recipes can be made or item subbed
                    if (canRecipeBeMade(userInventory, recipeItems.get(), userPreferences, userMinorItems, user.get(),
                            recipeWithSub)) {
                        setRecipeRatings(recipe);
                        recipeWithSub.setRecipe(recipe);
                        returnList.add(recipeWithSub);
                    }
                }
            }
            // Return the matched list
            // Need to perform a bound check to make sure to not get array out of bounds
            int startIndex = pageSettings.getPageNumber() * pageSettings.getPageSize();
            int endIndex = startIndex + pageSettings.getPageSize();

            if (startIndex >= recipeList.size()) {
                return new ArrayList<>();
            }

            if (endIndex > returnList.size()) {
                endIndex = returnList.size();
            }
            return returnList.subList(startIndex, endIndex);
        }

        return new ArrayList<>();
    }

    /**
     * This is used to get all the personal recipes associated with a user.
     * @param jwt: The JWT for a user.
     * @return A response Entity that shows the status of the operation.
     * */
    public ResponseEntity<?> getPersonalRecipes(String jwt){
        Optional<User> user = userService.getUserFromJwtToken(jwt);
        List<Recipe> recipeList = new ArrayList<>();
        user.ifPresent(value -> personalRecipeRepository.findByUser(value).ifPresent(personalRecipeList -> {
            personalRecipeList.forEach(recipe -> {
                Recipe unfinishedRecipe = recipe.getRecipe();
                setRecipeRatings(unfinishedRecipe);
                recipeList.add(unfinishedRecipe);
            });
        }));

        if (recipeList.size() == 0) {
            return new ResponseEntity<>("No Recipes Found", HttpStatus.OK);
        }
        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    /**
     * This is used to enable users to create personal recipes.
     * @param jwtToken: jwt for the user
     * @param personalRecipe: DTO that allows users to create a recipe
     * @return Response Entity that shows the status of the operation
     * */
    @Transactional
    public ResponseEntity<?> uploadPersonalRecipe(String jwtToken, RecipeUpload personalRecipe) {
        Optional<User> userOpt = userService.getUserFromJwtToken(jwtToken);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<PersonalRecipeItem> ingredientList = personalRecipe.getIngredients();
            //TODO: Convert Recipe
            if (ingredientList.size() == 0){
                return new ResponseEntity<>("No Ingredients were passed", HttpStatus.BAD_REQUEST);
            }
            // Create the new recipe
            Recipe newRecipe = new RecipeBuilder()
                    .name(personalRecipe.getRecipeName())
                    .description(personalRecipe.getRecipeDescription())
                    .isPublic(false).build();

            // Set up for the save of the items
            List<FoodItem> recipeItemList = new ArrayList<>();
            boolean missingIngredient = false;
            for (PersonalRecipeItem recipeItem : ingredientList) {
                // This could be optimized with the introduction of a cache
                Optional<FoodItem> itemOpt = foodItemRepository.findById(recipeItem.getFoodItemId());
                if (itemOpt.isPresent()){
                    recipeItemList.add(itemOpt.get());
                } else {
                    missingIngredient = true;
                    break;
                }
            }

            // If you can't find an ingredient
            if (missingIngredient) {
                return new ResponseEntity<>("An ingredient is missing", HttpStatus.NOT_FOUND);
            }

            // Save the recipe
            Recipe savedRecipe = recipeRepository.save(newRecipe);

            // If all the ingredients are present
            for (int i = 0; i < recipeItemList.size(); i++){
                if (ingredientList.get(i).getQuantity() < 1) {
                    return new ResponseEntity<>("Invalid Recipe Quantity", HttpStatus.BAD_REQUEST);
                }
                // Create a recipe Item from the user input, created recipe, and found foodItem
                RecipeItem newRecipeItem = new RecipeItemBuilder()
                        .foodItem(foodItemRepository.findById(recipeItemList.get(i).getId()).get())
                        .recipe(savedRecipe).measurementUnit(ingredientList.get(i).getUnit())
                        .quantity(ingredientList.get(i).getQuantity())
                        .build();
                // Save the item
                recipeItemRepository.save(newRecipeItem);
            }

            // Save the (user, recipe) combo in the personal recipe table
            PersonalRecipe personalRecipeSave = new PersonalRecipeBuilder().user(user).recipe(savedRecipe).build();
            personalRecipeRepository.save(personalRecipeSave);

            return new ResponseEntity<>("Recipe Successfully saved", HttpStatus.OK);
        }

        return new ResponseEntity<>("User was not Found", HttpStatus.NOT_FOUND);
    }

    /**
     * Used to remove personal recipes from the users profile
     * @param jwtToken: users jwt
     * @param RecipeId: id of the personal recipe to be removed
     * @return ResponseEntity indicating the status of the operation
     * */
    @Transactional
    public ResponseEntity<?> removePersonalRecipe(String jwtToken, Long RecipeId){
        //Find the user
        Optional<User> userOpt = userService.getUserFromJwtToken(jwtToken);
        if (userOpt.isEmpty()){
            return new ResponseEntity<>("User cannot be found", HttpStatus.NOT_FOUND);
        }
        //Find recipe
        Optional<Recipe> recipeOpt = recipeRepository.findById(RecipeId);
        if (recipeOpt.isEmpty()) {
            return new ResponseEntity<>("Recipe cannot be found", HttpStatus.NOT_FOUND);
        }
        //Find the personal recipe where user = found user and recipe = found recipe
        Optional<PersonalRecipe> personalRecipeOpt = personalRecipeRepository.findByUserAndRecipe(
                    userOpt.get().getId(), recipeOpt.get().getId());
        if(personalRecipeOpt.isEmpty()) {
            return new ResponseEntity<>("Personal Recipe cannot be found", HttpStatus.NOT_FOUND);
        }

        PersonalRecipe personalRecipe = personalRecipeOpt.get();

        //Remove personal Recipe entry
        personalRecipeRepository.delete(personalRecipe);
        //Remove the recipe items whose recipe id is that of the to be deleted recipe
        recipeItemRepository.deleteRecipeItemsByRecipe(recipeOpt.get());
        //Remove the recipe
        recipeRepository.delete(recipeOpt.get());

        return new ResponseEntity<>("Personal Recipe Successfully Removed", HttpStatus.OK);
    }

    /**
     * This checks a recipe against a user recipe to see if the recipe can be made.
     * @param userInventory: A given user's inventory.
     * @param recipeItems: The items required for a given recipe.
     * @param userPreferences: Optional including the disliked items of a user.
     * @return A boolean representing if a recipe can be made with the current ingredients a user has.
     * */
    private boolean canRecipeBeMade(List<InventoryItem> userInventory, List<RecipeItem> recipeItems,
                                    Optional<List<DislikedItem>> userPreferences,
                                    Optional<List<MinorItem>> minorItems,
                                    User user, RecipeWithSub recipeWithSub) {
        if (recipeItems.size() == 0) {
            return false;
        }

        boolean containsSubItem = false;
        for (RecipeItem recipeItem : recipeItems) {
            long currentFoodItemId = recipeItem.getFoodItem().getId();
            Float recipeQuantity = recipeItem.getQuantity();
            // Loop through inventory to see if it exists
            boolean found = false;
            for (InventoryItem inventoryItem : userInventory) {
                long currentInventoryItemId = inventoryItem.getFoodId().getId();
                Float inventoryItemQuantity = inventoryItem.getQuantity();
                if (currentFoodItemId == currentInventoryItemId && inventoryItemQuantity >= recipeQuantity) {
                    // If the user has preferences
                    if (userPreferences.isPresent()){
                        // If the items is not a disliked on, we can say the user has the item
                        if (isNotDislikedItem(recipeItem.getFoodItem(), userPreferences.get())){
                            found = true;
                            break;
                        }
                    }
                    // If they do not
                    else {
                        found = true;
                        break;
                    }
                // check if a sub item already exists for the recipe or if the item is a minor item
                } else if (!containsSubItem &&
                        (minorItems.isEmpty() || !isAMinorItem(recipeItem.getFoodItem(), minorItems.get()))) {
                    // if there are no other sub items and this item is not minor
                    Optional<List<FoodItem>> foodItemList = Optional.ofNullable(foodService.getSubItems(user, currentFoodItemId));
                    if (foodItemList.isPresent() && foodItemList.get().size() > 0) {
                        // mark item as subbed and add isSubbed to recipe
                        containsSubItem = true;
                        recipeWithSub.setIsSubbed(true);
                        recipeWithSub.setSubbedId(foodItemList.get().get(0).getId());
                        recipeWithSub.setFoodIdToReplace(currentFoodItemId);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                // If the user has no minor items
                if (minorItems.isEmpty()) {
                    return false;
                }

                // if the user does not have this item labeled as minor
                if (!isAMinorItem(recipeItem.getFoodItem(), minorItems.get())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isAMinorItem(FoodItem foodItem, List<MinorItem> minorItems) {
        for (MinorItem minorItem : minorItems) {
            if (foodItem.getId() == minorItem.getFoodItem().getId()){
                return true;
            }
        }
        return false;
    }

    private boolean isNotDislikedItem(FoodItem foodItem, List<DislikedItem> dislikedItems) {
        for (DislikedItem dislikedItem : dislikedItems){
            // If the food item has the same id, it has been marked as disliked
            if (foodItem.getId() == dislikedItem.getFoodItem().getId()){
                return false;
            }
        }
        // If the food item was not found in the users disliked items
        return true;
    }

    private void setRecipeRatings (Recipe recipe){
        Optional<Long> count = recipeRatingRepository.countByRecipeId(recipe.getId());
        count.ifPresentOrElse(
                recipe::setReviewCount,
                () -> recipe.setReviewCount(0L)
        );
        Optional<Float> rating = recipeRatingRepository.getAverageReviewByRecipeId(recipe.getId());
        rating.ifPresentOrElse(
                recipe::setAvgRating,
                () -> recipe.setAvgRating(0F)
        );
    }

    public List<Recipe> getAllRecipes(PaginationSetting pageSettings) {
        Pageable page = PageRequest.of(pageSettings.getPageNumber(), pageSettings.getPageSize());
        List<Recipe> returnList = new ArrayList<>();
        recipeRepository.findAllPublic(page).forEach(recipe -> {
            setRecipeRatings(recipe);
            returnList.add(recipe);
        });
        return returnList;
    }

    // Needs testing
    public ResponseEntity<?> getRecipeItemsById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()){
            Optional<List<RecipeItem>> recipeItemList = recipeItemRepository.findRecipeItemsByRecipe(recipe.get());
            if (recipeItemList.isPresent()){
                return new ResponseEntity<>(recipeItemList, HttpStatus.OK);
            }

            return new ResponseEntity<>("No Recipe Items Found", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>("Recipe Not Found", HttpStatus.NO_CONTENT);
    }

    public PaginationSetting getPaginationSettings(HashMap<String, HashMap<String, String>> objs) {
        try {
            Integer pageNumber = Integer.parseInt(objs.get("page").get("pageNumber"));
            Integer pageSize = Integer.parseInt(objs.get("page").get("pageSize"));
            return new PaginationSettingBuilder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .build();
        }
        catch (Exception e){
            LOGGER.error("Issue parsing page settings: ", e.getMessage(), e);
        }

        return DEFAULT_PAGE;
    }

    public RecipeQuery getRecipeQuery(HashMap<String, HashMap<String, String>> objs) {
        try {
            return new RecipeQueryBuilder()
                    .method(objs.get("query").get("method"))
                    .value(objs.get("query").get("value"))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Issue parsing query: ", e.getMessage(), e);
        }
        return DEFAULT_QUERY;
    }

    private <T> List<T> pageToList(Page<T> page){
        List<T> returnList = new ArrayList<>();
        page.forEach(returnList::add);
        return returnList;
    }

    // Write tests for this
    public ResponseEntity<?> getRecipeById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()){
            setRecipeRatings(recipe.get());
            return new ResponseEntity<>(recipe.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Recipe does not exist", HttpStatus.NOT_FOUND);
    }

    private HashMap<String, Object> createReturnMap(Page<Recipe> results) {
        HashMap<String, Object> returnMap = new HashMap<>();
        List<Recipe> recipeList = pageToList(results);
        returnMap.put("count", results.getTotalPages());
        returnMap.put("results", recipeList);
        return returnMap;
    }

    /**
     * Used to get recipes from the database
     * @param query: DTO for querying the db
     * @param pageSettings: DTO for setting the pagination settings
     * @return: a response entity representing the status of the operation
     * */
    public ResponseEntity<?> getQueriedRecipes(RecipeQuery query, PaginationSetting pageSettings) {
        QueryMethod method = queryMap.get(query.getMethod().toLowerCase());
        Pageable page = PageRequest.of(pageSettings.getPageNumber(), pageSettings.getPageSize());

        if (method == null) {
            return new ResponseEntity<>("Invalid Method", HttpStatus.BAD_REQUEST);
        }
        switch (method) {
            case NAME:
            case DESC: {
                Matcher specMatch = SPECIALCHARREGEX.matcher(query.getValue());
                if (specMatch.find()) {
                    return new ResponseEntity<>("Invalid Query", HttpStatus.BAD_REQUEST);
                }
                Page<Recipe> results = recipeRepository.findRecipeLike(query.getValue(), page);
                results.forEach(this::setRecipeRatings);
                HashMap<String, Object> returnMap = createReturnMap(results);
                return new ResponseEntity<>(returnMap, HttpStatus.OK);
            }
            case DEFAULT: {
                HashMap<String, Object> returnMap = new HashMap<>();
                returnMap.put("count", 1);
                returnMap.put("results", DEFAULT_RECIPE_LIST);
                return new ResponseEntity<>(returnMap, HttpStatus.OK);
            }
            default: {
                // This is unreachable in theory
                return new ResponseEntity<>("Invalid Method", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Updates recipeReview with inputted review Item
     * @param reviewItem: DTO for updating the recipe review.
     * @return ResponseEntity with fail or success message
     */
    public ResponseEntity<String> updateRecipeReview(UpdateRecipeReview reviewItem) {
        Optional<RecipeReview> recipeReview = recipeRatingRepository.findById(reviewItem.getReviewId());

        if(recipeReview.isPresent()) {
            recipeReview.get().setReview(reviewItem.getReview());

            try {
                recipeReview.get().setStars(reviewItem.getStars());
            } catch(Exception e) {
                return new ResponseEntity<>("Stars could not be set", HttpStatus.BAD_REQUEST);
            }

            try {
                recipeRatingRepository.save(recipeReview.get());
            } catch(Exception e) {
                return new ResponseEntity<>("Review failed to update", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Review not found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Review was updated", HttpStatus.OK);
    }

    /**
     * creates a new recipe review based on inputted reviewItem
     * @param reviewItem: DTO for adding a recipe review.
     * @return ResponseEntity with an error or success message
     */
    public ResponseEntity<String> addRecipeReview(String jwt, AddRecipeReview reviewItem, RecipeReview recipeReview) {
        recipeReview.setReview(reviewItem.getReview());
        recipeReview.setRecipe(recipeRepository.findById(reviewItem.getRecipeId()));
        recipeReview.setUser(userService.getUserFromJwtToken(jwt).get());

        try {
            recipeReview.setStars(reviewItem.getStars());
        } catch(Exception e) {
            return new ResponseEntity<>("Stars could not be set", HttpStatus.BAD_REQUEST);
        }
        try {
            recipeRatingRepository.save(recipeReview);
        } catch(Exception e) {
            return new ResponseEntity<>("Review failed to add", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Review was created", HttpStatus.OK);
    }

    /**
     * returns a RecipeReview based on an inputted recipe review id.
     * @param id: id of recipe review.
     * @return ResponseEntity with an error message or recipe review.
     */
    public ResponseEntity<?> getRecipeReview(long id) {
        Optional<RecipeReview> recipeReview = recipeRatingRepository.findById(id);
        if (recipeReview.isPresent()){
            return new ResponseEntity<>(recipeReview, HttpStatus.OK);
        }
        return new ResponseEntity<>("Recipe Review does not exist for this id", HttpStatus.NOT_FOUND);
    }

    /**
     * returns a RecipeReview list based on an inputted recipe id.
     * @param id: id of recipe.
     * @return ResponseEntity with an error message or list of recipe review.
     */
    public ResponseEntity<?> getRecipeReviewByRecipeId(long id) {
        Optional<List<RecipeReview>> recipeReviews = recipeRatingRepository.findByRecipeId(id);
        if (recipeReviews.isPresent()){
            for (RecipeReview review : recipeReviews.get()) {
                setRecipeRatings(review.getRecipe());
            }
            return new ResponseEntity<>(recipeReviews, HttpStatus.OK);
        }
        return new ResponseEntity<>("Recipe Review does not exist for this id", HttpStatus.NOT_FOUND);
    }
}
