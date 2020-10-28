package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class profListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_list);
    }

    public void moveToMain(View v){
        Intent i = new Intent(profListActivity.this, MainActivity.class);
        startActivity(i);
    }
}