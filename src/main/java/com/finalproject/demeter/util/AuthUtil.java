package com.finalproject.demeter.util;

import com.finalproject.demeter.dto.SignUpDto;
import java.util.regex.Pattern;

public class AuthUtil {
    // Need to write tests for this
    private static final Pattern SPECIALCHARREGEX = Pattern.compile("[$&+,:;=?@#|'<>.^*()%!-]");
    private static final Pattern EMAILREGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    private static final Pattern NAMEREGEX = Pattern.compile("^[a-zA-Z,.'-]+$");
    public static Boolean isValidUser(SignUpDto user){
        return isValidPassword(user.getPassword()) && isValidEmail(user.getEmail())
                && isValidUsername(user.getUsername());
    }

    public static boolean isValidName(String name){
        return name != null && name.length() > 0 && NAMEREGEX.matcher(name).find();
    }

    private static Boolean isValidUsername(String username){
        return username.length() > 4 && username.length() < 25 && !Character.isDigit(username.charAt(0));
    }
    public static Boolean isValidPassword(String password){
        return password.length() > 8 && password.length() < 25
                && containsUpperCaseLetter(password) && containsSpecialChar(password);
    }

    private static Boolean containsUpperCaseLetter(String password){
        for (char i : password.toCharArray()){
            if (Character.isLetter(i) && Character.isUpperCase(i)){
                return true;
            }
        }

        return false;
    }

    private static Boolean containsSpecialChar(String password){
        return SPECIALCHARREGEX.matcher(password).find();
    }

    public static Boolean isValidEmail(String email){
        return EMAILREGEX.matcher(email).find();
    }
}
