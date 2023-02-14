package com.finalproject.demeter.controller;

import com.finalproject.demeter.config.JwtUtil;
import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.repository.InventoryRepository;
import com.finalproject.demeter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/inventory")
public class InventoryController {
    private Logger log = LoggerFactory.getLogger(InventoryController.class);
    private JwtUtil jwtUtil = new JwtUtil();
    private InventoryRepository inventoryItemRepository;
    private UserRepository userRepository;

    @Autowired
    public InventoryController(InventoryRepository inventoryItemRepository, UserRepository userRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public List<?> getTest(@RequestHeader("AUTHORIZATION") String jwt) {
        // extract JWT Token
        String email = jwtUtil.extractEmail(jwt.substring(7));
        User user = userRepository.findByEmail(email).get();
        List<InventoryItem> itemList = inventoryItemRepository.findInventoryItemByUserId(user);
        return itemList;
    }
}
