package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ProfProfileActivity extends AppCompatActivity {

    private ImageView backBPP;
    private TextView namesPPTV;
    private TextView ratingPPTV;
    private TextView descPPTV;
    private TextView catPPTV;
    private TextView locationPPTV;
    private TextView phonePPTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_profile);

        findViews();
        activateBackButton();
        setDataFromDB();
    }

    private void findViews(){
        backBPP = findViewById(R.id.back_prof_profile);
        namesPPTV = findViewById(R.id.namesPP);
        ratingPPTV = findViewById(R.id.ratingPP);
        descPPTV = findViewById(R.id.descriptionPP);
        catPPTV = findViewById(R.id.catPP);
        locationPPTV = findViewById(R.id.locationPP);
        phonePPTV = findViewById(R.id.phonePP);
    }

    private void activateBackButton(){
        backBPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfProfileActivity.super.onBackPressed();
            }
        });
    }

    private void setDataFromDB(){
        FirebaseDBProfs dbProf = new FirebaseDBProfs();
        DatabaseReference drProf = dbProf.getProfByID(getIntent().getStringExtra("prof_id"));
        drProf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Prof profile = snapshot.getValue(Prof.class);
                descPPTV.setText("תיאור: " + profile.getDesc());
                //Add categories
                String categories;
                //End adding
                locationPPTV.setText("איזור עבודה: " + profile.getLocation());

                FirebaseDBUsers dbUser = new FirebaseDBUsers();
                DatabaseReference drUser = dbUser.getUserByID(profile.getUser_ID());
                drUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        namesPPTV.setText("שם: " + user.getFirstName() + " " + user.getLastName());
                        ratingPPTV.setText("דירוג: " + user.getRating()+ " כוכבים");
                        phonePPTV.setText("טלפון: " + user.getPhoneNumber());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}