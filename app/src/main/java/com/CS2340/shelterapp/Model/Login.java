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
        boolean length = pass.length() >= 8;
        boolean upper = false;
        boolean lower = false;
        boolean number = false;
        boolean symbol = false;
        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            if (c >= 65 && c <= 90) {
                upper = true;
            } else if (c >= 97 && c <= 122) {
                lower = true;
            } else if (c >= 48 && c <= 57) {
                number = true;
            } else if (c >= 33 && c <= 126) {
                symbol = true;
            }
        }
        return length && upper && lower && number && symbol;
    }

}
