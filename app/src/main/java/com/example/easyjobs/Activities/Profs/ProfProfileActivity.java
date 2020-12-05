package com.example.easyjobs.Activities.Profs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProfProfileActivity extends AppCompatActivity {

    private ImageView backBPP;
    private TextView namesPPTV;
    private TextView ratingPPTV;
    private TextView descPPTV;
    private TextView catPPTV;
    private TextView locationPPTV;
    private TextView phonePPTV;
    private RatingBar ratingBar;
    private String ProfProfile_UserID;

    private Button adminEditProf;

    private Prof profile;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_profile);

        findViews();
        activateButtons();
        setRatingBarListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanTexts();
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
        ratingBar = findViewById(R.id.ratingBarProfProfile);

        adminEditProf = findViewById(R.id.admin_edit_prof);
    }
    private void cleanTexts()
    {
        descPPTV.setText("");
        catPPTV.setText("");
        locationPPTV.setText("");
        namesPPTV.setText("");
        phonePPTV.setText("");
    }
    private void setRatingBarListener()
    {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(ProfProfile_UserID!=null) {
                  if(FirebaseAuth.getInstance().getCurrentUser() != null)
                    {
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().compareTo(ProfProfile_UserID) != 0) {
                            FirebaseDBUsers.setRating(FirebaseAuth.getInstance().getCurrentUser().getUid(), ProfProfile_UserID, rating, ProfProfileActivity.this, ratingBar);
                            ratingBar.setEnabled(false);
                        }
                        else
                        {
                            Toast.makeText(ProfProfileActivity.this, "אי אפשר לדרג את עצמך... P:", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
    }

    private void activateButtons() {
        backBPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfProfileActivity.super.onBackPressed();
            }
        });

        if (FirebaseDBUsers.isAdmin) {
            adminEditProf.setVisibility(View.VISIBLE);
            adminEditProf.setEnabled(true);
        }
        else {
            adminEditProf.setVisibility(View.GONE);
            adminEditProf.setEnabled(false);
        }

        adminEditProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfProfileActivity.this, AdminEditPostActivity.class);
                i.putExtra("Prof", profile);
                i.putExtra("User", user);
                startActivity(i);
            }
        });
    }

    private void setDataFromDB(){// Gotta make the numbers of the categories to the name of them.
        DatabaseReference drProf = FirebaseDBProfs.getProfByID(getIntent().getStringExtra("prof_id"));
        drProf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                profile = snapshot.getValue(Prof.class);
                if(profile == null)
                {
                    ProfProfileActivity.this.onBackPressed();
                }
                else {
                    descPPTV.setText("תיאור: " + profile.getDesc());
                    //Add categories
                    List<String> cats = profile.getCategory();
                    DatabaseReference catDR;
                    for (int i = 0; i < cats.size(); i++) {
                        final int x = i;
                        catDR = FirebaseDBCategories.getCatByID(cats.get(i));
                        catDR.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Category c = snapshot.getValue(Category.class);
                                catPPTV.setText(catPPTV.getText().toString() + c.getCat_name());
                                if (x < cats.size() - 1) {
                                    catPPTV.setText(catPPTV.getText().toString() + ", ");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    //End adding
                    locationPPTV.setText("איזור עבודה: " + profile.getLocation());
                    if(profile.getUser_ID().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid())==0)
                    {
                        adminEditProf.setVisibility(View.VISIBLE);
                        adminEditProf.setEnabled(true);
                    }
                    DatabaseReference drUser = FirebaseDBUsers.getUserByID(profile.getUser_ID());
                    drUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user = snapshot.getValue(User.class);
                            ProfProfile_UserID = user.getUser_ID();
                            namesPPTV.setText("שם: " + user.getFirstName() + " " + user.getLastName());
                            ratingPPTV.setText("דירוג: " + user.getRating() + " (" + user.getRatingsAmount() + ")");
                            phonePPTV.setText("טלפון: " + user.getPhoneNumber());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}