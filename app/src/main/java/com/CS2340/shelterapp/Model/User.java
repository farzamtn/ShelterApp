package com.CS2340.shelterapp.Model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * User class to update user values in the database
 * Created by chandler on 3/26/18.
 */

public class User {


     //Methods declared static to avoid creating new instances.


    /**
     * Update a given user's disabled attribute in the database
     * @param newDisabled string to be put into database
     * @param user user to be updated with new value
     */
    public static void updateDBUserDisabled(String newDisabled, DatabaseReference user) {
        if ((newDisabled == null) || (user == null)) {
            return;
        }
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.child("Disabled").setValue(newDisabled);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UpdateDisabled: ", databaseError.getMessage());
            }
        });
    }

    /**
     * Update a currentUser's checkedIn attribute in the database
     * @param newCheckedIn number to be put into database
     */
    public static void updateDBUserCheckedIn(int newCheckedIn) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        DatabaseReference userRef = userDB.child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRef.child("Checked In").setValue(newCheckedIn);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UpdateCheckedIn: ", databaseError.getMessage());
            }
        });
    }

    /**
     * Update a currentUser's bed attribute in the database
     * @param newBeds number to be put into database
     */
    public static void updateDBUserBeds(int newBeds) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        DatabaseReference userRef = userDB.child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRef.child("Beds").setValue(newBeds);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("updateDBBeds: ", databaseError.getMessage());
            }
        });
    }
}
