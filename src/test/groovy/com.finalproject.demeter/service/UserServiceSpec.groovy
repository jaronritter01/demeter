package com.finalproject.demeter.service

import com.finalproject.demeter.dao.DislikedItem
import com.finalproject.demeter.dao.FoodItem
import com.finalproject.demeter.dao.InventoryItem
import com.finalproject.demeter.dao.MinorItem
import com.finalproject.demeter.dao.PasswordResetToken
import com.finalproject.demeter.dao.User
import com.finalproject.demeter.dao.UserPreference
import com.finalproject.demeter.dto.SignUpDto
import com.finalproject.demeter.dto.UpdateInventory
import com.finalproject.demeter.repository.DislikedItemRepository
import com.finalproject.demeter.repository.FoodItemRepository
import com.finalproject.demeter.repository.InventoryRepository
import com.finalproject.demeter.repository.MinorItemRepository
import com.finalproject.demeter.repository.PasswordTokenRepository
import com.finalproject.demeter.repository.UserPreferenceRepository
import com.finalproject.demeter.repository.UserRepository
import com.finalproject.demeter.util.DislikedItemBuilder
import com.finalproject.demeter.util.FoodItemBuilder
import com.finalproject.demeter.util.InventoryItemBuilder
import com.finalproject.demeter.util.JwtUtil
import com.finalproject.demeter.util.MinorItemBuilder
import com.finalproject.demeter.util.UserPreferencesBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import java.time.temporal.ChronoUnit

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()
    PasswordTokenRepository passwordTokenRepository = Mock()
    FoodItemRepository foodItemRepository = Mock()
    InventoryRepository inventoryRepository = Mock()
    MinorItemRepository minorItemRepository = Mock()
    JwtUtil jwtUtil = Mock()
    DislikedItemRepository dislikedItemRepository = Mock()
    UserPreferenceRepository userPreferenceRepository = Mock()
    UserService userService
    User user = new User()
    String userJWT = "randomRealWorkingJWT"
    FoodItem foodItem1 = new FoodItemBuilder().name("Test Food Item1").id(1L).description("Test food item desc1")
    .picUrl("emptyPicUrl.com/1").reusable(false).build()
    FoodItem foodItem2 = new FoodItemBuilder().name("Test Food Item2").id(2L).description("Test food item desc2")
            .picUrl("emptyPicUrl.com/2").reusable(false).build()
    FoodItem foodItem3 = new FoodItemBuilder().name("Test Food Item3").id(3L).description("Test food item desc3")
            .picUrl("emptyPicUrl.com/3").reusable(false).build()
    InventoryItem item1 = new InventoryItemBuilder().id(1L).userId(user).foodItem(foodItem1).unit("test1").quantity(10F).build()
    InventoryItem item2 = new InventoryItemBuilder().id(2L).userId(user).foodItem(foodItem2).unit("test2").quantity(10F).build()
    InventoryItem item3 = new InventoryItemBuilder().id(3L).userId(user).foodItem(foodItem3).unit("test3").quantity(10F).build()
    UserPreference userPreference = new UserPreferencesBuilder().id(1L).user(user).isMetric(true).build()

    void setup(){
        userService = new UserService(userRepository, passwordEncoder, passwordTokenRepository, foodItemRepository,
                inventoryRepository, minorItemRepository, jwtUtil, dislikedItemRepository, userPreferenceRepository)
        user.username = "jsmith"
        user.password = "testingPassword1!"
        user.firstName = "John"
        user.lastName = "Smith"
        user.email = "johns@gmail.com"
    }

    def "When a valid user requests to change null as the preference it should return an error" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        userPreferenceRepository.findByUser(user) >> Optional.of(userPreference)

        when:
        ResponseEntity<?> re = userService.setUserPreferences(userJWT, null)

        then:
        re.statusCode == HttpStatus.BAD_REQUEST
        re.body == "Not a valid field"
        0 * userPreferenceRepository.save(_)
    }

    def "When an invalid used requests to change a preference, an error should be returned" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.setUserPreferences(userJWT, "unit")

        then:
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "User was not found"
        0 * userPreferenceRepository.save(_)
    }

    def "When a valid user requests to change a valid preference, but no preference exists it should be created" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        userPreferenceRepository.findByUser(user) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.setUserPreferences(userJWT, "unit")

        then:
        re.statusCode == HttpStatus.OK
        re.body == "User preferences were created"
        1 * userPreferenceRepository.save(_)
    }

    def "When a valid user requests to change a valid preference, and their preference exists it should be changed" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        userPreferenceRepository.findByUser(user) >> Optional.of(userPreference)

        when:
        ResponseEntity<?> re = userService.setUserPreferences(userJWT, "unit")

        then:
        re.statusCode == HttpStatus.OK
        re.body == "User Preferences Successfully saved"
        1 * userPreferenceRepository.save(_)
    }

    def "When an invalid user requests their preferences, an error should be returned" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.getUserPreferences(userJWT)

        then:
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "User was not found."
    }

    def "When a valid user requests their preferences but they have none, a default preference should be returned" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        userPreferenceRepository.findByUser(user) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.getUserPreferences(userJWT)

        then:
        re.statusCode == HttpStatus.OK
        re.body == new UserPreference()
    }

    def "When a valid user requests their preferences, they should be returned" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        userPreferenceRepository.findByUser(user) >> Optional.of(userPreference)

        when:
        ResponseEntity<?> re = userService.getUserPreferences(userJWT)

        then:
        re.statusCode == HttpStatus.OK
        re.body == userPreference
    }

    def "When a valid name and indicator are passed, but the user cannot be found, the users name should not be updated" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.updateName(userJWT, "name", "first")

        then:
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "User could not be found"
        0 * userRepository.save(_)
    }

    def "When a valid user and indicator are passed, but the name is not valid, the users name should not be updated" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)

        when:
        ResponseEntity<?> re = userService.updateName(userJWT, "Name()That^doesn't work<", "notlastorfirst")

        then:
        re.statusCode == HttpStatus.BAD_REQUEST
        re.body == "Not a valid name"
        0 * userRepository.save(_)
    }

    def "When a valid user, name, but a bad indicator is passed, the users name should not be updated" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)

        when:
        ResponseEntity<?> re = userService.updateName(userJWT, "fakename", "notlastorfirst")

        then:
        re.statusCode == HttpStatus.BAD_REQUEST
        re.body == "Invalid name identifier"
        0 * userRepository.save(_)
    }

    def "When a valid user, name, and indicator are passed, the users name should be updated" (String name, String indicator) {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)

        when:
        ResponseEntity<?> re = userService.updateName(userJWT, name, indicator)

        then:
        re.statusCode == HttpStatus.OK
        re.body == "Name was updated"
        1 * userRepository.save(_)

        where:
        name       | indicator
        "fakename" | "first"
        "FakeName" | "last"
    }

    def "When a valid user is passed and item id, the disliked item should be removed" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        DislikedItem dl = new DislikedItemBuilder().id(1L).user(user).foodItem(foodItem1).build()
        List<DislikedItem> dislikedItems = List.of(dl)
        dislikedItemRepository.findByUser(user) >> Optional.of(dislikedItems)

        when:
        ResponseEntity<?> re = userService.removeDislikedItem(userJWT, 1L)

        then:
        1 * dislikedItemRepository.delete(_)
        re.statusCode == HttpStatus.OK
        re.body == "Successful Removal"
    }

    def "When a valid user is passed and item id but the user has no disliked items, no items should try to be removed" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        dislikedItemRepository.findByUser(user) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.removeDislikedItem(userJWT, 1L)

        then:
        0 * dislikedItemRepository.delete(_)
        re.statusCode == HttpStatus.OK
        re.body == "Nothing to Remove"
    }

    def "When an valid user is passed, no items should try to be removed" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.removeDislikedItem(userJWT, 1L)

        then:
        0 * dislikedItemRepository.delete(_)
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "User could not be found"
    }

    def "When a valid user is passed and item id but the user doesn't have that item disliked, the disliked item should not be removed" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        DislikedItem dl = new DislikedItemBuilder().id(1L).user(user).foodItem(foodItem1).build()
        List<DislikedItem> dislikedItems = List.of(dl)
        dislikedItemRepository.findByUser(user) >> Optional.of(dislikedItems)

        when:
        ResponseEntity<?> re = userService.removeDislikedItem(userJWT, 2L)

        then:
        0 * dislikedItemRepository.delete(_)
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "Item to be removed was not in the disliked items for user with email: " + user.email
    }

    def "When a valid user is passed, the disliked item should be returned" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        DislikedItem dl = new DislikedItemBuilder().id(1L).user(user).foodItem(foodItem1).build()
        List<DislikedItem> dislikedItems = List.of(dl)
        dislikedItemRepository.findByUser(user) >> Optional.of(dislikedItems)

        when:
        ResponseEntity<?> re = userService.getDislikedItems(userJWT)

        then:
        re.statusCode == HttpStatus.OK
        re.body == dislikedItems
    }

    def "When an invalid user is passed, An error message and a 404 should be returned" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.getDislikedItems(userJWT)

        then:
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "User could not be found"
    }

    def "When a valid user is passed but they have no disliked items, an empty array should be returned" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        dislikedItemRepository.findByUser(user) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.getDislikedItems(userJWT)

        then:
        re.statusCode == HttpStatus.OK
        re.body instanceof ArrayList
        ((List) re.body).size() == 0
    }

    def "When a valid item and user are passed, the disliked item should be saved" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        foodItemRepository.findById(1L) >> Optional.of(foodItem1)

        when:
        ResponseEntity<?> re = userService.addDislikedItem(userJWT, 1L)

        then:
        re.statusCode == HttpStatus.OK
        re.body == "Preference Saved"
        1 * dislikedItemRepository.save(_)
    }

    def "When a valid item and invalid user are passed, the disliked item should not be saved" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.addDislikedItem(userJWT, 1L)

        then:
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "User could not be found"
        0 * dislikedItemRepository.save(_)
    }

    def "When no item and a valid user are passed, the disliked item should not be saved" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)

        when:
        ResponseEntity<?> re = userService.addDislikedItem(userJWT, null)

        then:
        re.statusCode == HttpStatus.BAD_REQUEST
        re.body == "You must provide the id of a food item"
        0 * dislikedItemRepository.save(_)
    }

    def "When a valid item and a valid user are passed, but the food item does not exist, the disliked item should not be saved" () {
        given:
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        foodItemRepository.findById(1L) >> Optional.empty()

        when:
        ResponseEntity<?> re = userService.addDislikedItem(userJWT, 1L)

        then:
        re.statusCode == HttpStatus.NOT_FOUND
        re.body == "FoodItem with id 1 could not be found"
        0 * dislikedItemRepository.save(_)
    }

    def "when a valid user and password are passed, the password should be encrypted and saved" () {
        given:
        String validPass = "Testpassword1!"

        when:
        userService.updateUserPassword(user, validPass)

        then:
        1 * passwordEncoder.encode(validPass)
        1 * userRepository.save(user)
    }

    def "when a valid user and invalid password are passed, the password shoud not be saved" () {
        given:
        String validPass = "badpass"

        when:
        userService.updateUserPassword(user, validPass)

        then:
        0 * passwordEncoder.encode(validPass)
        0 * userRepository.save(user)
    }

    def "when an email is passed, findByEmail should be called"() {
        when:
        userService.findUserByEmail(user.email)

        then:
        1 * userRepository.findByEmail(user.email)
    }

    def "when a valid user passed, their inventory should be found"() {
        given:
        userPreferenceRepository.findByUser(user) >> Optional.empty()
        when:
        userService.getInventory(user)

        then:
        1 * inventoryRepository.findInventoryItemByUserId(user) >> new ArrayList<>()
    }

    def "when a valid user and update item are passed and the user has the items, a 200 should be returned (update add)" (){
        given:
        List<InventoryItem> userInventory = List.of(item1, item2, item3)
        inventoryRepository.findInventoryItemByUserId(user) >> userInventory
        UpdateInventory ui = new UpdateInventory()
        ui.setFoodId(1)
        ui.setQuantity(2)
        ui.setUnit("test1")

        when:
        ResponseEntity<String> re = userService.updateInventory(user, ui)

        then:
        re.body == "Inventory was saved"
        re.statusCode == HttpStatus.OK
        1 * inventoryRepository.save(_)
    }

    def "when a valid user and update item are passed, a 200 should be returned (add)" (){
        given:
        List<InventoryItem> userInventory = List.of(item1, item2, item3)
        inventoryRepository.findInventoryItemByUserId(user) >> userInventory
        foodItemRepository.findById(6) >> Optional.of(new FoodItemBuilder().id(6).build())
        UpdateInventory ui = new UpdateInventory()
        ui.setFoodId(6)
        ui.setQuantity(2)
        ui.setUnit("test6")

        when:
        ResponseEntity<String> re = userService.updateInventory(user, ui)

        then:
        re.body == "Inventory was saved"
        re.statusCode == HttpStatus.OK
        1 * inventoryRepository.save(_)
    }

    def "when a valid user and update item are passed, but the item cannot be found a 204 should be returned" (){
        given:
        List<InventoryItem> userInventory = List.of(item1, item2, item3)
        inventoryRepository.findInventoryItemByUserId(user) >> userInventory
        foodItemRepository.findById(6) >> Optional.empty()
        UpdateInventory ui = new UpdateInventory()
        ui.setFoodId(6)
        ui.setQuantity(2)
        ui.setUnit("test6")

        when:
        ResponseEntity<String> re = userService.updateInventory(user, ui)

        then:
        re.body == "The given item does not exist"
        re.statusCode == HttpStatus.NO_CONTENT
        0 * inventoryRepository.save(_)
        0 * inventoryRepository.delete(_)
    }

    def "when a valid user and update item are passed, but the unit is empty, 204 should be returned" (){
        given:
        userPreferenceRepository.findByUser(user) >> Optional.empty()
        List<InventoryItem> userInventory = List.of(item1, item2, item3)
        inventoryRepository.findInventoryItemByUserId(user) >> userInventory
        foodItemRepository.findById(6) >> Optional.of(new FoodItemBuilder().id(6).build())
        UpdateInventory ui = new UpdateInventory()
        ui.setFoodId(6)
        ui.setQuantity(2)
        ui.setUnit("")

        when:
        ResponseEntity<String> re = userService.updateInventory(user, ui)

        then:
        re.body == "Invalid Unit"
        re.statusCode == HttpStatus.BAD_REQUEST
        0 * inventoryRepository.save(_)
        0 * inventoryRepository.delete(_)
    }

    def "when a valid user and update item are passed, but the quantity is negative, 204 should be returned" (){
        given:
        List<InventoryItem> userInventory = List.of(item1, item2, item3)
        inventoryRepository.findInventoryItemByUserId(user) >> userInventory
        foodItemRepository.findById(6) >> Optional.of(new FoodItemBuilder().id(6).build())
        UpdateInventory ui = new UpdateInventory()
        ui.setFoodId(6)
        ui.setQuantity(-2)
        ui.setUnit("test1")

        when:
        ResponseEntity<String> re = userService.updateInventory(user, ui)

        then:
        re.body == "Invalid Quantity"
        re.statusCode == HttpStatus.BAD_REQUEST
        0 * inventoryRepository.save(_)
        0 * inventoryRepository.delete(_)
    }

    def "when a valid user and update item are passed and the user has the items, a 200 should be returned (delete)" (){
        given:
        List<InventoryItem> userInventory = List.of(item1, item2, item3)
        inventoryRepository.findInventoryItemByUserId(user) >> userInventory
        UpdateInventory ui = new UpdateInventory()
        ui.setFoodId(1)
        ui.setQuantity(-10)
        ui.setUnit("test1")

        when:
        ResponseEntity<String> re = userService.updateInventory(user, ui)

        then:
        re.body == "Inventory Item was Removed"
        re.statusCode == HttpStatus.OK
        1 * inventoryRepository.delete(_)
    }

    def "when a valid user and invalid update item are passed, a 400 should be returned" (){
        given:
        List<InventoryItem> userInventory = List.of(item1, item2, item3)
        inventoryRepository.findInventoryItemByUserId(user) >> userInventory
        UpdateInventory ui = new UpdateInventory()
        ui.setFoodId(1)
        ui.setQuantity(-11)
        ui.setUnit("test1")

        when:
        ResponseEntity<String> re = userService.updateInventory(user, ui)

        then:
        re.body == "Invalid Quantity"
        re.statusCode == HttpStatus.BAD_REQUEST
        0 * inventoryRepository.delete(_)
        0 * inventoryRepository.save(_)
    }

    def "when a valid jwt, food item, and add is the mark value, a 200 should be returned" (){
        given:
        Integer foodItemId = 1
        String mark = "add"
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        foodItemRepository.findById(foodItemId) >> Optional.of(foodItem1)

        when:
        ResponseEntity re = userService.markFoodItem(userJWT, foodItemId, mark)

        then:
        1 * minorItemRepository.save(_)
        re.getStatusCode() == HttpStatus.OK
        re.body == "Item Updated"
    }

    def "when a valid jwt, food item, and invalid mark value is passed, a 400 should be returned" (){
        given:
        Integer foodItemId = 1
        String mark = "invalidMark"
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        foodItemRepository.findById(foodItemId) >> Optional.of(foodItem1)

        when:
        ResponseEntity re = userService.markFoodItem(userJWT, foodItemId, mark)

        then:
        0 * minorItemRepository.save(_)
        0 * minorItemRepository.delete(_)
        re.getStatusCode() == HttpStatus.BAD_REQUEST
        re.body == "Error Saving Item"
    }

    def "when a valid jwt, food item, and remove is the mark value, a 200 should be returned" (){
        given:
        Integer foodItemId = 1
        String mark = "remove"
        MinorItem mi = new MinorItemBuilder().id(1).user(user).foodItem(foodItem1).build()
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        foodItemRepository.findById(foodItemId) >> Optional.of(foodItem1)
        minorItemRepository.findMinorItemsByUser(user) >> List.of(mi)

        when:
        ResponseEntity re = userService.markFoodItem(userJWT, foodItemId, mark)

        then:
        1 * minorItemRepository.delete(_)
        re.getStatusCode() == HttpStatus.OK
        re.body == "Item Updated"
    }

    def "when a valid jwt is passed and add is the mark value, but an invalid item id is passed a 400 should be returned" (){
        given:
        Integer foodItemId = 10
        String mark = "add"
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.of(user)
        foodItemRepository.findById(_) >> Optional.empty()

        when:
        ResponseEntity re = userService.markFoodItem(userJWT, foodItemId, mark)

        then:
        0 * minorItemRepository.save(_)
        re.getStatusCode() == HttpStatus.BAD_REQUEST
        re.body == "Error Saving Item"
    }

    def "when an invalid jwt is passed a 400 should be returned" (){
        given:
        Integer foodItemId = 1
        String mark = "add"
        jwtUtil.extractEmail(_) >> ""
        userRepository.findByEmail(_) >> Optional.empty()
        foodItemRepository.findById(foodItemId) >> Optional.of(foodItem1)

        when:
        ResponseEntity re = userService.markFoodItem(userJWT, foodItemId, mark)

        then:
        0 * minorItemRepository.save(_)
        re.getStatusCode() == HttpStatus.BAD_REQUEST
        re.body == "Error Saving Item"
    }

    def "a valid password reset token is passed, the user should be returned" (){
        given:
        String token = "superFakeTokenThatWorks"
        PasswordResetToken pToken = new PasswordResetToken()
        pToken.setId(1)
        pToken.setToken(token)
        pToken.setUser(user)
        passwordTokenRepository.findByToken(token) >> Optional.of(pToken)

        when:
        Optional<User> userOpt = userService.findUserByToken(token)

        then:
        userOpt.get() == user

    }

    def "an invalid password reset token is passed, a user should not be returned" (){
        given:
        String token = "superFakeTokenThatWorks"
        passwordTokenRepository.findByToken(token) >> Optional.empty()

        when:
        Optional<User> userOpt = userService.findUserByToken(token)

        then:
        userOpt == Optional.empty()

    }

    def "this should return a userDetails with the email of the user" () {
        given:
        userRepository.findByUsernameOrEmail(user.username, user.username) >> Optional.of(user)

        when:
        UserDetails userDetails = userService.loadUserByUsername(user.username)

        then:
        user.email == userDetails.username
    }

    def "this should return a UsernameNotFoundException" () {
        given:
        String badUsername = "notarealuser"
        and:
        userRepository.findByUsernameOrEmail(badUsername, badUsername) >> Optional.ofNullable(null)

        when:
        userService.loadUserByUsername(badUsername)

        then:
        final UsernameNotFoundException exception = thrown()
        exception.message == "User not found with username or email: " + badUsername
    }

    def "this should return that the username is already taken" () {
        given:
        SignUpDto newUser = new SignUpDto()
        newUser.username = "jSmith"
        and:
        userRepository.existsByUsername(newUser.getUsername()) >> true

        when:
        ResponseEntity<?> response = userService.addUser(newUser)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST && response.body == "Username is already taken!"

    }

    def "this should return that the information provided does not meet the requirements" () {
        given:
        SignUpDto newUser = new SignUpDto()
        newUser.username = "jSmith"
        newUser.password = "notagoodpassword"
        and:
        userRepository.existsByUsername(newUser.getUsername()) >> false
        userRepository.existsByEmail(newUser.getEmail()) >> false

        when:
        ResponseEntity<String> response = userService.addUser(newUser)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body == "User information does not meet the necessary requirements"

    }

    def "this should return that the email is already taken" () {
        given:
        SignUpDto newUser = new SignUpDto()
        newUser.username = "jSmith"
        newUser.email = "johns@gmail.com"
        and:
        userRepository.existsByUsername(_) >> false
        userRepository.existsByEmail(newUser.getEmail()) >> true

        when:
        ResponseEntity<String> response = userService.addUser(newUser)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST && response.body == "Email is already taken!"

    }

    def "this should successfully register a user" () {
        given:
        SignUpDto newUser = new SignUpDto()
        newUser.username = "jSmith"
        newUser.password = "testingPassword1!"
        newUser.firstName = "John"
        newUser.lastName = "Smith"
        newUser.email = "johns@gmail.com"

        and:
        userRepository.existsByUsername(newUser.getUsername()) >> false

        and:
        userRepository.existsByEmail(newUser.getEmail()) >> false

        and:
        userRepository.save(user) >> user

        when:
        ResponseEntity<String> response = userService.addUser(newUser)

        then:
        response.statusCode == HttpStatus.CREATED && response.body == "User registered successfully"
    }

    def "Given a valid passwordResetToken String, A password reset otken should be created and saved" (){
        given:
        String token = UUID.randomUUID().toString()

        when:
        userService.createPasswordResetTokenForUser(user, token)

        then:
        1 * passwordTokenRepository.save(_)
    }

    def "Given a valid token, isTokenValid should return true"() {
        given:
        String token = UUID.randomUUID().toString()
        PasswordResetToken pToken = new PasswordResetToken(user, token)
        passwordTokenRepository.findByToken(token) >> Optional.of(pToken)

        when:
        Boolean isValid = userService.isTokenValid(token)

        then:
        isValid
    }

    def "Given an invalid token, isTokenValid should return false"() {
        given:
        String token = UUID.randomUUID().toString()
        PasswordResetToken pToken = new PasswordResetToken(user, token)
        Date newDate = Date.from(pToken.getExpiryDate().toInstant().minus(1, ChronoUnit.HOURS))
        pToken.setExpiryDate(newDate)
        passwordTokenRepository.findByToken(token) >> Optional.of(pToken)

        when:
        Boolean isValid = userService.isTokenValid(token)

        then:
        !isValid
    }

    def "Given an invalid token and isTokenValid could not be found"() {
        given:
        String token = UUID.randomUUID().toString()
        passwordTokenRepository.findByToken(token) >> Optional.empty()

        when:
        Boolean isValid = userService.isTokenValid(token)

        then:
        !isValid
    }
}
