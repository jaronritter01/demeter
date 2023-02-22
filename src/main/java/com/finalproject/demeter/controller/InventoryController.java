package com.finalproject.demeter.controller;

import com.finalproject.demeter.dto.UpdateInventory;
import com.finalproject.demeter.service.UserService;
import com.finalproject.demeter.util.JwtUtil;
import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;
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

    @Autowired
    public InventoryController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/updateInventory")
    public ResponseEntity<?> updateInventory(@RequestHeader("AUTHORIZATION") String jwt, @RequestBody UpdateInventory item) {
        Optional<User> user = userService.getUserFromJwtToken(jwt);
        ResponseEntity response = userService.updateInventory(user.get(), item);
        if (response.getBody().equals("Inventory was saved")) {
            List<InventoryItem> inventory = userService.getInventory(user.get());
            return new ResponseEntity(inventory, HttpStatus.OK);
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
}
