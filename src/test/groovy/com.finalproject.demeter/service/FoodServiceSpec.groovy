package com.finalproject.demeter.service

import com.finalproject.demeter.dao.FoodItem
import com.finalproject.demeter.dao.Substitution
import com.finalproject.demeter.dao.User
import com.finalproject.demeter.repository.FoodItemRepository
import com.finalproject.demeter.repository.InventoryRepository
import com.finalproject.demeter.repository.SubstitutionsRepository
import spock.lang.Specification

class FoodServiceSpec extends Specification{
    private FoodItemRepository foodItemRepository = Mock()
    private SubstitutionsRepository substitutionsRepository = Mock()
    private InventoryRepository inventoryRepository = Mock()
    private FoodService foodService = new FoodService(foodItemRepository,
                                            inventoryRepository, substitutionsRepository)
    private List<FoodItem> foodList = new ArrayList<>();
    List<Substitution> possibleSubs = new ArrayList<>()
    List<Long> foodItemIds = new ArrayList<>()
    User user = new User()
    FoodItem missingFoodItem = new FoodItem()
    FoodItem foundFoodItem = new FoodItem()
    Substitution sub = new Substitution()

    // user setup for possible future tests
    def setup() {
        user.setId(1L)
        user.setEmail("email")
        user.setFirstName("Cottoneye")
        user.setLastName("Joe")
        user.setPassword("password")
        user.setUsername("username")

        foundFoodItem.setId(40L)
        foundFoodItem.setDescription("desc")
        foundFoodItem.setName("Name")
        foundFoodItem.setPicUrl("url")
        foundFoodItem.setReusable(true)

        missingFoodItem.setId(90L)
        missingFoodItem.setDescription("desc")
        missingFoodItem.setName("Name")
        missingFoodItem.setPicUrl("url")
        missingFoodItem.setReusable(true)

        sub.setId(40L)
        sub.setMissingItem(missingFoodItem)
        possibleSubs.add(sub)
        foodItemIds.add(40L)
    }

    def "food service should return all food items" () {
        when:
        foodService.getAllFoodItems()

        then:
        1 * foodItemRepository.findAll()

    }

    def "return substitutions"() {
        given:
        substitutionsRepository.findByMissingItemId(90L) >> possibleSubs
        inventoryRepository.getFoodItemsByUserId(user.getId()) >> foodItemIds
        foodItemRepository.findFoodItemById(40L) >> foundFoodItem

        when:
        List<FoodItem> response = foodService.getSubItems(user, 90)

        then:
        response.get(0).getId() == 40L
    }
}
