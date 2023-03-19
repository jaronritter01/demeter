package com.finalproject.demeter.service

import com.finalproject.demeter.dao.FoodItem
import com.finalproject.demeter.dao.InventoryItem
import com.finalproject.demeter.dao.PersonalRecipe
import com.finalproject.demeter.dao.Recipe
import com.finalproject.demeter.dao.RecipeItem
import com.finalproject.demeter.dao.RecipeReview
import com.finalproject.demeter.dao.User
import com.finalproject.demeter.dto.PersonalRecipeItem
import com.finalproject.demeter.dto.RecipeQuery
import com.finalproject.demeter.dto.RecipeUpload
import com.finalproject.demeter.dto.UpdateRecipeReview
import com.finalproject.demeter.repository.FoodItemRepository
import com.finalproject.demeter.repository.PersonalRecipeRepository
import com.finalproject.demeter.repository.RecipeItemRepository
import com.finalproject.demeter.repository.RecipeRatingRepository
import com.finalproject.demeter.repository.RecipeRepository
import com.finalproject.demeter.util.FoodItemBuilder
import com.finalproject.demeter.util.InventoryItemBuilder
import com.finalproject.demeter.util.PersonalRecipeItemBuilder
import com.finalproject.demeter.util.RecipeBuilder
import com.finalproject.demeter.util.RecipeItemBuilder
import org.springframework.data.domain.Page
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
    RecipeService recipeService = new RecipeService(recipeRepository, recipeItemRepository, recipeRatingRepository,
            userService, foodItemRepository, personalRecipeRepository)
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
                    .avgRating(5.0F).reviewCount(10L).build()
            recipeList.add(recipe)
        }

        userInventory = new ArrayList<>()

        reviewItem.setReview("Very good.")
        reviewItem.setStars(5)
        reviewItem.setReviewId(1001)

        recipeReview.setId(1001)
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
}
