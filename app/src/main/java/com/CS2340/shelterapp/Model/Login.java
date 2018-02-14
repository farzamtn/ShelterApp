package com.CS2340.shelterapp.Model;

import java.util.HashMap;

/**
 * Model class for LoginActivity that handles user/pass validity.
 *
 * @author Farzam
 * @version 1.0
 */
public class Login {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    public static HashMap<String, String> DUMMY_CREDENTIALS = new HashMap<String, String>(){
        {put("user", "pass");}
    };

    private String username;
    private String password;

    public Login (String username, String password) {
        this.username = username;
        this.password = password;
    }


    public static boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return true;
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }

}
