package com.CS2340.shelterapp.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.CS2340.shelterapp.Model.ShelterData;
import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Main activity page with the main GoogleMap
 * and a Navigation Drawer for finding shelters and extra features.
 *
 * @author Farzam
 * @version 3.0
 */
public class MapsMasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = MapsMasterActivity.class.getSimpleName();
    private Shelters model;
    private DatabaseReference shelterDB;

    private FirebaseAuth mAuth;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private ArrayList<Marker> markers; //For later filtering of markers

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (GT Atlanta, GA) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(33.7756, -84.3963);
    private static final int DEFAULT_ZOOM = 11;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private PopupWindow popupWindow;
    private CheckBox men_checkbox, women_checkbox, youngAdult_checkbox, children_checkbox, families_checkbox, veterans_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps_master);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        checkIfEmailVerified();

        //GT defaults for the first time
        if (mLastKnownLocation == null) {
            //To avoid NullPointerException
            Location GT_location = new Location("Default GT Location");
            GT_location.setLatitude(33.7756);
            GT_location.setLongitude(-84.3963);
            mLastKnownLocation = GT_location;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        model = Shelters.INSTANCE;

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Must be after shelter model is populated from the DB
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void checkIfEmailVerified() {
        //Letting the user know they need to verify they email if they haven't already done so
        if (!mAuth.getCurrentUser().isEmailVerified()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsMasterActivity.this);
            builder.setMessage("For later email/password recovery and changes to your account you need to verify your email. Please" +
                    " check your email and follow the instructions as soon as possible. If you did not get any email, please go to settings and resend the verification email.");

            builder.setNeutralButton("OK", (dialog, which) -> {
                //Do nothing
            });

            builder.show();
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shelters) {
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

            builder.setPositiveButton("YES", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut(); //Ending FireBase session for this user
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            });

            builder.setNegativeButton("NO", (dialog, which) -> {
                //Do nothing
            });
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Move camera to GT (DEFAULT) coordinates - Added by Farzam
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

        markers = new ArrayList<>();

        //Adding marker pins for all shelters in the DB
        for (ShelterData s : model.getItems()) {
            String shelterName = s.getName();
            double shelterLatitude = s.getLatitude();
            double shelterLongitude = s.getLongitude();
            String shelterPhoneNumber = s.getPhoneNumber();

            LatLng shelterLocation = new LatLng(shelterLatitude, shelterLongitude);

            Marker m = mMap.addMarker(new MarkerOptions().position(shelterLocation).title(shelterName).snippet(shelterPhoneNumber));
            markers.add(m); //For later use in filtering markers
        }

        /** Starting the respective Detail page about this shelter using the key - Farzam */
        mMap.setOnInfoWindowClickListener(marker -> {
            Intent intent = new Intent(getBaseContext(), ShelterItemDetailActivity.class);
            intent.putExtra(ShelterItemDetailFragment.ARG_ITEM_ID, model.findItemByName(marker.getTitle()).getKey());
            startActivity(intent);
        });


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
//                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Event handler for FAB which filters the pins displyed on the map. - Farzam
     */
    public void newPopup(View view) {
        //instantiate the maps_master_popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) MapsMasterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.maps_master_popup,null);

        men_checkbox = (CheckBox) customView.findViewById(R.id.men_checkbox);
        women_checkbox = (CheckBox) customView.findViewById(R.id.women_checkbox);
        youngAdult_checkbox = (CheckBox) customView.findViewById(R.id.youngAdult_checkbox);
        children_checkbox = (CheckBox) customView.findViewById(R.id.children_checkbox);
        families_checkbox = (CheckBox) customView.findViewById(R.id.families_checkbox);
        veterans_checkbox = (CheckBox) customView.findViewById(R.id.veterans_checkbox);
        Button closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
        Button resetBtn = (Button) customView.findViewById(R.id.resetBtn);

        //Retrieving saved instances of checkedboxes
        SharedPreferences sp = getSharedPreferences("Men Check Boxes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        men_checkbox.setChecked(sp.getBoolean("men check", false));
        women_checkbox.setChecked(sp.getBoolean("women check", false));
        youngAdult_checkbox.setChecked(sp.getBoolean("young adult check", false));
        children_checkbox.setChecked(sp.getBoolean("children check", false));
        families_checkbox.setChecked(sp.getBoolean("families check", false));
        veterans_checkbox.setChecked(sp.getBoolean("veterans check", false));


        //instantiate popup window
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        CoordinatorLayout c = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //display the popup window
        popupWindow.showAtLocation(c, Gravity.CENTER, 0, 0);

        //set filter and close the popupwindow
        closePopupBtn.setOnClickListener(v -> {
            //Saving checkbox instances
            editor.putBoolean("men check", men_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("women check", women_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("young adult check", youngAdult_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("children check", children_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("families check", families_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("veterans check", veterans_checkbox.isChecked());
            editor.apply();

            //If all filters all selected show everything
            if (!(men_checkbox.isChecked() && women_checkbox.isChecked()
                    && youngAdult_checkbox.isChecked()
                    && children_checkbox.isChecked()
                    && families_checkbox.isChecked() && veterans_checkbox.isChecked())) {
                for (Marker m : markers) {
                    m.setVisible(true);
                }
            }

            //Making sure at leats one filter is selected
            if (men_checkbox.isChecked() || women_checkbox.isChecked() || youngAdult_checkbox.isChecked()
                    || children_checkbox.isChecked()
                    || families_checkbox.isChecked() || veterans_checkbox.isChecked()) {
                for (Marker m : markers) {
                    m.setVisible(false);
                    ShelterData s = model.findItemByName(m.getTitle());

                    //Displaying shelters with no restrictions for all filters
                    if (s.getRestrictions().trim().toLowerCase().equals("anyone")
                            || s.getRestrictions().toLowerCase().equals("no restrictions")) {
                        m.setVisible(true);
                    }

                    if (men_checkbox.isChecked()) {
                        if (s.getRestrictions().trim().toLowerCase().equals("men")) {
                            m.setVisible(true);
                        }
                    }

                    if (women_checkbox.isChecked()) {
                        if (s.getRestrictions().trim().toLowerCase().contains("women")) {
                            m.setVisible(true);
                        }
                    }

                    if (youngAdult_checkbox.isChecked()) {
                        if (s.getRestrictions().toLowerCase().equals("young adults")) {
                            m.setVisible(true);
                        }
                    }

                    if (children_checkbox.isChecked()) {
                        if (s.getRestrictions().trim().toLowerCase().contains("children")) {
                            m.setVisible(true);
                        }
                    }

                    if (families_checkbox.isChecked()) {
                        if (s.getRestrictions().trim().toLowerCase().contains("famil")) {
                            m.setVisible(true);
                        }
                    }

                    if (veterans_checkbox.isChecked()) {
                        if (s.getRestrictions().trim().toLowerCase().equals("veterans")) {
                            m.setVisible(true);
                        }
                    }
                }
            }

            popupWindow.dismiss();
        });

        //show all markers again and close the popupwindow
        resetBtn.setOnClickListener(view1 -> {
            for (Marker m : markers) {
                m.setVisible(true);
            }

            //Move camera to GT (DEFAULT) coordinates - Added by Farzam
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

            //Clearing all check boxes and their saved instances
            men_checkbox.setChecked(false);
            women_checkbox.setChecked(false);
            youngAdult_checkbox.setChecked(false);
            children_checkbox.setChecked(false);
            families_checkbox.setChecked(false);
            veterans_checkbox.setChecked(false);

            editor.putBoolean("men check", men_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("women check", women_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("young adult check", youngAdult_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("children check", children_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("families check", families_checkbox.isChecked());
            editor.apply();

            editor.putBoolean("veterans check", veterans_checkbox.isChecked());
            editor.apply();

            popupWindow.dismiss();
        });
    }
}
