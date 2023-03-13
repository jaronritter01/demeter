package com.finalproject.demeter.repository

import com.finalproject.demeter.dao.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class UserRepositorySpec extends Specification {

    @Shared
    private UserRepository userRepository

    @Autowired
    UserServiceSpecification (UserRepository userRepository) {
        this.userRepository = userRepository
    }

    void setup() {
        User user = new User();
        user.username = "jSmith"
        user.password = "testingPassword1!"
        user.firstName = "John"
        user.lastName = "Smith"
        user.email = "johns@gmail.com"

        userRepository.save(user)
    }

    void cleanupSpec() {
        userRepository.deleteAll()
    }

    def "This should find a user by their email"() {
        given:
        String email = "johns@gmail.com"

        when:
        User user = userRepository.findByEmail(email).get()

        then:
        user.email != null
        user.firstName != null
        user.lastName != null
        user.username != null
        user.password != null
    }

    def "this should return the same user by email and username" () {
        given:
        String username = "jSmith"

        and:
        String email = "johns@gmail.com"

        when:
        User user = userRepository.findByUsernameOrEmail(username, email).get()
        and:
        User userByUsername = userRepository.findByUsername(username).get()
        and:
        User userByEmail = userRepository.findByEmail(email).get()

        then:
        userByEmail == userByUsername && userByUsername == user
    }

    def "A user should be returned when a valid email is given"() {
        given:
        String email = "johns@gmail.com"

        when:
        Boolean exists = userRepository.existsByEmail(email)

        then:
        exists
    }

}
