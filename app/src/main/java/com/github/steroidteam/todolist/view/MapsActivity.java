package com.github.steroidteam.todolist.view;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.github.steroidteam.todolist.R;
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
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap map;
    private CameraPosition cameraPosition;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private SearchView searchView;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        searchView = findViewById(R.id.sv_location);

        searchView.setOnQueryTextListener(createOnQueryTextListener());
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
    }

    private SearchView.OnQueryTextListener createOnQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    markerSearchLocation(location, addressList);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        };
    }

    private void markerSearchLocation(String location, List<Address> addressList) {
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        String locationWithUppercase =
                location.substring(0, 1).toUpperCase() + location.substring(1);
        if (marker != null) {
            marker.setPosition(latLng);
            marker.setTitle(locationWithUppercase);
        } else {
            marker =
                    map.addMarker(
                            new MarkerOptions().position(latLng).title(locationWithUppercase));
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
    }

    /**
     * Manipulates the map when it's available. This callback is triggered when the map is ready to
     * be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /** Gets the current location of the device, and positions the map's camera. */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> onCompleteDeviceLocation(task));
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void onCompleteDeviceLocation(@NonNull Task<Location> task) {
        if (task.isSuccessful()) {
            // Set the map's camera position to the current location of the device.
            lastKnownLocation = task.getResult();
            deviceLocationMarker(lastKnownLocation);
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            Log.e(TAG, "Exception: %s", task.getException());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void deviceLocationMarker(Location location) {
        if (location != null) {
            deviceLocationMarkerLocNotNull(location);
        } else {
            deviceLocationMarkerLocIsNull();
        }
    }

    private void deviceLocationMarkerLocNotNull(Location location) {
        map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
        if (marker != null) {
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            marker.setTitle("I'm here");
        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            marker = map.addMarker(new MarkerOptions().position(latLng).title("I'm here"));
        }
    }

    private void deviceLocationMarkerLocIsNull() {
        map.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
        if (marker != null) {
            marker.setPosition(defaultLocation);
            marker.setTitle("Sydney :)");
        } else {
            marker =
                    map.addMarker(new MarkerOptions().position(defaultLocation).title("Sydney :)"));
        }
    }

    /** Prompts the user for permission to use the device location. */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                        this.getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /** Handles the result of the request for location permissions. */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        locationPermissionGranted = true;
                    }
                }
        }
        updateLocationUI();
    }

    /** Updates the map's UI settings based on whether the user has granted location permission. */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
