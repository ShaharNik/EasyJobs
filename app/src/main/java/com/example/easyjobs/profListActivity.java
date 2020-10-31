package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class profListActivity extends AppCompatActivity {

    private Button profProfile;
    private Button postProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_list);

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
        Intent i = new Intent(profListActivity.this, ProfProfileActivity.class);
        startActivity(i);
    }

    public void moveToPostProf(){
        Intent i = new Intent(profListActivity.this, PostProfActivity.class);
        startActivity(i);
    }

    public void moveToMain(View v){
        Intent i = new Intent(profListActivity.this, MainActivity.class);
        startActivity(i);
    }
}