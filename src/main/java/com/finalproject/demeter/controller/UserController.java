package com.finalproject.demeter.controller;

import com.finalproject.demeter.dto.NameUpdate;
import com.finalproject.demeter.dto.PreferenceUpdate;
import com.finalproject.demeter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("v1/api/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Used to update a user's name
     * @param jwt - Token needed to authenticate a user.
     * @param nameUpdate - dto NameUpdate holding the newName, and nameIndicator
     * @return: A ResponseEntity entity with operation status
     * */
    @PostMapping("/updateName")
    public ResponseEntity<?> updateName(@RequestHeader("AUTHORIZATION") String jwt,
                                        @RequestBody NameUpdate nameUpdate) {
        return userService.updateName(jwt, nameUpdate.getNewName(), nameUpdate.getNameIndicator());
    }

    /**
     * Used to get a users preferences
     * @param jwt - Token needed to authenticate a user.
     * @return: A ResponseEntity entity with a users preferences
     * */
    @PostMapping("/getUserPreferences")
    public ResponseEntity<?> getUserPreferences(@RequestHeader("AUTHORIZATION") String jwt) {
        return userService.getUserPreferences(jwt);
    }

    /**
     * Used to set a users preferences
     * @param jwt - Token needed to authenticate a user.
     * @return: A ResponseEntity entity with operation status
     * */
    @PostMapping("/setUserPreferences")
    public ResponseEntity<?> setUserPreferences(@RequestHeader("AUTHORIZATION") String jwt,
                                                @RequestBody PreferenceUpdate preferenceUpdate) {
        return userService.setUserPreferences(jwt, preferenceUpdate.getFieldToUpdate());
    }
}
