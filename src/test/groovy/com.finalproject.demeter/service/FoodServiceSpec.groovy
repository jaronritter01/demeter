package com.finalproject.demeter.service

import com.finalproject.demeter.dao.FoodItem
import com.finalproject.demeter.repository.FoodItemRepository
import spock.lang.Specification

class FoodServiceSpec extends Specification{
    private FoodItemRepository foodItemRepository = Mock()
    private FoodService foodService = new FoodService(foodItemRepository)
    private List<FoodItem> foodList = new ArrayList<>();

    def "food service should return all food items" () {
        when:
        foodService.getAllFoodItems()

        then:
        1 * foodItemRepository.findAll()

    }
}
