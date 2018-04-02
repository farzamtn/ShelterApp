package com.CS2340.shelterapp.Controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.CS2340.shelterapp.Model.ShelterData;
import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.Model.User;
import com.CS2340.shelterapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * An activity representing a single ShelterItem detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ShelterItemListActivity}.
 *
 * @author Farzam
 * @version 1.0
 */
public class ShelterItemDetailActivity extends AppCompatActivity {

    private DatabaseReference mShelterDatabase;
    private DatabaseReference conditionRef;
    private DatabaseReference userDBref;
    private DatabaseReference shelterDBref;
    private int shelterId;
    private String capChange;
    private String capacity;
    private String newCap;
    private int maxCap;
    private ShelterData mItem;
    private int checkedIn;
    private int beds;
    private int shelterKey;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelteritem_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        mShelterDatabase = FirebaseDatabase.getInstance().getReference().child("Shelters");
        maxCap = 10; //default maxCap
        capChange = "0"; //change is 0 by default

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String RegisteredUserID = currentUser.getUid();
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        userDBref = userDB.child(RegisteredUserID);

        Intent intent = getIntent();
        shelterId = intent.getIntExtra(ShelterItemDetailFragment.ARG_ITEM_ID, -1);
        mItem = Shelters.INSTANCE.findItemById(shelterId);
        shelterDBref = mShelterDatabase.child(Integer.toString(shelterId));

        getUserInfo();
        getShelterInfo();

        fab = findViewById(R.id.checkIn);

