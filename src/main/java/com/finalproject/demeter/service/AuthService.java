package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.User;
import com.finalproject.demeter.dto.LoginDto;
import com.finalproject.demeter.dto.PasswordUpdate;
import com.finalproject.demeter.dto.SignUpDto;
import com.finalproject.demeter.dto.UserLoginInfo;
import com.finalproject.demeter.util.AuthUtil;
import com.finalproject.demeter.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtUtil jwtUtils = new JwtUtil();
    private MailService mailService;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, MailService mailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
    }

    /**
     * Used to reset the password of a user.
     * @param request the request that the user sent for the password reset.
     * @param email the email of the user.
     * @return ResponseEntity with the status of the operation.
     * */
    public ResponseEntity<String> resetPassword(HttpServletRequest request, String email) {
        // This is currently set up as though the reset page is being served from the api
        if (!AuthUtil.isValidEmail(email.toLowerCase())){
            return new ResponseEntity<>("Please Send a Valid email", HttpStatus.BAD_REQUEST);
        }

        userService.findUserByEmail(email.toLowerCase()).ifPresent((user) -> {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            SimpleMailMessage message = mailService.constructResetTokenEmail(
                    mailService.getAppUrl(request), token, user
            );
            mailService.sendMessage(message);
        });

        return new ResponseEntity<>("Message Sent if User exists", HttpStatus.OK);
    }

    /**
     * Used to update the user's password.
     * @param passwordUpdate DTO that has the users reset token, new and old password in it.
     * @return ResponseEntity that represents the status of the reset.
     * */
    public ResponseEntity<Void> updatePassword(PasswordUpdate passwordUpdate) {
        if (!userService.isTokenValid(passwordUpdate.getToken())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (!AuthUtil.isValidPassword(passwordUpdate.getNewPassword()) ||
                    passwordUpdate.getOldPassword().equals(passwordUpdate.getNewPassword())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            userService.updateUserPassword(user, passwordUpdate.getNewPassword());
            userService.removePasswordResetToken(passwordUpdate.getToken());
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Used to authenticate a user.
     * @param loginDto the DTO that contains user login info.
     * @return ResponseEntity that represents the status of the operation.
     * */
    public ResponseEntity<?> authenticateUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername().toLowerCase(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Get the user
        UserDetails userDetails = userService.loadUserByUsername(loginDto.getUsername().toLowerCase());
        Optional<User> userOpt = userService.findByUsername(loginDto.getUsername().toLowerCase());
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

    /**
     * Used to register a user in the system.
     * @param signUpDto user registration info.
     * @return ResponseEntity that represents the status of the operation.
     * */
    public ResponseEntity<?> registerUser(SignUpDto signUpDto){
        return userService.addUser(signUpDto);
    }
}
