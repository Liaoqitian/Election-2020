package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class CongressionalView extends AppCompatActivity {

    static String API_KEY = "AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0";
    static String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    static String ADDRESS = "16743 F Rd, Meade, KS 67864, USA";

    /** crude way to set range of lat/lng for inside US – this is very imcomplete and better methods exist*/
    double LAT_MAX = 41.8, LAT_MIN = 33.8, LNG_MAX = -81.5, LNG_MIN = -116.2;
    private TextView locationText;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional_view);

        String type = getIntent().getExtras().getString("type");
        locationText = findViewById(R.id.location);

        switch (type) {
            case "inputLocation":
                address = getIntent().getExtras().getString("address");
                if (address != null) printLocation(address);
                break;
            case "randomLocation":
                Random r = new Random();
                double randomLat = LAT_MIN + (LAT_MAX - LAT_MIN) * r.nextDouble();
                double randomLng = LNG_MIN + (LNG_MAX - LNG_MIN) * r.nextDouble();
                LatLngToAddress(String.valueOf(randomLat), String.valueOf(randomLng));
                break;
            case "currentLocation":

                break;
        }
        // civic website: https://www.googleapis.com/civicinfo/v2/representatives?address=94704&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0
        // geo website: https://maps.googleapis.com/maps/api/geocode/json?latlng=35,-90&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0

//        locationText.setText(address);

    }

    /** Convert LAT/LON to postal address */
    private void LatLngToAddress(String lat, String lng) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String gps_URL = "?latlng=" + lat + "," + lng;
        String full_URL = GEO_URL + gps_URL + "&key=" + API_KEY;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            address = ((JSONArray) response.get("results")).getJSONObject(0).getString("formatted_address");
                            printLocation(address);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String jsonError = new String(error.networkResponse.data);
                address = jsonError;
            }
        });
        queue.add(stringRequest);
    }

    private void printLocation(String address) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String place_URL = "?address=" + address.replace("\\s+", "");
        String full_URL = CIVIC_URL + place_URL + "&key=" + API_KEY;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String cityName = null, stateName = null;
                        try {
                            cityName = ((JSONObject) response.get("normalizedInput")).getString("city");
                            stateName = ((JSONObject) response.get("normalizedInput")).getString("state");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        locationText.setText(cityName + ", " + stateName);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                locationText.setText("Please enter a valid address!");
            }
        });
        queue.add(stringRequest);
    }

    /** Convert address to LAT/LON */
    private static List<String> addressToLatLng(String address) throws JSONException {
        List<String> latlng = new ArrayList();
        String place_URL = "?address=" + address.replace(' ', '+');
        String full_URL = GEO_URL + place_URL + "&key=" + API_KEY;
        JSONObject response = new JSONObject(full_URL);
        String lat = ((JSONArray) response.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
        String lng = ((JSONArray) response.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
        latlng.add(lat);
        latlng.add(lng);
        return latlng;
    }



    /** Lookup Representatives for a given US address */
    private static void representatives(String address) throws JSONException {
        String place_URL = "?address=" + address.replace("\\s+", "");
        String full_URL = CIVIC_URL + place_URL + "&key=" + API_KEY;
        JSONObject response = new JSONObject(full_URL);

        // Print the city and state
        System.out.println(((JSONObject) response.get("normalizedInput")).getJSONObject("city") +
                ", " + ((JSONObject) response.get("normalizedInput")).getJSONObject("state"));
        JSONArray offices = (JSONArray) response.get("offices");
        JSONArray officials = (JSONArray) response.get("officials");
        for (int i = 0; i < offices.length(); i++) {
            if (offices.getJSONObject(i).getString("name").equals("U.S. Senator")) {
                System.out.println("U.S. Senators");
                JSONArray index = offices.getJSONObject(i).getJSONArray("officialIndices");
                for (int j = 0; j < index.length(); j++) {
                    printPerson(officials.getJSONObject((index.getInt(i))));
                }
            } else if (offices.getJSONObject(i).getString("name").equals("U.S. Representative")) {
                System.out.println("U.S. Representative");
                JSONArray index = offices.getJSONObject(i).getJSONArray("officialIndices");
                for (int j = 0; j < index.length(); j++) {
                    printPerson(officials.getJSONObject((index.getInt(i))));
                }
            }
        }
        System.out.println(" ");
    }

    private static String printPerson(JSONObject person) throws JSONException {
        String res = "   " + person.getString("name") + " [" + person.getJSONArray("party").getString(0) + "] "
                + person.getJSONArray("phones").getString(0) + " " + person.getJSONArray("urls").getString(0);
        return res;
    }
}