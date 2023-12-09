package com.example.firebase_map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.Arrays;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    FrameLayout map;
    GoogleMap gMap;
    Location currentLocation;
    Marker marker;
    FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;
    SearchView searchView;
    List<LatLng> markerLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Add the coordinates of the locations where you want to place markers
        markerLocations.add(new LatLng(33.996735416282945, -4.991888522417395)); //Fes
        markerLocations.add(new LatLng(32.32700726846075, -9.263692682097439)); //Safi
        markerLocations.add(new LatLng(33.25120572079343, -8.434133053318975)); //El Jadida
        markerLocations.add(new LatLng(30.406085808089117, -9.529786676324157)); //Agadir
        markerLocations.add(new LatLng(35.17290248466381, -3.8620371655010515)); //Al Hoceima
        markerLocations.add(new LatLng(34.248758279308944, -6.583234795648674)); //Kenitra
        markerLocations.add(new LatLng(35.73746880030851, -5.894376337653951)); //Tanger
        markerLocations.add(new LatLng(35.56235119001042, -5.3645789827718735)); //Tetouan
        markerLocations.add(new LatLng(34.650552969074965, -1.8963717807990044)); //Oujda
        markerLocations.add(new LatLng(33.25875137548691, -7.58394934117336)); //Berrechid
        markerLocations.add(new LatLng(31.646932268000786, -8.020401389902176)); //Merrakech
        markerLocations.add(new LatLng(32.37517104843241, -6.316361138062538)); //Beni Mellal
        markerLocations.add(new LatLng(32.8972657316742, -6.913763897923661)); //Khouribgua

        map = findViewById(R.id.map);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String loc = searchView.getQuery().toString();
                if (loc == null) {
                    Toast.makeText(MapsActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(loc, 1);
                        if (addressList.size() > 0) {
                            LatLng latLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            if (gMap != null) {
                                if (marker != null) {
                                    marker.remove();
                                }
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(loc);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
                                gMap.animateCamera(cameraUpdate);
                                marker = gMap.addMarker(markerOptions);
                            } else {
                                // Log a message or show a Toast indicating that the map is not ready
                                Toast.makeText(MapsActivity.this, "Map not ready yet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // Initialize LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if GPS is enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Prompt the user to enable GPS
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            return;  // Exit the method if GPS is not enabled
        }

        // Request last known location
        Task<Location> task = fusedClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    // Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        Log.d("MapsActivity", "onMapReady called");

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions myLocationMarker = new MarkerOptions().position(latLng).title("My Current Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // Adjust the zoom level if needed
        googleMap.addMarker(myLocationMarker);

        // Example list of customized names for each marker
        List<String> markerNames = Arrays.asList("ENSA FES", "ENSA SAFI","ENSA EL JADIDA", "ENSA AGADIR ", "ENSA AL HOCEIMA","ENSA KENITRA","ENSA TANGER","ENSA TETOUAN","ENSA OUJDA","ENSA BERRECHID","ENSA MARRAKECH","ENSA BENI MELLAL","ENSA KHOURIBGUA");

        // Add markers for each location in the markerLocations list
        for (int i = 0; i < markerLocations.size(); i++) {
            LatLng location = markerLocations.get(i);
            String markerTitle = markerNames.get(i); // Get the corresponding name

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title(markerTitle)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }
}