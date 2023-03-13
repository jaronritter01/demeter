package com.finalproject.demeter.dao;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Data
@Entity
public class PasswordResetToken {
    public PasswordResetToken(){}

    public PasswordResetToken(User user, String token){
        this.user = user;
        this.token = token;
        this.expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
    }
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private String token;
 
    @OneToOne
    private User user;
 
    private Date expiryDate;
}