package com.CS2340.shelterapp.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;

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
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private DatabaseReference userDB;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        mShelterDatabase = FirebaseDatabase.getInstance().getReference().child("Shelters");
        maxCap = 10; //default maxCap
        capChange = "0"; //change is 0 by default

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String RegisteredUserID = currentUser.getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        userDBref = userDB.child(RegisteredUserID);

        Intent intent = getIntent();
        shelterId = intent.getIntExtra(ShelterItemDetailFragment.ARG_ITEM_ID, -1);
        mItem = Shelters.INSTANCE.findItemById(shelterId);
        shelterDBref = mShelterDatabase.child(Integer.toString(shelterId));

        getUserInfo();
        getShelterInfo();




        fab = (FloatingActionButton) findViewById(R.id.checkIn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkedIn == -1) {
                    displayInputBox();
                } else if (checkedIn == mItem.getKey()) {
                    displayConfirm();
                } else {
                    displayCheckInError();
                }
                getUserInfo();
                getShelterInfo();
            }
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
        conditionRef = mShelterDatabase.child(Integer.toString(shelterId));

        conditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conditionRef.child("Capacity").setValue(newCapacity);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.print(databaseError.getMessage());
            }
        });
    }

    private void updateDBUserCheckedIn(int newCheckedIn) {
        userRef = userDB.child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRef.child("Checked In").setValue(newCheckedIn);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.print(databaseError.getMessage());
            }
        });
    }

    private void updateDBUserBeds(int newBeds) {
        userRef = userDB.child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRef.child("Beds").setValue("" + newBeds);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.print(databaseError.getMessage());
            }
        });
    }

    private void displayInputBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShelterItemDetailActivity.this);
        builder.setTitle("Number of beds");
        builder.setMessage("Enter the number of beds needed (Max is: " + maxCap + "):");

        // Set up the input
        final EditText input = new EditText(ShelterItemDetailActivity.this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                capChange = input.getText().toString();
                if (Integer.parseInt(mItem.getCapacity()) < Integer.parseInt(capChange)
                        || maxCap < Integer.parseInt(capChange)) {
                    input.setError("Not enough beds");
                    displayOverAlert();
                    capChange = "0";
                } else {
                    newCap = String.valueOf(Integer.parseInt(capacity) - Integer.parseInt(capChange));

                    if (!capChange.equals("0")) {
                        updateScreen();
                        checkInUser();
                        updateDBCap(newCap);
                        fab.setImageResource(android.R.drawable.checkbox_on_background);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void displayConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShelterItemDetailActivity.this);
        builder.setTitle("Release Beds");
        builder.setMessage("Confirm releasing " + beds + " bed(s).");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                capChange = Integer.toString(beds);
                newCap = String.valueOf(Integer.parseInt(capacity) + Integer.parseInt(capChange));
                if (!capChange.equals("0")) {
                    checkOutUser();
                    updateDBCap(newCap);
                    fab.setImageResource(android.R.drawable.checkbox_off_background);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void displayOverAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShelterItemDetailActivity.this);
        builder.setTitle("Too many beds");
        builder.setMessage("Amount of beds is either above the max or above the remaining capacity");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void displayCheckInError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShelterItemDetailActivity.this);
        builder.setTitle("User Already Checked-In");
        builder.setMessage("The current user is already checked-in to another Shelter");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void checkInUser() {
        updateDBUserBeds(Integer.parseInt(capChange));
        updateDBUserCheckedIn(mItem.getKey());
    }

    private void checkOutUser() {
        updateDBUserBeds(0);
        beds = 0;
        updateDBUserCheckedIn(-1);
        checkedIn = -1;
    }

    private void updateScreen() {

    }

    private void getUserInfo() {
        userDBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkedIn = dataSnapshot.child("Checked In").getValue(Integer.class);
                beds = Integer.parseInt(dataSnapshot.child("Beds").getValue(String.class));
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
                capacity = dataSnapshot.child("Capacity").getValue().toString();
                shelterKey = Integer.parseInt(dataSnapshot.child("Unique Key").getValue().toString());
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
