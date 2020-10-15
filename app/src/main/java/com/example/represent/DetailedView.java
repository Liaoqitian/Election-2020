package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class DetailedView extends AppCompatActivity {

    ImageView bigProfileIv;
    TextView nameTv, partyTv, websiteTv, phoneTv;

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

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

        if (!photoUrl.equals("")) new DownloadImageTask(bigProfileIv).execute(photoUrl);

    }
}