package com.finalproject.demeter.service;

import com.finalproject.demeter.dao.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class MailService {
    private MessageSource messages;
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}") String supportEmail;

    @Autowired
    public MailService(MessageSource messages, JavaMailSender mailSender){
        this.messages = messages;
        this.mailSender = mailSender;
    }

    public SimpleMailMessage constructResetTokenEmail(
            String contextPath, String token, User user) {
        // This can also be changed to refect any url the frontend wants
        String url = contextPath + "/changePassword?token=" + token;
        String message = "Please click the below link to reset your password.";
        return constructEmail("Demeter: Reset Password", message + " \r\n" + url, user);
    }

    public String getAppUrl(HttpServletRequest request) {
        // This will need to be updated to have the url of the front end
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(supportEmail);
        return email;
    }

    public void sendMessage(SimpleMailMessage message) {
        this.mailSender.send(message);
    }
}
