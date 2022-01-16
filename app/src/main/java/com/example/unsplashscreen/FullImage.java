package com.example.unsplashscreen;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FullImage extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);


        ImageView imageView = findViewById(R.id.myZoomageView);

        Glide.with(this).load(getIntent().getStringExtra("image")).into(imageView);

    }}

