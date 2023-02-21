package com.finalproject.demeter.dto;

import lombok.Data;

@Data
public class PasswordUpdate {
    private String token;
    private String oldPassword;
    private String newPassword;
}
