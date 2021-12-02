package com.valorain.playtogether.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.valorain.playtogether.R;

public class FullScreenActivity extends AppCompatActivity {

    private ImageView fullImage,closeImage;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        fullImage = findViewById(R.id.fullscreen_image);
        Intent gelenIntent = getIntent();
        String imgUrl = gelenIntent.getStringExtra("Source");

        System.out.println(imgUrl);


    }
}