package com.finalproject.demeter.service

import com.finalproject.demeter.dao.User
import org.springframework.context.MessageSource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification

class MailServiceSpec extends Specification{
    private MessageSource messageSource = Mock()
    private JavaMailSender mailSender = Mock()
    private MailService mailService = new MailService(messageSource, mailSender)
    private User user = new User()

    void setup(){
        user.username = "jSmith"
        user.password = "testingPassword1!"
        user.firstName = "John"
        user.lastName = "Smith"
        user.email = "johns@gmail.com"
    }

    def "constructEmail should return a valid simpleMailMessage" () {
        when:
        SimpleMailMessage message = mailService.constructEmail("test subject", "test body", user)

        then:
        message.getSubject() == "test subject"
    }
}
