package com.CS2340.shelterapp.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Shelter Model for listing all the shelters in the DB.
 *
 * @author Farzam
 * @version 1.4
 */

public final class Shelters {

    /**
     * Shelters Instance Initializer
     */
    public static final Shelters INSTANCE = new Shelters();

    private List<ShelterData> items;

    private Shelters() {
        items = new ArrayList<>();
    }

    /**
     * Adds a specific item to the list.
     *
     * @param item the ShelterData to be added to the list
     */
    public void addItem(ShelterData item) {
        items.add(item);
    }

    /**
     * Gets all the items in the list.
     *
     * @return a list of all the items
     */
    public List<ShelterData> getItems() {
        return items;
    }

    /**
     * Finds a shelter with a given name.
     *
     * @param name the name of the shelter to be searched for
     * @return the matching shelter
     */
    public ShelterData findItemByName(String name) {
        for (ShelterData sd: items) {
            if (sd.getName().equals(name)) {
                return sd;
            }
        }

        return null;
    }

    /**
     * Finds a shelter with a given id.
     *
     * @param id the id to be searched for
     * @return the matching case
     */
    public ShelterData findItemById(int id) {
        for (ShelterData sd : items) {
            if (sd.getKey() == id) {
                return sd;
            }
        }

        return null;
    }

    /**
     * Search method for the listview.
     *
     * @param charQuery the given search query
     * @return the mataching ShelterData
     */
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
                    if ("women".equals(genderExclude) && !restrictions.contains(genderExclude)) {
                        foundShelters.add(d);
                    } else if ("men".equals(genderExclude) && !restrictions.equals(genderExclude)) {
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
