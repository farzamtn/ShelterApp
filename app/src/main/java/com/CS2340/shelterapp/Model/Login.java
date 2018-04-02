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


     //Methods declared static to avoid creating new instances.


    /**
     * Checks if a username is valid.
     *
     * @param user the String to be checked for rulles
     * @return true if valid
     */
    public static boolean isUsernameValid(CharSequence user) {
        return isValidEmail(user);
    }

    /**
     * Checks if the email is valid.
     *
     * @param email the email to be checked
     * @return true if email is valid
     */
    public static Boolean isValidEmail(CharSequence email) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);

        return matcher.find();
    }

    /**
     * Checks if password is valid.
     *
     * @param pass the password to be checked
     * @return true if password is valid
     */
    public static boolean isPasswordValid(CharSequence pass) {
        //TODO: Replace this with your own logic
        return pass.length() >= 6;
    }

}