        fab.setOnClickListener(view -> {

            if (checkedIn == -1) {
                if (capacity.contains("famil") || capacity.contains("apartment")) {
                    groupDisplayInputBox();
                } else {
                    displayInputBox();
                }
            } else if (checkedIn == mItem.getKey()) {
                if (capacity.contains("famil") || capacity.contains("apartment")) {
                    groupDisplayConfirm();
                } else {
                    displayConfirm();
                }
            } else {
                displayCheckInError();
            }
            getUserInfo();
            getShelterInfo();
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(ShelterItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(ShelterItemDetailFragment.ARG_ITEM_ID, 1000));
            ShelterItemDetailFragment fragment = new ShelterItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.shelteritem_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ShelterItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDBCap(String newCapacity) {
        mItem.setCapacity(newCapacity); //For local in order to avoid DB pull down again - Farzam
        conditionRef = mShelterDatabase.child(Integer.toString(shelterId));

        conditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conditionRef.child("Capacity").setValue(newCapacity);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DB error in updateDBCap", databaseError.getMessage());
            }
        });
    }

    private void displayInputBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShelterItemDetailActivity.this);
        builder.setTitle("Number of beds");
        builder.setMessage("Enter the number of beds needed (Max is: " + maxCap + "):");

        // Set up the input
        final EditText input = new EditText(ShelterItemDetailActivity.this);
        // Specify the type of input expected; this, for example, sets the input as a password, and
        // will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            capChange = input.getText().toString();
            if (Integer.parseInt(mItem.getCapacity()) < Integer.parseInt(capChange)) {
                input.setError("Not enough beds");
                displayOverAlert();
                capChange = "0";
            } else if (maxCap < Integer.parseInt(capChange)) {
                displayMaxAlert();
                capChange = "0";
            } else {
                newCap = String.valueOf(Integer.parseInt(capacity) - Integer.parseInt(capChange));

                if (!"0".equals(capChange)) {
                    checkInUser();
                    updateDBCap(newCap);
                    fab.setImageResource(android.R.drawable.checkbox_on_background);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void groupDisplayInputBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ShelterItemDetailActivity.this);
        if (capacity.contains("single")) {
            builder.setTitle("Single or Group");
            builder.setMessage("Choose whether you are a single or a group");
        } else {
            builder.setTitle("Confirm Group Check-in");
            builder.setMessage("Confirm a check-in for one group or family");
        }


        // Set up the buttons
        builder.setPositiveButton("Family or Group", (dialog, which) -> {
            int index;
            if (capacity.contains("apartment")) {
                index = capacity.indexOf('a');
            }
            else {
                index = capacity.indexOf('f');
            }
            int oldCap = Integer.parseInt(capacity.substring(0, index - 1));
            if (oldCap == 0) {
                capChange = "0";
                displayOverAlert();
            } else {
                capChange = "1";
            }
            int newCap = oldCap - 1;
            String updatedCap = "" + newCap + " " + capacity.substring(index);


            if (!"0".equals(capChange)) {
                checkInUser();
                updateDBCap(updatedCap);
                fab.setImageResource(android.R.drawable.checkbox_on_background);
            }

        });

        if (capacity.contains("single")) {
            builder.setNegativeButton("Single", (dialog, which) -> {
                int index;
                int comma = capacity.indexOf(",");
                index = capacity.indexOf("single", comma);

                int oldCap = Integer.parseInt(capacity.substring(comma + 2, index - 1));
                if (oldCap == 0) {
                    capChange = "0";
                    displayOverAlert();
                } else {
                    capChange = "2";
                }
                int newCap = oldCap - 1;
                String updatedCap = capacity.substring(0, comma + 2)
                        + newCap + " " + capacity.substring(index);
                if (!"0".equals(capChange)) {
                    checkInUser();
                    updateDBCap(updatedCap);
                    fab.setImageResource(android.R.drawable.checkbox_on_background);
                }

            });
        }
        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void displayConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ShelterItemDetailActivity.this);
        builder.setTitle("Release Beds");
        builder.setMessage("Confirm releasing " + beds + " bed(s).");

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            capChange = Integer.toString(beds);
            newCap = String.valueOf(Integer.parseInt(capacity) + Integer.parseInt(capChange));
            if (!"0".equals(capChange)) {
                checkOutUser();
                updateDBCap(newCap);
                fab.setImageResource(android.R.drawable.checkbox_off_background);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void groupDisplayConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ShelterItemDetailActivity.this);
        builder.setTitle("Release Beds");

        switch (beds) {
            case 1:
                builder.setMessage("Confirm release of 1 family/group.");
                break;
            case 2:
                builder.setMessage("Confirm release of 1 single.");
                break;
            default:
                builder.setMessage("Confirm release of 1 family/group or 1 single.");
                break;
        }

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (beds == 1) {
                int index;
                if (capacity.contains("apartment")) {
                    index = capacity.indexOf('a');
                }
                else {
                    index = capacity.indexOf('f');
                }
                int oldCap = Integer.parseInt(capacity.substring(0, index - 1));
                int newCap = oldCap + 1;
                capChange = "1";
                String updatedCap = "" + newCap + " " + capacity.substring(index);

                if (!"0".equals(capChange)) {
                    checkOutUser();
                    updateDBCap(updatedCap);
                    fab.setImageResource(android.R.drawable.checkbox_off_background);
                }
            } else {
                int index;
                int comma = capacity.indexOf(",");
                index = capacity.indexOf("single", comma);

                int oldCap = Integer.parseInt(capacity.substring(comma + 2, index - 1));
                int newCap = oldCap + 1;
                String updatedCap = capacity.substring(0, comma + 2)
                        + newCap + " " + capacity.substring(index);
                capChange = "2";
                if (!"0".equals(capChange)) {
                    checkOutUser();
                    updateDBCap(updatedCap);
                    fab.setImageResource(android.R.drawable.checkbox_off_background);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void displayMaxAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ShelterItemDetailActivity.this);
        builder.setTitle("Too many Beds");
        builder.setMessage("Amount of beds is over the max allowed.");

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {

        });

        builder.show();
    }
    private void displayOverAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ShelterItemDetailActivity.this);
        builder.setTitle("Not enough beds");
        builder.setMessage("Shelter does not have enough beds.");

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {

        });

        builder.show();
    }

    private void displayCheckInError() {
        ShelterData mItem = Shelters.INSTANCE.findItemById(checkedIn);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ShelterItemDetailActivity.this);
        builder.setTitle("User Already Checked-In");
        assert mItem != null;
        builder.setMessage("The current user is already checked-in to another Shelter.\n"
                + "User checked in at " + mItem.getName());

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {

        });

        builder.show();
    }

    private void checkInUser() {
        User.updateDBUserBeds(Integer.parseInt(capChange));
        User.updateDBUserCheckedIn(mItem.getKey());
        updateScreen();
    }

    private void checkOutUser() {
        User.updateDBUserBeds(0);
        beds = 0;
        User.updateDBUserCheckedIn(-1);
        checkedIn = -1;
        updateScreen();
    }

    /**
     * creates a new instance of the current intent
     * (not the best way but for updating data dynamically) - Farzam
     */
    private void updateScreen() {
        Intent intent = new Intent(this, ShelterItemDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(ShelterItemDetailFragment.ARG_ITEM_ID, mItem.getKey());
        finish();
        startActivity(intent);
    }

    private void getUserInfo() {
        userDBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkedIn = dataSnapshot.child("Checked In").getValue(Integer.class);
                beds = dataSnapshot.child("Beds").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getShelterInfo() {
        shelterDBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                capacity = dataSnapshot.child("Capacity").getValue(String.class);
                shelterKey = dataSnapshot.child("Unique Key").getValue(Integer.class);
                updateFabImage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateFabImage() {
        if (checkedIn == -1) {
            fab.setImageResource(android.R.drawable.checkbox_off_background);
        } else if (checkedIn == shelterKey){
            fab.setImageResource(android.R.drawable.checkbox_on_background);
        }
    }
}
