package com.example.easyjobs.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyjobs.R;

public class AdminEditJobActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView namesText;
    private TextView descText;
    private TextView locText;
    private TextView priceText;
    private TextView dateText;
    private TextView phoneText;
    private Button approveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_job);

        findViews();
        activateButtons();
    }

    private void findViews() {
        backButton = findViewById(R.id.back_admin_editPost);
        namesText = findViewById(R.id.namesEJ);
        descText = findViewById(R.id.descriptionEJ);
        locText = findViewById(R.id.locationEJ);
        priceText = findViewById(R.id.priceEJ);
        dateText = findViewById(R.id.dateEJ);
        phoneText = findViewById(R.id.phoneEJ);
        approveChanges = findViewById(R.id.changeJobDetails);
    }

    private void activateButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEditJobActivity.super.onBackPressed();
            }
        });


    }
}