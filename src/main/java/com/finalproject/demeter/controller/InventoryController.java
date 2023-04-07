package com.finalproject.demeter.controller;

import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.dto.FoodMark;
import com.finalproject.demeter.dto.UpdateInventory;
import com.finalproject.demeter.service.FoodService;
import com.finalproject.demeter.service.UserService;
import com.finalproject.demeter.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/inventory")
public class InventoryController {
    private Logger log = LoggerFactory.getLogger(InventoryController.class);
    private JwtUtil jwtUtil = new JwtUtil();
    private UserService userService;
    private FoodService foodService;

    @Autowired
    public InventoryController(UserService userService, FoodService foodService) {
        this.userService = userService;
        this.foodService = foodService;
    }

    @PostMapping("/getMinorItems")
    public ResponseEntity<?> getMinorItems(@RequestHeader("AUTHORIZATION") String jwt) {
        return userService.getMinorItems(jwt);
    }

    @PostMapping("/markItem")
    public ResponseEntity<String> markMinorItem(
            @RequestHeader("AUTHORIZATION") String jwt, @RequestBody FoodMark item) {
        return userService.markFoodItem(jwt, item.getItemId(), item.getCommand());
    }

    @PostMapping("/updateInventory")
    public ResponseEntity<?> updateInventory(@RequestHeader("AUTHORIZATION") String jwt, @RequestBody UpdateInventory item) {
        Optional<User> user = userService.getUserFromJwtToken(jwt);
        ResponseEntity<String> response = userService.updateInventory(user.get(), item);
        if (response.getBody().equals("Inventory was saved") ||
                response.getBody().equals("Inventory Item was Removed")) {
            List<InventoryItem> inventory = userService.getInventory(user.get());
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        }

        return response;
    }

    @PostMapping("/getInventory")
    List<InventoryItem> getUserInventory(@RequestHeader("AUTHORIZATION") String jwt) {
        Optional<User> user = userService.getUserFromJwtToken(jwt);
        if (user.isPresent()) {
            return userService.getInventory(user.get());
        }
        return new ArrayList<>();
    }


    /**
     * This is used to add an item to a users disliked items
     * @param jwt - the user's jwt token
     * @param foodItemId - the id of the food item the user wants to dislike
     * @return A ResponseEntity that contains the status of the operation.
     */
    @PostMapping("/addDislikedItem")
    ResponseEntity<?> addDislikedItem(@RequestHeader("AUTHORIZATION") String jwt, @RequestBody Long foodItemId) {
        return userService.addDislikedItem(jwt, foodItemId);
    }

    /**
     * This function is used to removed an item from the users disliked items.
     * @param jwt - a users jwt token
     * @param foodItemId - the id of the food item that the user wants to remove
     * @return a ResponseEntity that will contain the status of the operation
     */
    @PostMapping("/removeDislikedItem")
    ResponseEntity<?> removeDislikedItem(@RequestHeader("AUTHORIZATION") String jwt, @RequestBody Long foodItemId) {
        return userService.removeDislikedItem(jwt, foodItemId);
    }

    /**
     * This is used to retreive a users disliked items.
     * @param jwt - the users jwt token
     * @return ResponseEntity the will contain an error or a list of items
     */
    @PostMapping("/getDislikedItems")
    ResponseEntity<?> getDislikedItems(@RequestHeader("AUTHORIZATION") String jwt) {
        return userService.getDislikedItems(jwt);
    }
    /**
     * The function is an endpoint to find the substitution foodItems for a foodItem.
     * @param jwt - authentication/username
     * @param id - the id for a foodItem that needs substitution
     * @return a list or empty list of foodItems
     */
    @GetMapping("/getSub")
    List<FoodItem> getSubItems(@RequestHeader("AUTHORIZATION") String jwt, @RequestParam Long id) {
        Optional<User> user = userService.getUserFromJwtToken(jwt);
        if (user.isPresent()) {
            return foodService.getSubItems(user.get(), id);
        }
        return new ArrayList<>();
    }
}
