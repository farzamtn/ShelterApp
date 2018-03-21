package com.CS2340.shelterapp.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Shelter Model for listing all the shelters in the DB.
 *
 * @author Farzam
 * @version 1.4
 */

public class Shelters {
    public static final Shelters INSTANCE = new Shelters();

    private List<ShelterData> items;

    private Shelters() {
        items = new ArrayList<>();
    }

    public void addItem(ShelterData item) {
        items.add(item);
    }

    public List<ShelterData> getItems() {
        return items;
    }

    public ShelterData findItemByName(String name) {
        for (ShelterData d: items) {
            if (d.getName().equals(name)) return d;
        }

        Log.d("Can't Find Shelter", "Warning - Failed to find name: " + name);
        return null;
    }

    public ShelterData findItemById(int id) {
        for (ShelterData d : items) {
            if (d.getKey() == id) return d;
        }

        Log.d("Can't Find Shelter", "Warning - Failed to find id: " + id);
        return null;
    }

    public List<ShelterData> findItemsByQuery(CharSequence charQuery) {
        List<ShelterData> foundShelters = new ArrayList<>();
        String query = charQuery.toString().toLowerCase();
        String searchType ="";
        String keyword = "";
        String genderExclude = "";

        // Decides what kind of search user is trying to perform
        if (query.contains("men") || query.contains("male")) {
            searchType = "gender";
            genderExclude = "women";
            if (query.contains("women") || query.contains("female")) {
                genderExclude = "men";
            }
        } else if (query.contains("families") || query.contains("newborn")
                || query.contains("family")) {
            searchType = "age";
            keyword = "families";
        } else if (query.contains("child")) {
            searchType = "age";
            keyword = "child";
        } else if (query.contains("young adult")) {
            searchType = "age";
            keyword = "young adult";
        } else if (query.contains("anyone")) {
            searchType = "age";
            keyword = "anyone";
        }


        switch(searchType) {
            case "gender":
                for (ShelterData d : items) {
                    String restrictions = d.getRestrictions().toLowerCase();
                    //adds shelters based on non-matches with the genderExclude keyword
                    if (genderExclude.equals("women") && !restrictions.contains(genderExclude)) {
                        foundShelters.add(d);
                    } else if (genderExclude.equals("men") && !restrictions.equals(genderExclude)) {
                        foundShelters.add(d);
                    }
                }
                break;
            case "age":
                for (ShelterData d : items) {
                    String restrictions = d.getRestrictions().toLowerCase();
                    if (restrictions.contains(keyword)) {
                        foundShelters.add(d);
                    }
                }
                break;
            default: //search is assumed to be for name, address, or special notes
                for (ShelterData d : items) {
                    String restrictions = d.getRestrictions().toLowerCase();
                    if (d.getName().toLowerCase().contains(query)
                            || d.getAddress().toLowerCase().contains(query)
                            || d.getSpecialNotes().toLowerCase().contains(query)) {
                        foundShelters.add(d);
                    }
                }
        }

        return foundShelters;
    }
}
