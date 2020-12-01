package com.example.easyjobs.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;

public class AdminEditPostActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView namesText;
    private TextView rateText;
    private EditText descText;
    private EditText catText;
    private EditText locText;
    private TextView phoneText;
    private Button approveChanges;

    private User user;
    private Prof prof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_post);

        user = (User) getIntent().getSerializableExtra("User");
        prof = (Prof) getIntent().getSerializableExtra("Prof");

        findViews();
        inputTempData();
        activateButtons();
    }

    private void findViews() {
        backButton = findViewById(R.id.back_admin_editPost);
        namesText = findViewById(R.id.namesEP);
        rateText = findViewById(R.id.ratingEP);
        descText = findViewById(R.id.descriptionEP);
        catText = findViewById(R.id.catEP);
        locText = findViewById(R.id.locationEP);
        phoneText = findViewById(R.id.phoneEP);
        approveChanges = findViewById(R.id.changeProfDetails);
    }

    private void inputTempData() {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        rateText.setText(user.getRating()+ " ("+ user.getRatingsAmount()+")");

        descText.setText(prof.getDesc());
        catText.setText("");
        locText.setText(prof.getLocation());

        phoneText.setText(user.getPhoneNumber());
    }

    private void activateButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEditPostActivity.super.onBackPressed();
            }
        });
    }
}