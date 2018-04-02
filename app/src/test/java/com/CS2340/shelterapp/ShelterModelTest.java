package com.CS2340.shelterapp;

import com.CS2340.shelterapp.Model.ShelterData;
import com.CS2340.shelterapp.Model.Shelters;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Local tester for Shelters Model class which tests all the methods - (M10).
 *
 * @author Farzam Tafreshian
 * @version 1.0
 */

public class ShelterModelTest {
    private static Shelters model;
    private static ShelterData sampleShelterData;

    @BeforeClass //To be executed only once - but has to be static (Or we can put addItem in every method)
    public static void setUp() {
        model = Shelters.INSTANCE;
        sampleShelterData = new ShelterData(0, "Test Shelter", "20",
                "Men", 33.7756, -84.3963, "123 Test Dr, Atlanta, GA 30332",
                "NONE", "1231231234");
        model.addItem(sampleShelterData);
    }

    @Test
    public void testAddGetItems() {
        List<ShelterData> items = model.getItems();
        assertEquals("Size check failed", 1, items.size());

        assertEquals("Key not correct", 0, items.get(0).getKey());
        assertEquals("Name not correct", "Test Shelter", items.get(0).getName());
        assertEquals("Capacity not correct", "20", items.get(0).getCapacity());
        assertEquals("Restrictions not correct", "Men", items.get(0).getRestrictions());
        assertEquals("Longitude not correct", 33.7756, items.get(0).getLongitude());
        assertEquals("Latitude not correct", -84.3963, items.get(0).getLatitude());
        assertEquals("Address not correct", "123 Test Dr, Atlanta, GA 30332", items.get(0).getAddress());
        assertEquals("Special Notes not correct", "NONE", items.get(0).getSpecialNotes());
        assertEquals("Phone Number not correct", "1231231234", items.get(0).getPhoneNumber());
    }

    @Test
    public void testFindItemByName() {
        assertEquals("Returned ShelterData by the loop is not the same", sampleShelterData, model.findItemByName("Test Shelter"));
    }

    @Test
    public void testFindItemById() {
        assertEquals("Returned ShelterData by the loop is not the same", sampleShelterData, model.findItemById(0));
    }

    @Test
    public void testFindItemsByQuery() {
        assertEquals("Returned ShelterData by the search function is not the same", sampleShelterData, model.findItemsByQuery("Men").get(0));
    }
}
