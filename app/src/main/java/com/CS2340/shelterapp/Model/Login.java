package com.CS2340.shelterapp.Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model class for LoginActivity that handles user/pass validity.
 *
 * @author Farzam
 * @version 1.1
 */
public class Login {

    /**  the one and only instance of this class for the singleton pattern */
    public static Login INSTANCE = new Login();

    /**
     * Checks if a username is valid.
     *
     * @param user the String to be checked for rulles
     * @return true if valid
     */
    public static boolean isUsernameValid(String user) {
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

    /**
     * Check if the password is valid
     */
    public static boolean isPasswordValid(String pass) {
        //TODO: Replace this with your own logic
        return pass.length() >= 6;
    }

}
