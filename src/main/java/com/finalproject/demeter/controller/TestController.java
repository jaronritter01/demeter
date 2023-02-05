package com.finalproject.demeter.controller;

import com.finalproject.demeter.config.JwtUtil;
import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.repository.InventoryItemRepository;
import com.finalproject.demeter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class TestController {

    private InventoryItemRepository inventoryItemRepository;
    private UserRepository userRepository;
    private JwtUtil jwtUtil = new JwtUtil();

    @Autowired
    public TestController(InventoryItemRepository inventoryItemRepository, UserRepository userRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
         this.userRepository = userRepository;
    }

    @GetMapping
    public List<?> getTest(@RequestHeader("AUTHORIZATION") String jwt) {
        // Example of how to extract the email and find a user's inventory
        String email = jwtUtil.extractEmail(jwt.substring(7));
        User user = userRepository.findByEmail(email).get();
        List<InventoryItem> itemList =inventoryItemRepository.findInventoryItemByUserId(user);
        return itemList;
    }
}
