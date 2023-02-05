package com.finalproject.demeter.util

import com.finalproject.demeter.dto.SignUpDto
import spock.lang.Specification

class AuthUtilSpec extends Specification{
    def "A valid user should return true" (){
        given:
        SignUpDto user = new SignUpDto()
        user.firstName = "Jim"
        user.lastName = "Smith";
        user.username = "JSmith20";
        user.email = "jsmith@gmail.com"
        user.password = "SuperUniquePass1!"

        when:
        def isValid = AuthUtil.isValidUser(user)

        then:
        isValid
    }

    def "A valid user with bad password should return false" (){
        given:
        SignUpDto user = new SignUpDto()
        user.firstName = "Jim"
        user.lastName = "Smith";
        user.username = "JSmith20";
        user.email = "jsmith@gmail.com"
        user.password = "password"

        when:
        def isValid = AuthUtil.isValidUser(user)

        then:
        !isValid
    }

    def "A valid user with invalid email should return false" (){
        given:
        SignUpDto user = new SignUpDto()
        user.firstName = "Jim"
        user.lastName = "Smith";
        user.username = "JSmith20";
        user.email = "jsmith@gmail"
        user.password = "SuperUniquePass1!"

        when:
        def isValid = AuthUtil.isValidUser(user)

        then:
        !isValid
    }

    def "A valid user with a short username should return false" (){
        given:
        SignUpDto user = new SignUpDto()
        user.firstName = "Jim"
        user.lastName = "Smith";
        user.username = "JSm";
        user.email = "jsmith@gmail"
        user.password = "SuperUniquePass1!"

        when:
        def isValid = AuthUtil.isValidUser(user)

        then:
        !isValid
    }
}
