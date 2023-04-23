package com.finalproject.demeter.controller;

import com.finalproject.demeter.dto.LoginDto;
import com.finalproject.demeter.dto.PasswordUpdate;
import com.finalproject.demeter.dto.SignUpDto;
import com.finalproject.demeter.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/auth")
public class AuthController {
    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestBody String email){
        return authService.resetPassword(request, email);
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdate passwordUpdate){
        return authService.updatePassword(passwordUpdate);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto){
        return authService.authenticateUser(loginDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        return authService.registerUser(signUpDto);
    }
}