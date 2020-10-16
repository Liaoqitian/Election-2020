package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class DetailedView extends AppCompatActivity {

    ImageView bigProfileIv;
    TextView nameTv, titleTv, partyTv, websiteTv, phoneTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        String photoUrl = getIntent().getExtras().getString("photoUrl");
        String name = getIntent().getExtras().getString("name");
        String title = getIntent().getExtras().getString("title");
        String party = getIntent().getExtras().getString("party");
        String phone = getIntent().getExtras().getString("phone");
        String website = getIntent().getExtras().getString("website");

        bigProfileIv = findViewById(R.id.bigProfile);
        titleTv = findViewById(R.id.title);
        nameTv = findViewById(R.id.name);
        partyTv = findViewById(R.id.party);
        websiteTv = findViewById(R.id.website);
        phoneTv = findViewById(R.id.phone);

        nameTv.setText(name);
        titleTv.setText(title);
        partyTv.setText(party + " Party");
        if (party.equals("Republican")) partyTv.setTextColor(Color.RED);
        else if (party.equals("Democratic")) partyTv.setTextColor(Color.BLUE);
        phoneTv.setText(phone);
        websiteTv.setText(website);
        if (photoUrl != null) Picasso.get().load(photoUrl).into(bigProfileIv);
    }
}