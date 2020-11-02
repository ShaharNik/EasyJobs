package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button jobListB;
    private Button proListB;
    private TextView loginT;

    private Button camOpener; // Open Camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open Camera
        camOpener = findViewById(R.id.openCam);
        camOpener.setOnClickListener(new View.OnClickListener() { // Open Camera
            @Override
            public void onClick(View v) {
                openCameraButton();
            }
        });// Open Camera

        loginT = findViewById(R.id.button_mainto_login);
        loginT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLoginActivity();
            }
        });

        jobListB = findViewById(R.id.button_mainto_joblist);
        jobListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToJobList();
            }
        });

        proListB = findViewById(R.id.button_mainto_proflist);
        proListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToProfList();
            }
        });
    }

    public void openCameraButton(){ // Open Camera
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);
    }

    public void moveToLoginActivity(){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void moveToJobList(){
        Intent i = new Intent(MainActivity.this, JobsListActivity.class);
        startActivity(i);
    }

    public void moveToProfList(){
        Intent i = new Intent(MainActivity.this, ProfListActivity.class);
        startActivity(i);
    }

}