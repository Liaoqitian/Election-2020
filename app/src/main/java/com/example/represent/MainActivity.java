package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Go button
        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText addressInput = (EditText) findViewById(R.id.addressInput);
                String address = addressInput.getText().toString();
                Intent startIntent = new Intent(getApplicationContext(), CongressionalView.class);
                startIntent.putExtra("type", "inputLocation");
                startIntent.putExtra("address", address);
                startActivity(startIntent);
            }
        });

        // Current location button
        Button currentLocationButton = (Button) findViewById(R.id.currentLocation);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), CongressionalView.class);
                startIntent.putExtra("type", "currentLocation");
                startActivity(startIntent);
            }
        });

        // Random location button
        Button randomLocationButton = (Button) findViewById(R.id.randomLocation);
        randomLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), CongressionalView.class);
                startIntent.putExtra("type", "randomLocation");
                startActivity(startIntent);
            }
        });
    }
}