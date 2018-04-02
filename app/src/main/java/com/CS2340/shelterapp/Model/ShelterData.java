package com.CS2340.shelterapp.Model;

/**
 * Data class for ShelterItems which includes all the information for a specific shelter in the DB.
 *
 * @author Farzam
 * @version 1.0
 */

public class ShelterData {
    private int key;
    private String name;
    private String capacity; //Declared as String since some fields have different capacities
    private String restrictions;
    private Double longitude;
    private Double latitude;
    private String address;
    private String specialNotes;
    private String phoneNumber;

    /**
     * Constructor for ShelterData.
     *
     * @param key Shelter's key
     * @param name Shelter's name
     * @param capacity Shelter's capacity
     * @param restrictions Shelter's restrictions
     * @param longitude Shelter's longitude
     * @param latitude Shelter's latitude
     * @param address Shelter's address
     * @param specialNotes Shelter's special notes
     * @param phoneNumber Shelter's phone number
     */
    public ShelterData(int key, String name, String capacity, String restrictions,
                       Double longitude, Double latitude, String address,
                       String specialNotes, String phoneNumber) {
        this.key = key;
        this.name = name;
        this.capacity = capacity;
        this.restrictions = restrictions;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.specialNotes = specialNotes;
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return this shelter's key
     */
    public int getKey() {
        return key;
    }

    /**
     * @return this shelter's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return this shelter's capacity
     */
    public String getCapacity() {
        return capacity;
    }

    /**
     * Sets this shelter's capacity.
     *
     * @param capacity the new capacity of the shelter
     */
    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    /**
     * @return this shelter's restrictions
     */
    public String getRestrictions() {
        return restrictions;
    }

    /**
     * @return this shelter's longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @return this shelter's latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @return this shelter's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return this shelter's notes
     */
    public String getSpecialNotes() {
        return specialNotes;
    }

    /**
     * @return this shelter's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
