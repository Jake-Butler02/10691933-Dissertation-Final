package com.jake.dissertation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    private PlacesAPI placesAPI;
    private static final String API_KEY = "AIzaSyBB5DXbNPUO3Inr9FQ8W2K7lqvq-o2Mlm4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        String apiKey = API_KEY;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //checks for location permission and adds location layer to map
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (googleMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Location permission denied. Some features may not be available.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //creates the googlemap object and info window and searches for nearby pubs
        this.googleMap = googleMap;

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                TextView title = v.findViewById(R.id.title);
                TextView snippet = v.findViewById(R.id.snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());

                return v;
            }
        });

        //*Section this off when submitting. This section sets the location to the plymouth university for testing
        double latitude = 50.3762;
        double longitude = -4.1426;
        LatLng plymouthUniLocation = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(plymouthUniLocation, 15));

        placesAPI = new PlacesAPI(googleMap);

        placesAPI.searchNearbyPubs(latitude, longitude, 5000, API_KEY);
         //*/

    /* This gets rid of this section for presentation and testing using android studio


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Gets the users latitude and longitude of their current location
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                LatLng currentLocation = new LatLng(latitude, longitude);
                                // Moves the camera to the user's current location
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));

                                placesAPI = new PlacesAPI(googleMap);
                                placesAPI.searchNearbyPubs(latitude, longitude, 5000, API_KEY);
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
     /*/
    }
}