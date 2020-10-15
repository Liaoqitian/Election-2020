package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class DetailedView extends AppCompatActivity {

    ImageView bigProfileIv;
    TextView nameTv, partyTv, websiteTv, phoneTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        String photoUrl = getIntent().getExtras().getString("photoUrl");
        String name = getIntent().getExtras().getString("name");
        String party = getIntent().getExtras().getString("party");
        String phone = getIntent().getExtras().getString("phone");
        String website = getIntent().getExtras().getString("website");

        bigProfileIv = findViewById(R.id.bigProfile);
        nameTv = findViewById(R.id.name);
        partyTv = findViewById(R.id.party);
        websiteTv = findViewById(R.id.website);
        phoneTv = findViewById(R.id.phone);

        nameTv.setText(name);
        partyTv.setText(party + " Party");
        phoneTv.setText(phone);
        websiteTv.setText(website);
        Picasso.get().load(photoUrl).into(bigProfileIv);
    }
}