package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.*;
import com.finalproject.demeter.dto.SignUpDto;
import com.finalproject.demeter.dto.UpdateInventory;
import com.finalproject.demeter.repository.*;
import com.finalproject.demeter.util.AuthUtil;
import com.finalproject.demeter.util.DislikedItemBuilder;
import com.finalproject.demeter.util.JwtUtil;
import com.finalproject.demeter.util.MinorItemBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private PasswordTokenRepository passwordTokenRepository;
    private FoodItemRepository foodItemRepository;
    private InventoryRepository inventoryRepository;
    private MinorItemRepository minorItemRepository;
    private DislikedItemRepository dislikedItemRepository;
    private Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final Set<SimpleGrantedAuthority> authorities = new HashSet<>(){{
        add(new SimpleGrantedAuthority("user"));
    }};

    /**
     * This service is used to interact with users
     * */

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       PasswordTokenRepository passwordTokenRepository, FoodItemRepository foodItemRepository,
                       InventoryRepository inventoryRepository, MinorItemRepository minorItemRepository,
                       JwtUtil jwtUtil, DislikedItemRepository dislikedItemRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordTokenRepository = passwordTokenRepository;
        this.foodItemRepository = foodItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.minorItemRepository = minorItemRepository;
        this.jwtUtil = jwtUtil;
        this.dislikedItemRepository = dislikedItemRepository;
    }

    /**
     * This is used to add a user preference to the db. i.e. allergy, disliked item, or foods they cannot eat.
     * @param jwt: user JWT
     * @param foodItemId: id of the food item that needs to be saved
     * @return ResponseEntity with the status of the operation
     * */
    public ResponseEntity<?> addDislikedItem(String jwt, Long foodItemId) {
        Optional<User> user = getUserFromJwtToken(jwt);
        if (user.isPresent()) {
            if (foodItemId != null) {
                Optional<FoodItem> foodItem = foodItemRepository.findById(foodItemId);
                if (foodItem.isPresent()){
                    DislikedItem dislikedItem = new DislikedItemBuilder().user(user.get())
                            .foodItem(foodItem.get()).build();
                    dislikedItemRepository.save(dislikedItem);
                    return new ResponseEntity<>("Preference Saved", HttpStatus.OK);
                } else {
                    String errorMessage = String.format("FoodItem with id %d could not be found", foodItemId);
                    return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("You must provide the id of a food item", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("User could not be found", HttpStatus.NOT_FOUND);
    }

    /**
     * This is used to get all the disliked items from a user.
     * @param jwt: JWT for a user
     * @return Response Entity that either has the items or an error
     * */
    public ResponseEntity<?> getDislikedItems(String jwt) {
        Optional<User> user = getUserFromJwtToken(jwt);
        if (user.isPresent()) {
            Optional<List<DislikedItem>> dislikedItemsOpt = dislikedItemRepository.findByUser(user.get());
            if (dislikedItemsOpt.isPresent()) {
                return new ResponseEntity<>(dislikedItemsOpt.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return new ResponseEntity<>("User could not be found", HttpStatus.NOT_FOUND);
    }

    /**
     * Used to remove an item from the user's disliked items;
     * @param jwt: User JWT
     * @param foodItemId: id of the item that needs to be removed
     * @return ResponseEntity representing the status of the operation
     * */
    public ResponseEntity<?> removeDislikedItem(String jwt, Long foodItemId) {
        Optional<User> user = getUserFromJwtToken(jwt);
        if (user.isPresent()) {
            Optional<List<DislikedItem>> dislikedItemsOpt = dislikedItemRepository.findByUser(user.get());
            if (dislikedItemsOpt.isPresent()) {
                List<DislikedItem> dislikedItems = dislikedItemsOpt.get();
                for (DislikedItem item : dislikedItems) {
                    if (item.getFoodItem().getId() == foodItemId) {
                        dislikedItemRepository.delete(item);
                        return new ResponseEntity<>("Successful Removal", HttpStatus.OK);
                    }
                }
                String error = String.format(
                        "Item to be removed was not in the disliked items for user with email: %s",
                        user.get().getEmail()
                );
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Nothing to Remove", HttpStatus.OK);
        }
        return new ResponseEntity<>("User could not be found", HttpStatus.NOT_FOUND);
    }

    /**
     * Takes a JWT and retrieves the user associated with it.
     * @param jwtToken the passed jwt.
     * @return An optional containing the user if one could be found.
     * */
    public Optional<User> getUserFromJwtToken(String jwtToken){
        String email = jwtUtil.extractEmail(jwtToken.substring(7));
        return userRepository.findByEmail(email);
    }

    /**
     * Takes a username or email and tries to find the associated user.
     * @param usernameOrEmail The username or email of the desired user.
     * @return UserDetails object for the found user
     * */
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: "+ usernameOrEmail)
                );

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                authorities);
    }

    /**
     *  Allow the user to create an account.
     * @param signUpDto The data transfer object used to allow the user to create and account.
     * @return The response based on the success of the addition.
     * */
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

    /**
     * Used to turn a sign up object into a user and save that user.
     * @param signUpDto: The DTO used to sign a user up
     * @return NONE
     * */
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

    /**
     * Find a user by their email.
     * @param email: String representation of a users email
     * @return An optional of the user if one was found
     * */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Take a user and a generated token and save that user's token for password reset validation.
     * @param user: User whose password needs reset
     * @param token: The generated password reset token
     * @return NONE
     * */
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(user, token);
        passwordTokenRepository.save(myToken);
    }

    /**
     * Verify that a password reset token is valid.
     * @param token: the password reset token send by the user
     * @return a boolean representing if the user's token is valid
     * */
    public boolean isTokenValid(String token) {
        Optional<PasswordResetToken> pToken = passwordTokenRepository.findByToken(token);
        if (pToken.isPresent()){
            return !isTokenExpired(pToken.get());
        }
        return false;
    }

    /**
     * Retrieve the user that is associated with a password reset token.
     * @param token: the password reset token send by the user
     * @return an optional of the user if one was found
     * */
    public Optional<User> findUserByToken(String token){
        Optional<PasswordResetToken> pToken = passwordTokenRepository.findByToken(token);
        if (pToken.isPresent()) {
            return Optional.of(pToken.get().getUser());
        }
        return Optional.empty();
    }

    /**
     * Verify that a password reset token is not expired.
     * @param passToken: the password reset token send by the user
     * @return a boolean representing if the user's token is expired
     * */
    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    /**
     * Mark a food item as minor
     * @param jwtToken: user jwt
     * @param itemId: id of the food item to mark as minor
     * @param mark: string value of if the item should be added as minor or removed
     * @return a response entity on the status of the operation
     * */
    public ResponseEntity<String> markFoodItem(String jwtToken, Long itemId, String mark) {
        Optional<User> userOpt = getUserFromJwtToken(jwtToken);
        try {
            userOpt.ifPresentOrElse(user -> {
                Optional<FoodItem> foodItem = foodItemRepository.findById(itemId);
                if (foodItem.isPresent()) {
                    MinorItem newItem = new MinorItemBuilder().user(user).foodItem(foodItem.get()).build();
                    if (mark.equalsIgnoreCase("add")) {
                        minorItemRepository.save(newItem);
                    } else if (mark.equalsIgnoreCase("remove")) {
                        List<MinorItem> markedItems = minorItemRepository.findMinorItemsByUser(user);
                        markedItems.forEach(markedItem -> {
                            if (markedItem.getFoodItem().getId() == itemId) {
                                minorItemRepository.delete(markedItem);
                            }
                        });
                    } else {
                        throw new RuntimeException("Item not found");
                    }
                } else {
                    throw new RuntimeException("Item not found");
                }
            }, () -> {
                throw new RuntimeException("User not found");
            });
            return new ResponseEntity("Item Updated", HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Save Error: " + e.getMessage());
            return new ResponseEntity("Error Saving Item", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updated a users password
     * @param user: the user whose password needs reset
     * @param pass: the user's new password
     * @return NONE
     * */
    public void updateUserPassword(User user, String pass) {
        if (AuthUtil.isValidPassword(pass)) {
            user.setPassword(passwordEncoder.encode(pass));
            userRepository.save(user);
        }
    }

    /**
     * Used to add, remove, decrement, etc. a users inventory.
     * @param user: the user whose inventory needs updated
     * @param inventoryItem: the representation of the item that needs to update the inventory
     * @return a response entity that signifies the status of the operation
     * */
    public ResponseEntity<String> updateInventory(User user, UpdateInventory inventoryItem) {
        List<InventoryItem> inventory = inventoryRepository.findInventoryItemByUserId(user);
        InventoryItem foundItem = null;
        // This could likely be optimized
        for (InventoryItem item : inventory) {
            if (item.getFoodId().getId() == inventoryItem.getFoodId()) {
                foundItem = item;
                break;
            }
        }

        if (foundItem != null) {
            Float currentQuantity = foundItem.getQuantity();
            if (currentQuantity + inventoryItem.getQuantity() == 0) {
                inventoryRepository.delete(foundItem);
                return new ResponseEntity("Inventory Item was Removed", HttpStatus.OK);
            } else {
                if (currentQuantity + inventoryItem.getQuantity() < 0) {
                    return new ResponseEntity("Invalid Quantity", HttpStatus.BAD_REQUEST);
                }
                // update the inventory
                foundItem.setQuantity(currentQuantity + inventoryItem.getQuantity());
            }
        } else {
            // The user does not have the item in their current inventory
            if (inventoryItem.getQuantity() <= 0) { // and the added value is invalid
                return new ResponseEntity("Invalid Quantity", HttpStatus.BAD_REQUEST);
            }
            Optional<FoodItem> newItem = foodItemRepository.findById(inventoryItem.getFoodId());
            if (!newItem.isPresent()) {
                return new ResponseEntity("The given item does not exist", HttpStatus.NO_CONTENT);
            }
            if (inventoryItem.getUnit().equals("")) {
                return new ResponseEntity("Invalid Unit", HttpStatus.BAD_REQUEST);
            }
            // This could be replaced with a builder
            foundItem = new InventoryItem();
            foundItem.setUserId(user);
            foundItem.setFoodId(newItem.get());
            foundItem.setQuantity(inventoryItem.getQuantity());
        }
        foundItem.setUnit(inventoryItem.getUnit());
        inventoryRepository.save(foundItem);
        return new ResponseEntity("Inventory was saved", HttpStatus.OK);
    }

    /**
     * Get the inventory of a user.
     * @param user: the user who needs their inventory retrieved.
     * @return a list of the user's inventory items
     * */
    public List<InventoryItem> getInventory(User user) {
        return inventoryRepository.findInventoryItemByUserId(user);
    }
}
