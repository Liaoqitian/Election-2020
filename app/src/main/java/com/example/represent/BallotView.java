package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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

    private TextView ballotLocationTv;
    private TextView electionEventTv;
    private TextView pollingLocationTv;
    private TextView dropOffLocationTv;
    private TextView ballotMeasuresTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ballot_view);

        String location = getIntent().getExtras().getString("location");
        String address = getIntent().getExtras().getString("address");
        ballotLocationTv = findViewById(R.id.ballotLocation);
        electionEventTv = findViewById(R.id.electionEvent);
        pollingLocationTv = findViewById(R.id.pollingLocation);
        dropOffLocationTv = findViewById(R.id.dropOffLocation);
        ballotMeasuresTv = findViewById(R.id.ballotMeasures);
        if (address != null) printInformation(address);
        else pollingLocationTv.setText("Invalid Address");

        ballotLocationTv.setText(location);
    }

    private void printInformation(String address) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String place_URL = "?address=" + address;
        String full_URL = VOTE_URL + place_URL + "&key=" + API_KEY;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray pollingLocations, dropOffLocations;
                String electionEvent;
                String pollingLocationName, pollingLine1, pollingLocation;
                String dropOffLocationName, dropOffLine1, dropOffLocation;
                String ballotMeasure;
                try {
                    electionEvent = response.getJSONObject("election").getString("name");
                    electionEventTv.setText(electionEvent);
                } catch (JSONException e) {
                    electionEventTv.setText("N/A");
                    e.printStackTrace();
                }
                try {
                    pollingLocations = response.getJSONArray("pollingLocations");
                    pollingLocationName = pollingLocations.getJSONObject(0).getJSONObject("address").getString("locationName");
                    pollingLine1 = pollingLocations.getJSONObject(0).getJSONObject("address").getString("line1");
                    pollingLocation = pollingLocationName + ", " + pollingLine1;
                    pollingLocationTv.setText(pollingLocation);
                } catch (JSONException e) {
                    pollingLocationTv.setText("N/A");
                    e.printStackTrace();
                }
                try {
                    dropOffLocations = response.getJSONArray("dropOffLocations");
                    dropOffLocationName = dropOffLocations.getJSONObject(0).getJSONObject("address").getString("locationName");
                    dropOffLine1 = dropOffLocations.getJSONObject(0).getJSONObject("address").getString("line1");
                    dropOffLocation = dropOffLocationName + ", " + dropOffLine1;
                    dropOffLocationTv.setText(dropOffLocation);
                } catch (JSONException e) {
                    dropOffLocationTv.setText("N/A");
                    e.printStackTrace();
                }
                try {
                    ballotMeasure = response.getJSONArray("contests").getJSONObject(0).getString("ballotTitle");
                    ballotMeasuresTv.setText(ballotMeasure);
                } catch (JSONException e) {
                    ballotMeasuresTv.setText("N/A");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                electionEventTv.setText("N/A");
                pollingLocationTv.setText("N/A");
                dropOffLocationTv.setText("N/A");
                ballotMeasuresTv.setText("N/A");
            }
        });
        queue.add(stringRequest);
    }
}