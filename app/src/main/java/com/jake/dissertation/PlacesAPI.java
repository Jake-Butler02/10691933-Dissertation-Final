package com.jake.dissertation;

import android.location.Location;
import android.util.Log;
import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlacesAPI {

    private static final String TAG = "PlacesAPI";
    private GoogleMap mMap;
    private List<Marker> pubMarkers;

    public PlacesAPI(GoogleMap map) {
        mMap = map;
        pubMarkers = new ArrayList<>();
    }

    public void searchNearbyPubs(double latitude, double longitude, int radius, String apiKey) {
        //this section gets the nearby pubs distance details
        new NearbyPubsTask().execute(latitude, longitude, radius, apiKey);
    }

    private class NearbyPubsTask extends AsyncTask<Object, Void, String> {
        //this section gets the pubs data and connects to the api

        @Override
        protected String doInBackground(Object... params) {
            double latitude = (double) params[0];
            double longitude = (double) params[1];
            int radius = (int) params[2];
            String apiKey = (String) params[3];

            try {
                String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=" + latitude + "," + longitude +
                        "&radius=" + radius +
                        "&type=bar" +
                        "&key=" + apiKey;

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return readStream(in);
                } finally {
                    urlConnection.disconnect();
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Error fetching nearby pubs data: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                LatLng userLocation = mMap.getCameraPosition().target;
                parseNearbySearchResponse(response, userLocation);
            } else {
                //logs for testing
                Log.e(TAG, "Failed to fetch nearby pubs data");
            }
        }

        private String readStream(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        }
    }

    private void parseNearbySearchResponse(String response, LatLng userLocation) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject place = results.getJSONObject(i);
                String name = place.getString("name");
                double latitude = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double longitude = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                // Calculates the distance between the user's location and the pubs
                Location pubLocation = new Location("pub");
                pubLocation.setLatitude(latitude);
                pubLocation.setLongitude(longitude);

                // Converts LatLng to Location for the user's location
                Location userLoc = new Location("user");
                userLoc.setLatitude(userLocation.latitude);
                userLoc.setLongitude(userLocation.longitude);

                // Calculate distance between user's location and pub's location
                float distance = userLoc.distanceTo(pubLocation);
                // Gets the pubs rating if available
                double rating = place.optDouble("rating", -1.0);

                // Creates the snippet to show details of the pub
                String snippet;
                if (rating != -1.0) {
                    snippet = "Distance From You: " + distance + " meters\nRating: " + rating;
                } else {
                    snippet = "Distance: " + distance + " meters\nRating not available";
                }

                // Creates a marker for each pub in the radius and adds it to the map
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(name)
                        .snippet(snippet);
                Marker marker = mMap.addMarker(markerOptions);
                pubMarkers.add(marker);
            }
        } catch (JSONException e) {
            //logs for testing
            Log.e(TAG, "Error parsing nearby search response: " + e.getMessage());
        }
    }





}




