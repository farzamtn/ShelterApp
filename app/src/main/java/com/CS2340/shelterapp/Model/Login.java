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
    private static Login INSTANCE = new Login();

    private String username;
    private String password;

    /**
     * This is the singleton pattern accessor.  Call this to get the instance of this class
     * @return  the one instance of the facade
     */
    public static Login getInstance() { return INSTANCE; }


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

    /**
     * Check if the password is valid
     */
    public static boolean isPasswordValid(String pass) {
        //TODO: Replace this with your own logic
        return pass.length() >= 6;
    }

}
