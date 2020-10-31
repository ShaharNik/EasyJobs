package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button jobListB;
    private Button proListB;
    private TextView loginT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void moveToLoginActivity(){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void moveToJobList(){
        Intent i = new Intent(MainActivity.this, JobsListActivity.class);
        startActivity(i);
    }

    public void moveToProfList(){
        Intent i = new Intent(MainActivity.this, profListActivity.class);
        startActivity(i);
    }

}