package com.finalproject.demeter.controller;

import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.dto.LoginDto;
import com.finalproject.demeter.dto.PasswordUpdate;
import com.finalproject.demeter.dto.SignUpDto;
import com.finalproject.demeter.dto.UserLoginInfo;
import com.finalproject.demeter.service.MailService;
import com.finalproject.demeter.service.UserService;
import com.finalproject.demeter.util.AuthUtil;
import com.finalproject.demeter.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("v1/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtUtil jwtUtils = new JwtUtil();
    private MailService mailService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          MailService mailService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestBody String email){
        // This is currently set up as though the reset page is being served from the api
        if (!AuthUtil.isValidEmail(email.toLowerCase())){
            return new ResponseEntity("Please Send a Valid email", HttpStatus.BAD_REQUEST);
        }

        userService.findUserByEmail(email.toLowerCase()).ifPresent((user) -> {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            SimpleMailMessage message = mailService.constructResetTokenEmail(
                    mailService.getAppUrl(request), token, user
            );
            mailService.sendMessage(message);
        });

        return new ResponseEntity("Message Sent if User exists", HttpStatus.OK);
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdate passwordUpdate){
        if (!userService.isTokenValid(passwordUpdate.getToken())){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Optional<User> userPresent = userService.findUserByToken(passwordUpdate.getToken());
        if (userPresent.isPresent()) {
            User user = userPresent.get();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            passwordUpdate.getOldPassword()
                    )
            );

            if (!authentication.isAuthenticated()){
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }

            if (!AuthUtil.isValidPassword(passwordUpdate.getNewPassword()) ||
                    passwordUpdate.getOldPassword().equals(passwordUpdate.getNewPassword())) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            userService.updateUserPassword(user, passwordUpdate.getNewPassword());
        }
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Get the user
        UserDetails userDetails = userService.loadUserByUsername(loginDto.getUsername());
        Optional<User> userOpt = userService.findByUsername(loginDto.getUsername());
        // Generate jwtToken
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String jwtToken = jwtUtils.generateToken(userDetails);
            String jwtReturnToken = new StringBuilder().append("Bearer:").append(jwtToken).toString();
            // Return Token to front end
            UserLoginInfo returnInfo = new UserLoginInfo();
            returnInfo.setToken(jwtReturnToken);
            returnInfo.setFirstName(user.getFirstName());
            returnInfo.setLastName(user.getLastName());
            returnInfo.setEmail(user.getEmail());
            return new ResponseEntity<>(returnInfo, HttpStatus.OK);
        }
        // This in theory should never get hit
        return new ResponseEntity<>("No User Found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        return userService.addUser(signUpDto);
    }
}