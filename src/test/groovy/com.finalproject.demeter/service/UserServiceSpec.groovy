package com.finalproject.demeter.service

import com.finalproject.demeter.dao.User
import com.finalproject.demeter.dto.SignUpDto
import com.finalproject.demeter.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceSpec extends Specification {

    private UserRepository userRepository
    private PasswordEncoder passwordEncoder
    private UserService userService
    private User user = new User()


    void setup(){
        userRepository = Stub(UserRepository.class)
        passwordEncoder = Mock(PasswordEncoder.class)
        userService = new UserService(userRepository, passwordEncoder)
        user.username = "jSmith"
        user.password = "testingPassword1!"
        user.firstName = "John"
        user.lastName = "Smith"
        user.email = "johns@gmail.com"
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
        ResponseEntity<String> response = userService.addUser(newUser)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST && response.body == "Username is already taken!"

    }

    def "this should return that the email is already taken" () {
        given:
        SignUpDto newUser = new SignUpDto()
        newUser.username = "jSmith"
        newUser.email = "johns@gmail.com"
        and:
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
}
