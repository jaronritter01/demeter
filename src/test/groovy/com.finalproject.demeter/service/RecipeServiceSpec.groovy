package com.finalproject.demeter.service

import com.finalproject.demeter.dao.FoodItem
import com.finalproject.demeter.dao.InventoryItem
import com.finalproject.demeter.dao.Recipe
import com.finalproject.demeter.dao.RecipeItem
import com.finalproject.demeter.dao.RecipeReview
import com.finalproject.demeter.dao.User
import com.finalproject.demeter.dto.RecipeQuery
import com.finalproject.demeter.dto.UpdateRecipeReview
import com.finalproject.demeter.repository.RecipeItemRepository
import com.finalproject.demeter.repository.RecipeRatingRepository
import com.finalproject.demeter.repository.RecipeRepository
import com.finalproject.demeter.util.FoodItemBuilder
import com.finalproject.demeter.util.InventoryItemBuilder
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
    RecipeService recipeService = new RecipeService(recipeRepository, recipeItemRepository, recipeRatingRepository,
            userService)
    User user = new User()
    FoodItem foodItemOne = null
    FoodItem foodItemTwo = null
    List<Recipe> recipeList = new ArrayList<>()
    List<InventoryItem> userInventory = null

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
                    .cookTime(100).avgRating(5.0F).reviewCount(10L).build()
            recipeList.add(recipe)
        }

        userInventory = new ArrayList<>()

        reviewItem.setReview("Very good.")
        reviewItem.setStars(5)
        reviewItem.setReviewId(1001)

        recipeReview.setId(1001)
    }

    def "When a user has more than enough ingredients for a recipe, true should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems)

        then:
        canBeMade
    }

    def "When a user has just enough ingredients for a recipe, true should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(5F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems)

        then:
        canBeMade
    }

    def "When a user does not enough ingredients for a recipe, false should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemOne)
                .quantity(2F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems)

        then:
        !canBeMade
    }

    def "When a user does not have the right ingredients for a recipe, false should be returned" () {
        given:
        List<RecipeItem> recipeItems = new ArrayList<>()

        and:
        Recipe recipe = recipeList.get(0)
        RecipeItem recipeItemOne = new RecipeItemBuilder().id(1L).foodItem(foodItemOne).recipe(recipe)
                .measurementUnit("grams").quantity(5.0F).build()

        and:
        InventoryItem inventoryItemOne = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItemTwo)
                .quantity(10F).unit("grams").build()
        userInventory.add(inventoryItemOne)
        recipeItems.add(recipeItemOne)

        when:
        boolean canBeMade = recipeService.canRecipeBeMade(userInventory, recipeItems)

        then:
        !canBeMade
    }

    def "getAllRecipes should call the recipe repo" () {
        when:
        recipeService.getAllRecipes(RecipeService.DEFAULT_PAGE)

        then:
        1 * recipeRepository.findAll(_) >> new PageImpl<Recipe>(new ArrayList<Recipe>())
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

    def "when a valid method def (less) is passed, a 200 and a list should be returned" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("lesstime")
        rq.setValue("2")
        recipeRepository.findRecipeWithTimeLess(_,_) >> new PageImpl<>(new ArrayList<Recipe>())
        RecipeService.createReturnMap(_) >> new HashMap<String, Object>()

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq, RecipeService.DEFAULT_PAGE)

        then:
        response.getStatusCode() == HttpStatus.OK && response.body instanceof HashMap
    }

    def "when a valid method def (less) and an invalid value is passed, a 400" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("lesstime")
        rq.setValue("asdf")
        recipeRepository.findRecipeWithTimeLess(_,_) >> {throw new NumberFormatException()}

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq, RecipeService.DEFAULT_PAGE)

        then:
        response.getStatusCode() == HttpStatus.BAD_REQUEST && response.body == "Please pass a valid number"
    }

    def "when a valid method def (more) is passed, a 200 and a list should be returned" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("moretime")
        rq.setValue("2")
        recipeRepository.findRecipeWithTimeMore(_,_) >> new PageImpl<>(new ArrayList<Recipe>())
        RecipeService.createReturnMap(_) >> new HashMap<String, Object>()

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq, RecipeService.DEFAULT_PAGE)

        then:
        response.getStatusCode() == HttpStatus.OK && response.body instanceof HashMap
    }

    def "when a valid method def (more) and an invalid value is passed, a 400" () {
        given:
        RecipeQuery rq = new RecipeQuery()
        rq.setMethod("moretime")
        rq.setValue("asdf")
        recipeRepository.findRecipeWithTimeMore(_,_) >> {throw new NumberFormatException()}

        when:
        ResponseEntity response = recipeService.getQueriedRecipes(rq, RecipeService.DEFAULT_PAGE)

        then:
        response.getStatusCode() == HttpStatus.BAD_REQUEST && response.body == "Please pass a valid number"
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
        1 * recipeRatingRepository.findById(1001) >> recipeReview
        response.getStatusCode() == HttpStatus.OK && response.body == "Review was saved"
    }

    def "test failure to set stars updateRecipeReview" () {
        given:
        reviewItem.setStars(10)

        when:
        ResponseEntity response = recipeService.updateRecipeReview(reviewItem)

        then:
        1 * recipeRatingRepository.findById(1001) >> recipeReview
        response.getStatusCode() == HttpStatus.BAD_REQUEST && response.body == "A value less than 1 or more than 5 cannot be used as a rating"
    }
}
