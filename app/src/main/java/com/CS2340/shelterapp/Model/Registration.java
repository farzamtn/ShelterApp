package com.CS2340.shelterapp.Model;

/**
 * Model class for RegistrationActivity that
 * adds the new users to the DB after validation.
 *
 * @author farzam
 * @version 1.0
 */
public class Registration {

    /**
     * Check if Name Field is valid
     */
    public static boolean isNameValid(String name) {
        return name.length() >= 4;
    }

    /**
     * Check if PhoneNumber Field is valid
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.length() == 10;
    }
}
