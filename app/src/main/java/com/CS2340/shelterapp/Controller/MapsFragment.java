package com.CS2340.shelterapp.Controller;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Map screen which will display all the closest shelters and their information.
 *
 * @author Farzam
 * @version 1.0
 */
public class MapsFragment extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{

    //TODO: Farzam: Link this page to the MapsMasterActivity main UI
    private GoogleMap mMap;

    private Shelters model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        model = Shelters.INSTANCE;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near GT, Atlanta, Georgia. (CS 2340 - M4 - TEAM 29)
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Atlanta (GT Coordinates) and move the camera - Added by Farzam
        LatLng GT = new LatLng(33.7756, -84.3963);
//        mMap.addMarker(new MarkerOptions().position(GT).title("Georgia Institute of Technology"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GT, 10));

        for (ShelterData s : model.getItems()) {
            String shelterName = s.getName();
            double shelterLatitude = s.getLatitude();
            double shelterLongitude = s.getLongitude();
            String shelterAddress = s.getAddress();
            String phoneNumber = s.getPhoneNumber();

            LatLng shelterLocation = new LatLng(shelterLatitude, shelterLongitude);

            Marker shelter = mMap.addMarker(new MarkerOptions().position(shelterLocation).title(shelterName));
//            mMap.setOnInfoWindowClickListener(this);
//            shelter.showInfoWindow();
        }

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                // Getting view from the layout file info_window_layout
//                View v = getLayoutInflater().inflate(R.layout.mapmarker_detail, null);
//
//                // Getting the position from the marker
//                LatLng latLng = marker.getPosition();
//
//                TextView name = (TextView) v.findViewById(R.id.shelterName);
//                TextView capacity = (TextView) v.findViewById(R.id.shelterCapacity);
//                TextView lat = (TextView) v.findViewById(R.id.shelterLatitude);
//                TextView lng = (TextView) v.findViewById(R.id.shelterLongitude);
//                TextView address = (TextView) v.findViewById(R.id.shelterAddress);
//
//                name.setText(String.format("Name: %s", ));
//                lat.setText(String.format("Latitude: %s", latLng.latitude));
//                lng.setText(String.format("Longitude: %s", latLng.longitude));
//
//                // Returning the view containing InfoWindow contents
//                return v;
//            }
//        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        navigateUpTo(new Intent(this, ShelterItemDetailFragment.class));
    }
}
