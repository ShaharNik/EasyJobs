package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Date;

public class PostJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backBPJ;

    private Spinner spinnerPJ;
    private int catNum = 0;
    private EditText descED;
    private EditText locED;
    private EditText priceED;

    private Button postJobB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        descED = findViewById(R.id.editDescPJ);
        locED = findViewById(R.id.editLocPJ);
        priceED = findViewById(R.id.editPricePJ);

        backBPJ = findViewById(R.id.back_post_job);
        backBPJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { PostJobActivity.super.onBackPressed(); }
        });

        postJobB = findViewById(R.id.postJobBtn);
        postJobB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postJobToDB();
            }
        });

        spinnerPJ = findViewById(R.id.pickCategoryPostJob);
        String[] items = new String[]{"1", "2", "three"};
        // need to set up categories on db and make the string connecting there.
        String[] items2 = new String[7];

        //create an adapter to describe how the items are displayed.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinnerPJ.setAdapter(adapter);
        spinnerPJ.setOnItemSelectedListener(this);
    }

    public void postJobToDB(){//need to configure userID and Date !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        FirebaseDBJobs db = new FirebaseDBJobs();
        String desc = descED.getText().toString();
        String loc = locED.getText().toString();
        int price = 0;
        try { price = Integer.parseInt(priceED.getText().toString()); }
        catch(Exception e){ price = 0; } //Maybe pop-up window to tell user to insert int?????????????

        db.addNewJob("oooooooooo", desc, price, loc, new Date(1888880000), catNum);
        PostJobActivity.super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        catNum = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        catNum = 0;// Default Category??????????????????????????????????/
    }
}