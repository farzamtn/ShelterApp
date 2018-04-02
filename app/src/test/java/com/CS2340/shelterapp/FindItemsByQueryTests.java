package com.CS2340.shelterapp;

import com.CS2340.shelterapp.Model.ShelterData;
import com.CS2340.shelterapp.Model.Shelters;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Shelters.findItemsByQuery() tests.
 *
 * @author Jiawen Ge
 * @version 1.0
 */
public class FindItemsByQueryTests {
    private static Shelters model;
    private static List<ShelterData> male;
    private static List<ShelterData> female;
    private static List<ShelterData> family;
    private static List<ShelterData> child;
    private static List<ShelterData> youngAdult;
    private static List<ShelterData> anyone;
    private static List<ShelterData> name1;
    private static List<ShelterData> name2;
    private static List<ShelterData> address1;
    private static List<ShelterData> address2;
    private static List<ShelterData> specialNote1;
    private static List<ShelterData> specialNote2;
    private static ShelterData sampleData1;
    private static ShelterData sampleData2;
    private static ShelterData sampleData3;
    private static ShelterData sampleData4;
    private static ShelterData sampleData5;

    @BeforeClass
    public static void setUp() {
        model = Shelters.INSTANCE;
        sampleData1 = new ShelterData(0, "Atlanta House", "150", "Men", -84.410142, 33.780174, "921 Howell Mill Road, Atlanta, Georgia 30318", "General recovery services", "(404) 000-0001");
        sampleData2 = new ShelterData(1, "My House, Your House", "30", "Women/Children", -84.408771, 33.784889, "1300 Joseph E. Boone Blvd NW, Atlanta, GA 30314", "Family services", "(404) 987-4545");
        sampleData3 = new ShelterData(2, "Atlanta Center", "120", "Families w/ Newborns", -84.39265, 33.765162, "156 Mills Street, Atlanta, Georgia 30313", "Trasitional housing", "(404) 404-4404");
        sampleData4 = new ShelterData(3, "Gatech's Inn", "80", "Children/Young Adults", -84.392273, 33.76515, "607 Peachtree Street NE Atlanta, GA 30308", "Shelter and recovery services", "(404) 100-2340");
        sampleData5 = new ShelterData(4, "East Center", "100", "Anyone", -84.384433, 33.770949, "1559 Johnson Road NW, Atlanta, GA 30318", "Emergency Shelter", "(404) 123-4567");
        model.addItem(sampleData1);
        model.addItem(sampleData2);
        model.addItem(sampleData3);
        model.addItem(sampleData4);
        model.addItem(sampleData5);
        male = new ArrayList<>();
        male.add(sampleData1);
        male.add(sampleData3);
        male.add(sampleData4);
        male.add(sampleData5);
        female = new ArrayList<>();
        female.add(sampleData2);
        female.add(sampleData3);
        female.add(sampleData4);
        female.add(sampleData5);
        family = new ArrayList<>();
        family.add(sampleData3);
        child = new ArrayList<>();
        child.add(sampleData2);
        child.add(sampleData4);
        youngAdult = new ArrayList<>();
        youngAdult.add(sampleData4);
        anyone = new ArrayList<>();
        anyone.add(sampleData5);
        name1 = new ArrayList<>();
        name1.add(sampleData1);
        name1.add(sampleData2);
        name2 = new ArrayList<>();
        name2.add(sampleData3);
        name2.add(sampleData5);
        address1 = new ArrayList<>();
        address1.add(sampleData1);
        address1.add(sampleData5);
        address2 = new ArrayList<>();
        address2.add(sampleData1);
        address2.add(sampleData3);
        specialNote1 = new ArrayList<>();
        specialNote1.add(sampleData5);
        specialNote2 = new ArrayList<>();
        specialNote2.add(sampleData1);
        specialNote2.add(sampleData4);
    }

    @Test
    public void testFindItemsByQueryByGender() {
        assertEquals("Search by gender (male) does not return the correct list.", male, model.findItemsByQuery("men"));
        assertEquals("Search by gender (male) does not return the correct list.", male, model.findItemsByQuery("male only"));
        assertEquals("Search by gender (female) does not return the correct list.", female, model.findItemsByQuery("women"));
        assertEquals("Search by gender (female) does not return the correct list.", female, model.findItemsByQuery("female"));
    }

    @Test
    public void testFindItemsByQueryByAge() {
        assertEquals("Search by age (family) does not return the correct list.", family, model.findItemsByQuery("families"));
        assertEquals("Search by age (newborn) does not return the correct list.", family, model.findItemsByQuery("newborn child"));
        assertEquals("Search by age (family) does not return the correct list.", family, model.findItemsByQuery("whole family"));
        assertNotEquals("Search by age (newborn) does not return the correct list.", child, model.findItemsByQuery("newborn child"));
        assertEquals("Search by age (child) does not return the correct list.", child, model.findItemsByQuery("child"));
        assertEquals("Search by age (child) does not return the correct list.", child, model.findItemsByQuery("children"));
        assertEquals("Search by age (youngAdult) does not return the correct list.", youngAdult, model.findItemsByQuery("young adult"));
        assertEquals("Search by age (youngAdult) does not return the correct list.", youngAdult, model.findItemsByQuery("young adults"));
        assertEquals("Search by age (anyone) does not return the correct list.", anyone, model.findItemsByQuery("anyone"));
    }

    @Test
    public void testFindItemsByQueryByName() {
        assertEquals("Search by name does not return the correct list.", name1, model.findItemsByQuery("House"));
        assertEquals("Search by name does not return the correct list.", name2, model.findItemsByQuery("Center"));
    }

    @Test
    public void testFindItemsByQueryByAddress() {
        assertEquals("Search by address does not return the correct list.", address1, model.findItemsByQuery("Road"));
        assertEquals("Search by address does not return the correct list.", address2, model.findItemsByQuery("Mill"));
    }

    @Test
    public void testFindItemsByQueryBySpecialNote() {
        assertEquals("Search by special note does not return the correct list.", specialNote1, model.findItemsByQuery("Emergency"));
        assertEquals("Search by special note does not return the correct list.", specialNote2, model.findItemsByQuery("Recovery"));
    }
}
