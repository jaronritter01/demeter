package com.finalproject.demeter.service

import com.finalproject.demeter.dao.DislikedItem
import com.finalproject.demeter.dao.FavoriteRecipe
import com.finalproject.demeter.dao.FoodItem
import com.finalproject.demeter.dao.InventoryItem
import com.finalproject.demeter.dao.MinorItem
import com.finalproject.demeter.dao.PersonalRecipe
import com.finalproject.demeter.dao.Recipe
import com.finalproject.demeter.dao.RecipeItem
import com.finalproject.demeter.dao.RecipeReview
import com.finalproject.demeter.dao.User
import com.finalproject.demeter.dto.PersonalRecipeItem
import com.finalproject.demeter.dto.RecipeQuery
import com.finalproject.demeter.dto.RecipeUpload
import com.finalproject.demeter.dto.RecipeWithSub
import com.finalproject.demeter.dto.UpdateRecipeReview
import com.finalproject.demeter.repository.DislikedItemRepository
import com.finalproject.demeter.repository.FavoriteRecipeRepository
import com.finalproject.demeter.repository.FoodItemRepository
import com.finalproject.demeter.repository.MinorItemRepository
import com.finalproject.demeter.repository.PersonalRecipeRepository
import com.finalproject.demeter.repository.RecipeItemRepository
import com.finalproject.demeter.repository.RecipeRatingRepository
import com.finalproject.demeter.repository.RecipeRepository
import com.finalproject.demeter.util.DislikedItemBuilder
import com.finalproject.demeter.util.FavoriteRecipeBuilder
import com.finalproject.demeter.util.FoodItemBuilder
import com.finalproject.demeter.util.InventoryItemBuilder
import com.finalproject.demeter.util.MinorItemBuilder
import com.finalproject.demeter.util.PersonalRecipeItemBuilder
import com.finalproject.demeter.util.RecipeBuilder
import com.finalproject.demeter.util.RecipeItemBuilder
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification


class RecipeServiceSpec extends Specification{
    RecipeRepository recipeRepository = Mock()
    RecipeItemRepository recipeItemRepository = Mock()
    RecipeRatingRepository recipeRatingRepository = Mock()
    UserService userService = Mock()
    FoodItemRepository foodItemRepository= Mock()
    PersonalRecipeRepository personalRecipeRepository = Mock()
    DislikedItemRepository dislikedItemRepository = Mock()
    FavoriteRecipeRepository favoriteRecipeRepository = Mock()
    MinorItemRepository minorItemRepository = Mock()
    FoodService foodService = Mock()
    RecipeService recipeService = new RecipeService(recipeRepository, recipeItemRepository, recipeRatingRepository,
            userService, foodItemRepository, personalRecipeRepository, dislikedItemRepository, minorItemRepository,
            favoriteRecipeRepository, foodService)
    User user = new User()
    FoodItem foodItemOne = null
    FoodItem foodItemTwo = null
    List<Recipe> recipeList = new ArrayList<>()
    List<InventoryItem> userInventory = null
    List<MinorItem> minorItems = new ArrayList<>()

    UpdateRecipeReview reviewItem = new UpdateRecipeReview()
    RecipeReview recipeReview = new RecipeReview()

    def setup(){
        user.id = 1L
        user.firstName = "John"
        user.lastName = "Doe"
        user.email = "jdoe@gmail.com"
        user.password = "Testpassword1!"
        foodItemOne = new FoodItemBuilder().id(1L).name("item1")
                .description("item 1 description").reusable(false).picUrl("randomUrl").build()
        foodItemTwo = new FoodItemBuilder().id(2L).name("item2")
                .description("item 2 description").reusable(false).picUrl("randomUrl").build()

        for (int i = 1; i <= 5; i++) {
            Recipe recipe = new RecipeBuilder().id(Long.valueOf(i.toString()))
                    .name(String.format("Test Recipe %d", i))
                    .description(String.format("Test Recipe %d Description", i)).isPublic(true)
                    .avgRating(5.0F).reviewCount(10L).build()
            recipeList.add(recipe)
        }

        userInventory = new ArrayList<>()

        reviewItem.setReview("Very good.")
        reviewItem.setStars(5)
        reviewItem.setReviewId(1001)

        recipeReview.setId(1001)
    }

