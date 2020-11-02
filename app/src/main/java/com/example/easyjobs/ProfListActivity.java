package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ProfListActivity extends AppCompatActivity {

    private Button profProfile;
    private Button postProf;
    private ImageView backBLA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_list);

        backBLA = findViewById(R.id.back_prof_list);
        backBLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfListActivity.super.onBackPressed();
            }
        });

        profProfile = findViewById(R.id.profList_to_profProfile);
        profProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToProfProfile();
            }
        });

        postProf = findViewById(R.id.profList_to_PostProf);
        postProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPostProf();
            }
        });
    }

    public void moveToProfProfile(){
        Intent i = new Intent(ProfListActivity.this, ProfProfileActivity.class);
        startActivity(i);
    }

    public void moveToPostProf(){
        Intent i = new Intent(ProfListActivity.this, PostProfActivity.class);
        startActivity(i);
    }

}