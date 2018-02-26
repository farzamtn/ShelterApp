package com.CS2340.shelterapp.Model;

import android.util.Log;

import com.CS2340.shelterapp.Controller.ShelterData;

import java.util.ArrayList;
import java.util.List;

/**
 * Shelter Model for listing all the shelters in the DB.
 *
 * @author Farzam
 * @version 1.0
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

    public ShelterData findItemById(int id) {
        for (ShelterData d : items) {
            if (d.getKey() == id) return d;
        }

        Log.d("Can't Find Shelter", "Warning - Failed to find id: " + id);
        return null;
    }
}