    def "When a valid JWT is passed, but the user cannot be found, an error should be returned" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.empty()

        when:
        ResponseEntity ru = recipeService.getFavoriteRecipe(_ as String)

        then:
        ru.statusCode == HttpStatus.NOT_FOUND
        ru.body == "User not Found"
    }

    def "When a valid JWT is passed, but the user has no favorites, an empty list should be returned" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        favoriteRecipeRepository.findByUser(_) >> Optional.empty()

        when:
        ResponseEntity ru = recipeService.getFavoriteRecipe(_ as String)

        then:
        ru.statusCode == HttpStatus.OK
        ru.body == new ArrayList()
    }

    def "When a valid JWT is passed, the users favorite list should be returned" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        List<FavoriteRecipe> favoriteRecipes = List.of(
                new FavoriteRecipeBuilder().id(1L).user(user).recipe(recipeList.get(0)).build()
        )
        favoriteRecipeRepository.findByUser(_) >> Optional.of(favoriteRecipes)
        when:
        ResponseEntity ru = recipeService.getFavoriteRecipe(_ as String)

        then:
        ru.statusCode == HttpStatus.OK
        ru.body == favoriteRecipes
    }

    def "When a valid JWT and invalid recipe id are passed, but the user cannot be found, the recipe shouldn't be removed from favorite list" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.empty()
        List<FavoriteRecipe> favoriteRecipes = List.of(
                new FavoriteRecipeBuilder().id(1L).user(user).recipe(recipeList.get(0)).build()
        )
        favoriteRecipeRepository.findByUser(_) >> Optional.of(favoriteRecipes)
        when:
        ResponseEntity ru = recipeService.removeFavoriteRecipe(_ as String, 2L)

        then:
        0 * favoriteRecipeRepository.delete(_)
        ru.statusCode == HttpStatus.NOT_FOUND
        ru.body == "User not Found"
    }

    def "When a valid JWT and invalid recipe id are passed, the recipe shouldn't be removed from favorite list" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        List<FavoriteRecipe> favoriteRecipes = List.of(
                new FavoriteRecipeBuilder().id(1L).user(user).recipe(recipeList.get(0)).build()
        )
        favoriteRecipeRepository.findByUser(_) >> Optional.of(favoriteRecipes)
        when:
        ResponseEntity ru = recipeService.removeFavoriteRecipe(_ as String, 2L)

        then:
        0 * favoriteRecipeRepository.delete(_)
        ru.statusCode == HttpStatus.NOT_FOUND
        ru.body == "Recipe to remove was not found"
    }

    def "When a valid JWT and recipe id are passed, the recipe should be removed from favorite list" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        List<FavoriteRecipe> favoriteRecipes = List.of(
                new FavoriteRecipeBuilder().id(1L).user(user).recipe(recipeList.get(0)).build()
        )
        favoriteRecipeRepository.findByUser(_) >> Optional.of(favoriteRecipes)
        when:
        ResponseEntity ru = recipeService.removeFavoriteRecipe(_ as String, 1L)

        then:
        1 * favoriteRecipeRepository.delete(_)
        ru.statusCode == HttpStatus.OK
        ru.body == "Removal Successful"
    }

    def "When a valid JWT and recipe id are passed, the recipe should be added to favorite list" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        recipeRepository.findById(_) >> Optional.of(recipeList.get(0))

        when:
        ResponseEntity ru = recipeService.addFavoriteRecipe(_ as String, 1L)

        then:
        1 * favoriteRecipeRepository.save(_)
        ru.statusCode == HttpStatus.OK
        ru.body == "Favorite saved"
    }

    def "When a valid JWT and invalid recipe id are passed, the recipe shouldn't be added to favorites" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        recipeRepository.findById(_) >> Optional.empty()

        when:
        ResponseEntity ru = recipeService.addFavoriteRecipe(_ as String, 1L)

        then:
        0 * favoriteRecipeRepository.save(_)
        ru.statusCode == HttpStatus.NOT_FOUND
        ru.body == "Recipe not Found"
    }

    def "When an invalid JWT is passed, the recipe shouldn't be added to favorites" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.empty()
        recipeRepository.findById(_) >> Optional.empty()

        when:
        ResponseEntity ru = recipeService.addFavoriteRecipe(_ as String, 1L)

        then:
        0 * favoriteRecipeRepository.save(_)
        ru.statusCode == HttpStatus.NOT_FOUND
        ru.body == "User not Found"
    }

    def "When a valid JWT and recipe id are passed, the recipe should be removed" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        recipeRepository.findById(_) >> Optional.of(recipeList.get(0))
        personalRecipeRepository.findByUserAndRecipe(_,_) >> Optional.of(new PersonalRecipe())

        when:
        ResponseEntity ru = recipeService.removePersonalRecipe(_ as String, 1L)

        then:
        1 * personalRecipeRepository.delete(_)
        1 * recipeItemRepository.deleteRecipeItemsByRecipe(_)
        1 * recipeRepository.delete(_)
        ru.body == "Personal Recipe Successfully Removed" && ru.getStatusCode() == HttpStatus.OK
    }

    def "When an invalid JWT and recipe id are passed, the recipe should not be removed" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.empty()
        recipeRepository.findById(_) >> Optional.of(recipeList.get(0))
        personalRecipeRepository.findByUserAndRecipe(_,_) >> Optional.of(new PersonalRecipe())

        when:
        ResponseEntity ru = recipeService.removePersonalRecipe(_ as String, 1L)

        then:
        0 * personalRecipeRepository.delete(_)
        0 * recipeItemRepository.deleteRecipeItemsByRecipe(_)
        0 * recipeRepository.delete(_)
        ru.body == "User cannot be found" && ru.getStatusCode() == HttpStatus.NOT_FOUND
    }

    def "When an valid JWT and invalid recipe id are passed, the recipe should not be removed" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        recipeRepository.findById(_) >> Optional.empty()
        personalRecipeRepository.findByUserAndRecipe(_,_) >> Optional.of(new PersonalRecipe())

        when:
        ResponseEntity ru = recipeService.removePersonalRecipe(_ as String, 1L)

        then:
        0 * personalRecipeRepository.delete(_)
        0 * recipeItemRepository.deleteRecipeItemsByRecipe(_)
        0 * recipeRepository.delete(_)
        ru.body == "Recipe cannot be found" && ru.getStatusCode() == HttpStatus.NOT_FOUND
    }

    def "When an valid JWT and recipe id are passed, but the personal Recipe cannot be found, the recipe should not be removed" () {
        given:
        userService.getUserFromJwtToken(_) >> Optional.of(user)
        recipeRepository.findById(_) >> Optional.of(recipeList.get(0))
        personalRecipeRepository.findByUserAndRecipe(_,_) >> Optional.empty()

        when:
        ResponseEntity ru = recipeService.removePersonalRecipe(_ as String, 1L)

        then:
        0 * personalRecipeRepository.delete(_)
        0 * recipeItemRepository.deleteRecipeItemsByRecipe(_)
        0 * recipeRepository.delete(_)
        ru.body == "Personal Recipe cannot be found" && ru.getStatusCode() == HttpStatus.NOT_FOUND
    }

    def "When a valid jwt and personal recipe dto are passed, the user can add a personal recipe" () {
        given:
        List<PersonalRecipeItem> ingredientList = new ArrayList<PersonalRecipeItem>(){{
            add(new PersonalRecipeItemBuilder().unit("grams").quantity(10).foodItemId(1).build())
            add(new PersonalRecipeItemBuilder().unit("grams").quantity(11).foodItemId(2).build())
        }}
        userService.getUserFromJwtToken(_ as String) >> Optional.of(user)
        RecipeUpload ru = new RecipeUpload()
        ru.setRecipeName("test Recipe")
        ru.setRecipeDescription("test recipe Description")
        ru.setIngredients(ingredientList)
        foodItemRepository.findById(_) >> Optional.of(foodItemOne)

        when:
        ResponseEntity re = recipeService.uploadPersonalRecipe(_ as String, ru)

        then:
        1 * recipeRepository.save(_)
        2 * recipeItemRepository.save(_)
        1 * personalRecipeRepository.save(_)
        re.body == "Recipe Successfully saved"
    }

    def "When a valid jwt and personal recipe dto are passed, but the food items do not exists the user cannot add a personal recipe" () {
        given:
        List<PersonalRecipeItem> ingredientList = new ArrayList<PersonalRecipeItem>(){{
            add(new PersonalRecipeItemBuilder().unit("grams").quantity(10).foodItemId(1).build())
            add(new PersonalRecipeItemBuilder().unit("grams").quantity(11).foodItemId(2).build())
        }}
        userService.getUserFromJwtToken(_ as String) >> Optional.of(user)
        RecipeUpload ru = new RecipeUpload()
        ru.setRecipeName("test Recipe")
        ru.setRecipeDescription("test recipe Description")
        ru.setIngredients(ingredientList)
        foodItemRepository.findById(_ as Long) >> Optional.empty()

        when:
        ResponseEntity re = recipeService.uploadPersonalRecipe(_ as String, ru)

        then:
        0 * recipeRepository.save(_)
        0 * recipeItemRepository.save(_)
        0 * personalRecipeRepository.save(_)
        re.body == "An ingredient is missing"
    }

    def "When a valid jwt and an invalid personal recipe dto are passed, the user cannot add a personal recipe" () {
        given:
        List<PersonalRecipeItem> ingredientList = new ArrayList<PersonalRecipeItem>()
        userService.getUserFromJwtToken(_ as String) >> Optional.of(user)
        RecipeUpload ru = new RecipeUpload()
        ru.setRecipeName("test Recipe")
        ru.setRecipeDescription("test recipe Description")
        ru.setIngredients(ingredientList)

        when:
        ResponseEntity re = recipeService.uploadPersonalRecipe(_ as String, ru)

        then:
        0 * recipeRepository.save(_)
        0 * recipeItemRepository.save(_)
        0 * personalRecipeRepository.save(_)
        re.body == "No Ingredients were passed"
    }

    def "When an invalid jwt and a valid personal recipe dto are passed, the user cannot add a personal recipe" () {
        given:
        List<PersonalRecipeItem> ingredientList = new ArrayList<PersonalRecipeItem>(){{
            add(new PersonalRecipeItemBuilder().unit("grams").quantity(10).foodItemId(1).build())
            add(new PersonalRecipeItemBuilder().unit("grams").quantity(11).foodItemId(2).build())
        }}
        userService.getUserFromJwtToken(_ as String) >> Optional.empty()
        RecipeUpload ru = new RecipeUpload()
        ru.setRecipeName("test Recipe")
        ru.setRecipeDescription("test recipe Description")
        ru.setIngredients(ingredientList)

        when:
        ResponseEntity re = recipeService.uploadPersonalRecipe(_ as String, ru)

        then:
        0 * recipeRepository.save(_)
        0 * recipeItemRepository.save(_)
        0 * personalRecipeRepository.save(_)
        re.body == "User was not Found"
    }

    def "When a user does not have enough of an item, but its not marked as minor, the recipe should not still be made." () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(1F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.of(
                List.of(
                        new MinorItemBuilder().id(2L).user(user).foodItem(foodItemOne).build()
                )
        )

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        canBeMade
    }

    def "When a user does not have enough of an item, but its marked as minor, the recipe should still be made." () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(1F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.of(
                List.of(
                    new MinorItemBuilder().id(1L).user(user).foodItem(foodItemOne).build()
                )
        )

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        canBeMade
    }

    def "When a user has more than enough ingredients for a recipe, but an item in the recipe is marked as disliked, the user cannot make it" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        DislikedItem dislikedItem = new DislikedItemBuilder().id(1L).user(user).foodItem(foodItemOne).build()
        Optional<List<DislikedItem>> userPreferences = Optional.of(List.of(dislikedItem))

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        !canBeMade
    }

    def "When a user has more than enough ingredients for a recipe and a preference list, but no item in the recipe is marked, the user can make it" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        DislikedItem dislikedItem = new DislikedItemBuilder().id(1L).user(user).foodItem(foodItemTwo).build()
        Optional<List<DislikedItem>> userPreferences = Optional.of(List.of(dislikedItem))

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        canBeMade
    }

    def "When a user has more than enough ingredients for a recipe, true should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        canBeMade
    }

    def "When a user has just enough ingredients for a recipe, true should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(5F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        canBeMade
    }

    def "When a user does not enough ingredients for a recipe, false should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(2F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        !canBeMade
    }

    def "When a user does not have the right ingredients for a recipe, false should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemTwo)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        !canBeMade
    }

    def "getAllRecipes should call the recipe repo" () {
        when:
        recipeService.getAllRecipes(RecipeService.DEFAULT_PAGE)

        then:
        1 * recipeRepository.findAllPublic(_) >> new PageImpl<Recipe>(new ArrayList<Recipe>())
    }

    def "when a valid method def is passed, a 200 should be returned" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("name")
        rq.setValue("chicken")

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq, RecipeService.DEFAULT_PAGE)

        then:
        1 * recipeRepository.findRecipeLike(_,_) >> new PageImpl<Recipe>(new ArrayList<Recipe>())
        response.getStatusCode() == HttpStatus.OK && response.body instanceof HashMap
    }

    def "when an invalid method def is passed, a bad request should be returned" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("invalidMethod")
        rq.setValue("abc")

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq, RecipeService.DEFAULT_PAGE)

        then:
        response.getStatusCode() == HttpStatus.BAD_REQUEST && response.body == "Invalid Method"
    }

    def "the default query is invoked" () {
        when:
        ResponseEntity response = recipeService.getQueriedRecipes(RecipeService.DEFAULT_QUERY, RecipeService.DEFAULT_PAGE)

        then:
        response.getStatusCode() == HttpStatus.OK && response.body instanceof HashMap
    }

    def "test updateRecipeReview" () {
        when:
        ResponseEntity response = recipeService.updateRecipeReview(reviewItem)

        then:
        1 * recipeRatingRepository.findById(1001) >> Optional.of(recipeReview)
        response.getStatusCode() == HttpStatus.OK && response.body == "Review was updated"
    }

    def "test failure to set stars updateRecipeReview" () {
        given:
        reviewItem.setStars(10)

        when:
        ResponseEntity response = recipeService.updateRecipeReview(reviewItem)

        then:
        1 * recipeRatingRepository.findById(1001) >> Optional.of(recipeReview)
        response.getStatusCode() == HttpStatus.BAD_REQUEST && response.body == "Stars could not be set"
    }

    def "When a user does not have the right ingredients for a recipe, but has a sub true should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()
        List<FoodItem> foodItemList = new ArrayList<FoodItem>();
        foodItemList.add(foodItemTwo);
        foodService.getSubItems(user, 1) >> foodItemList

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemTwo)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        canBeMade
    }

    def "When a user does not have the right ingredients for a recipe, but has more than 1 subbed items" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()
        Optional<List<DislikedItem>> userPreferences = Optional.empty()
        List<FoodItem> foodItemList = new ArrayList<FoodItem>();
        foodItemList.add(foodItemTwo);
        foodService.getSubItems(user, 1) >> foodItemList
        foodService.getSubItems(user, 2) >> foodItemList

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()
        RecipeItem recipeItemTwo = new RecipeItemBuilder().id(1L).foodItem(foodItemTwo).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()
        FoodItem foodItemThree = new FoodItemBuilder().id(3L).name("item3")
                .description("item 3 description").reusable(false).picUrl("randomUrl").build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemThree)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)
        recipeItems.add(recipeItemTwo)

        and:
        Optional<List<MinorItem>> minorItemsOpt = Optional.empty()

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems, userPreferences, minorItemsOpt, user, new RecipeWithSub())

        then:
        !canBeMade
    }
}
