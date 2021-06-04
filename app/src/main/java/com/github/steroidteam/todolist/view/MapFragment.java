package com.github.steroidteam.todolist.view;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.github.steroidteam.todolist.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    public static final String LOCATION_REQ = MapFragment.class.toString() + "/location";
    public static final String LOCATION_KEY = "location";
    public static final String LOCATION_NAME_KEY = "location-name";

    private static final String TAG = MapFragment.class.getSimpleName();

    private GoogleMap map;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int ZOOM_SEARCH = 10;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    private SearchView searchView;
    private Marker marker;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        root.findViewById(R.id.map_save_location).setOnClickListener(this::onSavePressed);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        // Retrieve the content view that renders the map.
        // setContentView(R.layout.fragment_map);
        searchView = root.findViewById(R.id.sv_location);

        searchView.setOnQueryTextListener(createOnQueryTextListener());
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return root;
    }

    private SearchView.OnQueryTextListener createOnQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    markerSearchLocation(location, addressList);
                    searchView.setQuery("", false);
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
        Geocoder geocoder = new Geocoder(getContext());
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!checkLocationHasBeenFound(addressList)) {
            Toast.makeText(getContext(), getString(R.string.location_not_found), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Address address = addressList.get(0);
        locationHasBeenFound(address);
    }

    private boolean checkLocationHasBeenFound(List<Address> addressList) {
        return addressList.size() > 0;
    }

    private void locationHasBeenFound(Address address) {
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        String locationName = address.getLocality();
        if (marker != null) {
            marker.setPosition(latLng);
            marker.setTitle(locationName);
        } else {
            marker = map.addMarker(new MarkerOptions().position(latLng).title(locationName));
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_SEARCH));
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

    public void onSavePressed(View view) {
        if (marker != null) {
            LatLng position = marker.getPosition();

            Geocoder geocoder = new Geocoder(getContext());
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(position.latitude, position.longitude, 2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putParcelable(LOCATION_KEY, position);
            bundle.putString(LOCATION_NAME_KEY, addressList.get(0).getLocality());

            getParentFragmentManager().setFragmentResult(LOCATION_REQ, bundle);
        }
        // Go back to the original fragment.
        getParentFragmentManager().popBackStack();
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
                locationResult.addOnCompleteListener(getActivity(), this::onCompleteDeviceLocation);
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

    public void deviceLocationMarker(Location location) {
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
            marker.setTitle(getString(R.string.current_location_message));
        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            marker =
                    map.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(getString(R.string.current_location_message)));
        }
    }

    private void deviceLocationMarkerLocIsNull() {
        map.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
        if (marker != null) {
            marker.setPosition(defaultLocation);
            marker.setTitle(getString(R.string.default_location));
        } else {
            marker =
                    map.addMarker(
                            new MarkerOptions()
                                    .position(defaultLocation)
                                    .title(getString(R.string.default_location)));
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
                        getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(),
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
