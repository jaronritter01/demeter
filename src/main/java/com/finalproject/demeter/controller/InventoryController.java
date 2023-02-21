package com.finalproject.demeter.controller;

import com.finalproject.demeter.config.JwtUtil;
import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.repository.InventoryRepository;
import com.finalproject.demeter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/inventory")
public class InventoryController {
    private Logger log = LoggerFactory.getLogger(InventoryController.class);
    private JwtUtil jwtUtil = new JwtUtil();
    private InventoryRepository inventoryItemRepository;
    private UserRepository userRepository;
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}") private String sender;

    @Autowired
    public InventoryController(InventoryRepository inventoryItemRepository, UserRepository userRepository,
                               JavaMailSender mailSender) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @PostMapping
    public List<?> getTest(@RequestHeader("AUTHORIZATION") String jwt) {
        // extract JWT Token
        String email = jwtUtil.extractEmail(jwt.substring(7));
        User user = userRepository.findByEmail(email).get();
        List<InventoryItem> itemList = inventoryItemRepository.findInventoryItemByUserId(user);
        return itemList;
    }

    @GetMapping("/emailTest")
    public String emailTest(){
        return sendEmail();
    }

    public String sendEmail(){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo("jaronritter01@gmail.com");
            message.setText("test message");
            message.setSubject("Demeter: Test");

            mailSender.send(message);

            return "Sent Successfully";
        } catch (Exception e){
            e.printStackTrace();
            return "Error";
        }
    }
}
