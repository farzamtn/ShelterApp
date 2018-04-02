package com.CS2340.shelterapp.Model;

/**
 * Model class for RegistrationActivity that
 * adds the new users to the DB after validation.
 *
 * @author farzam
 * @version 1.0
 */
public class Registration {

    //Methods declared static to avoid creating new instances.


    /**
     * Checks if a given name is valid.
     *
     * @param name the name to be checked
     * @return true if valid
     */
    public static boolean isNameValid(CharSequence name) {
        return name.length() >= 4;
    }

    /**
     * Checks if a given phone# is valid.
     *
     * @param phoneNumber the phone# to be checked
     * @return true if valid
     */
    public static boolean isPhoneNumberValid(CharSequence phoneNumber) {
        return phoneNumber.length() == 10;
    }
}
