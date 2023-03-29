package com.finalproject.demeter.dto;

import lombok.Data;

@Data
public class UserLoginInfo {
    private String token;
    private String firstName;
    private String lastName;
    private String email;
}
