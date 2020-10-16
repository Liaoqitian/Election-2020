package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BallotView extends AppCompatActivity {

    static String API_KEY = "AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0";
    static String VOTE_URL = "https://www.googleapis.com/civicinfo/v2/voterinfo";

    private TextView ballotLocationTv, pollingLocationTv, dropOffLocationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ballot_view);

        String location = getIntent().getExtras().getString("location");
        String address = getIntent().getExtras().getString("address");
        ballotLocationTv = findViewById(R.id.ballotLocation);
        pollingLocationTv = findViewById(R.id.pollingLocation);
        dropOffLocationTv = findViewById(R.id.dropOffLocation);

        if (address != null) printInformation(address);


        ballotLocationTv.setText(location);
        pollingLocationTv.setText("hello");
    }

    private void printInformation(String address) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String place_URL = "?address=" + address.replace("\\s+", "");
        String full_URL = VOTE_URL + place_URL + "&key=" + API_KEY;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray pollingLocations;
                String locationName, line1, pollingLocation;
                try {
                    pollingLocations = response.getJSONArray("pollingLocations");
                    locationName = pollingLocations.getJSONObject(0).getJSONObject("address").getString("locationName");
                    line1 = pollingLocations.getJSONObject(0).getJSONObject("address").getString("line1");
                    pollingLocation = locationName + ", " + line1;
                    pollingLocationTv.setText("entered");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pollingLocationTv.setText("There is an error");
            }
        });
    }
}