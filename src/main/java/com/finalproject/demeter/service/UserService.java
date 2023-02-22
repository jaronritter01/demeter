package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.FoodItem;
import com.finalproject.demeter.dao.InventoryItem;
import com.finalproject.demeter.dao.PasswordResetToken;
import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.dto.SignUpDto;
import com.finalproject.demeter.dto.UpdateInventory;
import com.finalproject.demeter.repository.FoodItemRepository;
import com.finalproject.demeter.repository.InventoryRepository;
import com.finalproject.demeter.repository.PasswordTokenRepository;
import com.finalproject.demeter.repository.UserRepository;
import com.finalproject.demeter.util.AuthUtil;
import com.finalproject.demeter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private JwtUtil jwtUtil = new JwtUtil();
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private PasswordTokenRepository passwordTokenRepository;
    private FoodItemRepository foodItemRepository;
    private InventoryRepository inventoryRepository;

    private final Set<SimpleGrantedAuthority> authorities = new HashSet<>(){{
        add(new SimpleGrantedAuthority("user"));
    }};

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       PasswordTokenRepository passwordTokenRepository, FoodItemRepository foodItemRepository,
                       InventoryRepository inventoryRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordTokenRepository = passwordTokenRepository;
        this.foodItemRepository = foodItemRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public Optional<User> getUserFromJwtToken(String jwtToken){
        String email = jwtUtil.extractEmail(jwtToken.substring(7));
        User user = userRepository.findByEmail(email).get();
        return Optional.ofNullable(user);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: "+ usernameOrEmail)
                );

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                authorities);
    }

    public ResponseEntity<?> addUser(SignUpDto signUpDto) {
        // add check for username exists in a DB
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (AuthUtil.isValidUser(signUpDto)){
            createAndSaveUser(signUpDto);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("User information does not meet the necessary requirements", HttpStatus.BAD_REQUEST);
    }

    private void createAndSaveUser(SignUpDto signUpDto){
        User user = new User();
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail().toLowerCase());
        // Hash password
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(user, token);
        passwordTokenRepository.save(myToken);
    }

    public boolean isTokenValid(String token) {
        Optional<PasswordResetToken> pToken = passwordTokenRepository.findByToken(token);
        if (pToken.isPresent()){
            return !isTokenExpired(pToken.get());
        }
        return false;
    }

    public Optional<User> findUserByToken(String token){
        Optional<PasswordResetToken> pToken = passwordTokenRepository.findByToken(token);
        if (pToken.isPresent()) {
            return Optional.of(pToken.get().getUser());
        }
        return Optional.ofNullable(null);
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public void updateUserPassword(User user, String pass) {
        user.setPassword(passwordEncoder.encode(pass));
        userRepository.save(user);
    }

    public ResponseEntity<String> updateInventory(User user, UpdateInventory inventoryItem) {
        List<InventoryItem> inventory = inventoryRepository.findInventoryItemByUserId(user);
        InventoryItem foundItem = null;
        // This could likely be optimized
        for (InventoryItem item : inventory)
            if (item.getFoodId().getId() == inventoryItem.getFoodId()) {
                foundItem = item;
        }

        if (foundItem != null){
            Float currentQuantity = foundItem.getQuantity();
            if (inventoryItem.getQuantity() > currentQuantity) {
                return new ResponseEntity("Invalid Quantity", HttpStatus.BAD_REQUEST);
            }
            foundItem.setQuantity(currentQuantity + inventoryItem.getQuantity());
        } else {
            // The user does not have the item in their current inventory
            if (inventoryItem.getQuantity() <= 0) { // and the added value is invalid
                return new ResponseEntity("Invalid Quantity", HttpStatus.BAD_REQUEST);
            }
            Optional<FoodItem> newItem = foodItemRepository.findById(inventoryItem.getFoodId());
            if (!newItem.isPresent()) {
                return new ResponseEntity("The given item does not exist", HttpStatus.NO_CONTENT);
            }
            foundItem = new InventoryItem(
                    user, newItem.get(), inventoryItem.getQuantity(), inventoryItem.getUnit()
            );
        }

        inventoryRepository.save(foundItem);
        return new ResponseEntity("Inventory was saved", HttpStatus.OK);
    }

    public List<InventoryItem> getInventory(User user) {
        return inventoryRepository.findInventoryItemByUserId(user);
    }
}
