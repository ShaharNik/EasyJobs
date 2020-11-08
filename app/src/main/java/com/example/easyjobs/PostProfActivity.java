package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PostProfActivity extends AppCompatActivity {

    private ImageView backBPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prof);

        backBPP = findViewById(R.id.back_post_prof);
        backBPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostProfActivity.super.onBackPressed();
            }
        });

        /*List<Integer> temp = new ArrayList<Integer>();
        temp.add(5);
        temp.add(3);
        FirebaseDBProfs db = new FirebaseDBProfs();
        db.addNewProf("Gggggfgf", "Fart tester", temp, "Shahar's Dungeon");*/
    }
}