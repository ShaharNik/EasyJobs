package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void moveToLogin(View v){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void moveToJobList(View v){
        Intent i = new Intent(MainActivity.this, JobsListActivity.class);
        startActivity(i);
    }

    public void moveToProfList(View v){
        Intent i = new Intent(MainActivity.this, profListActivity.class);
        startActivity(i);
        // TEST TEST
    }

}