package com.CS2340.shelterapp.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Main activity page with a Navigation Drawer for finding shelters and extra features.
 *
 * @author Farzam
 * @version 1.2
 */
public class MapsMasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseUser currentUser;
    private DatabaseReference userDB;
    private DatabaseReference shelterDB;

    private Shelters model;

    private TextView userLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_master);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TODO: Search for shelters", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Getting the Shelters Table from DB and creating a new Model Instance for Shelters
        shelterDB = FirebaseDatabase.getInstance().getReference().child("Shelters");
        model = Shelters.INSTANCE;

        //TODO: Farzam: Check if the number of children in Shelters DB changes and clear model list and add shelters to model again
        //Tried getting ChildrenCount from dataSnapShot and comparing it with getItems().size() but it returns 0 everytime
        if (model.getItems().size() == 0) {
            populateShelterInfo();
        }

        /*
            The following block of code has been commented by Farzam (crashes)
            However, this is how we would read data from our DB to change the userLabel.
        */

//        userLabel = (TextView) findViewById(R.id.userLabel);
//
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        String RegisteredUserID = currentUser.getUid();
//        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
//        DatabaseReference userDBref = userDB.child(RegisteredUserID);

//        userDBref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String name = dataSnapshot.child("Name").getValue(String.class);
//                userLabel.setText(String.format("Hi %s", name));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                userLabel.setText("github.com/farzamtn/ShelterApp");
//                System.out.println("The read failed: " + databaseError.getMessage());
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps_master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_shelters) {
            Intent intent = new Intent(this, ShelterItemListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id ==  R.id.nav_signout) {
            /*  Prompt the user to sign out
                If yes, clear cache and go back to the login screen. - Farzam
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsMasterActivity.this);
            builder.setMessage("Are you sure you want to sign out?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut(); //Ending FireBase session for this user
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing
                }
            });
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method for getting all the shelter info from the FireBase DB and adding it to the local model.
     * Remember both the .csv file (for local bufferReading) and .json file (For FireBase DB) has been
     * added to the res/raw just in case. - Farzam
     */
    private void populateShelterInfo() {
        shelterDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shelters : dataSnapshot.getChildren()) {
                   model.addItem(new ShelterData(shelters.child("Unique Key").getValue(Integer.class),
                           shelters.child("Shelter Name").getValue(String.class),
                           shelters.child("Capacity").getValue(String.class),
                           shelters.child("Restrictions").getValue(String.class),
                           shelters.child("Longitude").getValue(Double.class),
                           shelters.child("Latitude").getValue(Double.class),
                           shelters.child("Address").getValue(String.class),
                           shelters.child("Special Notes").getValue(String.class),
                           shelters.child("Phone Number").getValue(String.class)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Shelter DB Error", databaseError.getMessage());
            }
        });

        /**
         * The following code is for populating shelter information by
         * reading the .csv file instead of getting data for DB (in case DB/App crashes) - Farzam
         * TODO: Farzam: Remove following after demo and validation
         */

//        public static final int KEY_POSITION = 0;
//        public static final int NAME_POSITION = 1;
//        public static final int CAPACITY_POSITION = 2;
//        public static final int RESTRICTION_POSITION = 3;
//        public static final int LONGITUDE_POSITION = 4;
//        public static final int LATITUDE_POSITION = 5;
//        public static final int ADDRESS_POSITION = 6;
//        public static final int NOTES_POSITION = 7;
//        public static final int PHONENUMBER_POSITION = 8;

//        try {
//            //Open a stream on the raw file
//            InputStream is = getResources().openRawResource(R.raw.shelterdatabase);
//            //From here we probably should call a model method and pass the InputStream
//            //Wrap it in a BufferedReader so that we get the readLine() method
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//
//            String line;
//            br.readLine(); //get rid of header line
//            while ((line = br.readLine()) != null) {
//                String[] tokens = line.split(",");
//                int key = Integer.parseInt(tokens[KEY_POSITION]);
//                Double longitude = Double.parseDouble(tokens[LONGITUDE_POSITION]);
//                Double latitude = Double.parseDouble(tokens[LATITUDE_POSITION]);
//                model.addItem(new ShelterData(key, tokens[NAME_POSITION], tokens[CAPACITY_POSITION],
//                        tokens[RESTRICTION_POSITION], longitude, latitude, tokens[ADDRESS_POSITION],
//                        tokens[NOTES_POSITION], tokens[PHONENUMBER_POSITION]));
//            }
//            br.close();
//        } catch (IOException e) {
//            Log.d("Error in file read", e.getMessage());
//        }
    }
}
