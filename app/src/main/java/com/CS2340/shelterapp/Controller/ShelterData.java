package com.CS2340.shelterapp.Controller;

/**
 * Data class for ShelterItems which includes all the information for a specific shelter in the DB.
 *
 * @author Farzam
 * @version 1.0
 */

public class ShelterData {
    private int key;
    private String name;
    private String capacity; //Declared as String since some fields have different capacities for different people
    private String restrictions;
    private Double longitude;
    private Double latitude;
    private String address;
    private String specialNotes;
    private String phoneNumber;

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

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
