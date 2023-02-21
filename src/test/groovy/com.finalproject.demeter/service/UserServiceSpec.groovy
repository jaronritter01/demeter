package com.finalproject.demeter.service

import com.finalproject.demeter.dao.PasswordResetToken
import com.finalproject.demeter.dao.User
import com.finalproject.demeter.dto.SignUpDto
import com.finalproject.demeter.repository.PasswordTokenRepository
import com.finalproject.demeter.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import java.time.temporal.ChronoUnit

class UserServiceSpec extends Specification {

    private UserRepository userRepository
    private PasswordEncoder passwordEncoder
    private UserService userService
    private PasswordTokenRepository passwordTokenRepository
    private User user = new User()


    void setup(){
        userRepository = Stub(UserRepository.class)
        passwordTokenRepository = Mock(PasswordTokenRepository.class)
        passwordEncoder = Mock(PasswordEncoder.class)
        userService = new UserService(userRepository, passwordEncoder, passwordTokenRepository)
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
}
