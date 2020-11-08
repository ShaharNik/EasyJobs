package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Date;

public class PostJobActivity extends AppCompatActivity {

    private ImageView backBPJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        backBPJ = findViewById(R.id.back_post_job);
        backBPJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostJobActivity.super.onBackPressed();
            }
        });

        //FirebaseDBJobs db = new FirebaseDBJobs();
        //db.addNewJob("gggggggggggggkfkgfkfgk", "Painting", 350, "Dar Sambrano's Cave", new Date(1888880000),3);
    }
}