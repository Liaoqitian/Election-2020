package com.example.represent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


// civic website: https://www.googleapis.com/civicinfo/v2/representatives?address=94704&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0
// geo website: https://maps.googleapis.com/maps/api/geocode/json?latlng=35,-90&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0

public class CongressionalView extends AppCompatActivity {

    static String API_KEY = "AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0";
    static String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    static String VOTE_URL = "https://www.googleapis.com/civicinfo/v2/voterinfo";
    static String ADDRESS = "16743 F Rd, Meade, KS 67864, USA";

    /** crude way to set range of lat/lng for inside US – this is very imcomplete and better methods exist*/
    double LAT_MAX = 41.8, LAT_MIN = 33.8, LNG_MAX = -81.5, LNG_MIN = -116.2;
    private TextView partyOneTv, partyTwoTv, partyThreeTv;
    private ImageView profileOneIv, profileTwoIv, profileThreeIv;
    private Button nameOneBtn, nameTwoBtn, nameThreeBtn, locationBtn;
    private String address, nameOne, nameTwo, nameThree, partyOne, partyTwo, partyThree, phoneOne, phoneTwo, phoneThree, websiteOne, websiteTwo, websiteThree,
            photoUrlOne, photoUrlTwo, photoUrlThree, linkOne, linkTwo, linkThree;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional_view);

        String type = getIntent().getExtras().getString("type");
        profileOneIv = findViewById(R.id.profileOne);
        profileTwoIv = findViewById(R.id.profileTwo);
        profileThreeIv = findViewById(R.id.profileThree);
        locationBtn = findViewById(R.id.location);
        nameOneBtn = findViewById(R.id.NameOne);
        partyOneTv = findViewById(R.id.PartyOne);
        nameTwoBtn = findViewById(R.id.NameTwo);
        partyTwoTv = findViewById(R.id.PartyTwo);
        nameThreeBtn = findViewById(R.id.NameThree);
        partyThreeTv = findViewById(R.id.PartyThree);

        switch (type) {
            case "inputLocation":
                address = getIntent().getExtras().getString("address");
                if (address != null) printInformation(address);
                break;
            case "randomLocation":
                Random r = new Random();
                double randomLat = LAT_MIN + (LAT_MAX - LAT_MIN) * r.nextDouble();
                double randomLng = LNG_MIN + (LNG_MAX - LNG_MIN) * r.nextDouble();
                processRandomLocation(String.valueOf(randomLat), String.valueOf(randomLng));
                break;
            case "currentLocation":
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                getLocation();
                break;
        }

        nameOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), DetailedView.class);
                startIntent.putExtra("name", nameOne);
                startIntent.putExtra("party", partyOne);
                startIntent.putExtra("website", websiteOne);
                startIntent.putExtra("phone", phoneOne);
                startIntent.putExtra("photoUrl", photoUrlOne);
                startActivity(startIntent);
            }
        });

        nameTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), DetailedView.class);
                startIntent.putExtra("name", nameTwo);
                startIntent.putExtra("party", partyTwo);
                startIntent.putExtra("website", websiteTwo);
                startIntent.putExtra("phone", phoneTwo);
                startIntent.putExtra("photoUrl", photoUrlTwo);
                startActivity(startIntent);
            }
        });

        nameThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), DetailedView.class);
                startIntent.putExtra("name", nameThree);
                startIntent.putExtra("party", partyThree);
                startIntent.putExtra("website", websiteThree);
                startIntent.putExtra("phone", phoneThree);
                startIntent.putExtra("photoUrl", photoUrlThree);
                startActivity(startIntent);
            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CongressionalView.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(CongressionalView.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String address = addresses.get(0).getAddressLine(0);
                        printInformation(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /** Process a random location by convert LAT/LON to postal address, then printing out relevant information */
    private void processRandomLocation(String lat, String lng) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String gps_URL = "?latlng=" + lat + "," + lng;
        String full_URL = GEO_URL + gps_URL + "&key=" + API_KEY;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            address = ((JSONArray) response.get("results")).getJSONObject(0).getString("formatted_address");
                            printInformation(address);
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

    private void printInformation(String address) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String place_URL = "?address=" + address.replace("\\s+", "");
        String full_URL = CIVIC_URL + place_URL + "&key=" + API_KEY;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String cityName = null, stateName = null;
                        JSONArray offices = null, officials = null;
                        try {
                            cityName = ((JSONObject) response.get("normalizedInput")).getString("city");
                            stateName = ((JSONObject) response.get("normalizedInput")).getString("state");
                            offices = (JSONArray) response.get("offices");
                            officials = (JSONArray) response.get("officials");
                            for (int i = 0; i < offices.length(); i++) {
                                if (offices.getJSONObject(i).getString("name").equals("U.S. Senator")) {
                                    JSONArray index = offices.getJSONObject(i).getJSONArray("officialIndices");
                                    for (int j = 0; j < index.length(); j++) {
                                        final JSONObject person = officials.getJSONObject((index.getInt(j)));
                                        if (j == 0) {
                                            // Set up the name of U.S. Senator One
                                            nameOne = person.getString("name");
                                            SpannableString name = new SpannableString(person.getString("name"));
                                            name.setSpan(new UnderlineSpan(), 0, name.length(), 0);
                                            nameOneBtn.setText(name);

                                            // Set up the party of U.S. Senator One
                                            String party = person.getString("party").split("\\s+")[0];
                                            partyOne = party;
                                            partyOneTv.setText(party);
                                            if (party.equals("Republican")) partyOneTv.setTextColor(Color.RED);
                                            else if (party.equals("Democratic")) partyOneTv.setTextColor(Color.BLUE);

                                            // Set up the photo of U.S. Senator One
                                            if (person.has("photoUrl")) {
                                                photoUrlOne = person.getString("photoUrl");
                                                Picasso.get().load(photoUrlOne).into(profileOneIv);
                                            } else photoUrlOne = null;

                                            // Set up other relevant information of U.S. Senator One
                                            phoneOne = person.getJSONArray("phones").getString(0);
                                            websiteOne = person.getJSONArray("urls").getString(0);
                                        } else if (j == 1) {
                                            // Set up the name of U.S. Senator Two
                                            nameTwo = person.getString("name");
                                            SpannableString name = new SpannableString(person.getString("name"));
                                            name.setSpan(new UnderlineSpan(), 0, name.length(), 0);
                                            nameTwoBtn.setText(name);

                                            // Set up the party of U.S. Senator Two
                                            String party = person.getString("party").split("\\s+")[0];
                                            partyTwo = party;
                                            partyTwoTv.setText(party);
                                            if (party.equals("Republican")) partyTwoTv.setTextColor(Color.RED);
                                            else if (party.equals("Democratic")) partyTwoTv.setTextColor(Color.BLUE);

                                            // Set up the photo of U.S. Senator Two
                                            if (person.has("photoUrl")) {
                                                photoUrlTwo = person.getString("photoUrl");
                                                Picasso.get().load(photoUrlTwo).into(profileTwoIv);
                                            } else photoUrlTwo = null;

                                            // Set up other relevant information of U.S. Senator Two
                                            phoneTwo = person.getJSONArray("phones").getString(0);
                                            websiteTwo = person.getJSONArray("urls").getString(0);
                                        }
                                    }
                                } else if (offices.getJSONObject(i).getString("name").equals("U.S. Representative")) {
                                    JSONArray index = offices.getJSONObject(i).getJSONArray("officialIndices");
                                    JSONObject person = officials.getJSONObject((index.getInt(0)));

                                    // Set up the name of U.S. Representative
                                    nameThree = person.getString("name");
                                    SpannableString name = new SpannableString(person.getString("name"));
                                    name.setSpan(new UnderlineSpan(), 0, name.length(), 0);
                                    nameThreeBtn.setText(name);

                                    // Set up the party of U.S. Representative
                                    String party = person.getString("party").split("\\s+")[0];
                                    partyThree = party;
                                    partyThreeTv.setText(party);
                                    if (party.equals("Republican")) partyThreeTv.setTextColor(Color.RED);
                                    else if (party.equals("Democratic")) partyThreeTv.setTextColor(Color.BLUE);

                                    // Set up the photo of U.S. Representative
                                    if (person.has("photoUrl")) {
                                        photoUrlThree = person.getString("photoUrl");
                                        Picasso.get().load(photoUrlThree).into(profileThreeIv);
                                    } else photoUrlThree = null;

                                    // Set up other relevant information of U.S. Representative
                                    phoneThree = person.getJSONArray("phones").getString(0);
                                    websiteThree = person.getJSONArray("urls").getString(0);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SpannableString locationText = new SpannableString(cityName + ", " + stateName);
                        locationText.setSpan(new UnderlineSpan(), 0, locationText.length(), 0);
                        locationBtn.setText(locationText);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                locationBtn.setText("Invalid Location");
            }
        });
        queue.add(stringRequest);
    }
}