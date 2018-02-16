package com.CS2340.shelterapp.Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model class for LoginActivity that handles user/pass validity.
 *
 * @author Farzam
 * @version 1.0
 */
public class Login {

    private String username;
    private String password;

    public Login (String username, String password) {
        this.username = username;
        this.password = password;
    }


    public static boolean isUsernameValid(String user) {
        //TODO: Replace this with your own logic
        return isValidEmail(user);
    }

    /**
     * Check if an email is valid
     */
    public static Boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);

        return matcher.find();
    }

    public static boolean isPasswordValid(String pass) {
        //TODO: Replace this with your own logic
        return pass.length() >= 6;
    }

}
