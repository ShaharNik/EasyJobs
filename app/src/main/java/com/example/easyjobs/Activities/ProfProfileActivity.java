package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_profile);

        findViews();
        activateBackButton();
        setDataFromDB();
        setRatingBarListener();
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
    }
    private void setRatingBarListener()
    {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(ProfProfile_UserID!=null) {
                    FirebaseDBUsers db = new FirebaseDBUsers();
                  if(FirebaseAuth.getInstance().getCurrentUser() != null)
                    {
                        db.setRating(FirebaseAuth.getInstance().getCurrentUser().getUid(),ProfProfile_UserID,rating,ProfProfileActivity.this,ratingBar);
                        ratingBar.setEnabled(false);
                    }
                }
            }
        });
    }

    private void activateBackButton(){
        backBPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfProfileActivity.super.onBackPressed();
            }
        });
    }

    private void setDataFromDB(){// Gotta make the numbers of the categories to the name of them.
        FirebaseDBProfs dbProf = new FirebaseDBProfs();
        DatabaseReference drProf = dbProf.getProfByID(getIntent().getStringExtra("prof_id"));
        drProf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Prof profile = snapshot.getValue(Prof.class);
                descPPTV.setText("תיאור: " + profile.getDesc());
                //Add categories
                ArrayList<Integer> cats = (ArrayList<Integer>) profile.getCategory();
                FirebaseDBCategories catDb = new FirebaseDBCategories();
                DatabaseReference catDR;
                for (int i=0; i<cats.size(); i++){
                    final int x = i;
                    catDR = catDb.getCatByID("\"" + cats.get(i) + "\"");
                    catDR.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Category c = snapshot.getValue(Category.class);
                            catPPTV.setText(catPPTV.getText().toString() + c.getCat_name());
                            if (x<cats.size() - 1){
                                catPPTV.setText(catPPTV.getText().toString() + ", ");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
                //End adding
                locationPPTV.setText("איזור עבודה: " + profile.getLocation());

                FirebaseDBUsers dbUser = new FirebaseDBUsers();
                DatabaseReference drUser = dbUser.getUserByID(profile.getUser_ID());
                drUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        ProfProfile_UserID = user.getUser_ID();
                        namesPPTV.setText("שם: " + user.getFirstName() + " " + user.getLastName());
                        ratingPPTV.setText("דירוג: " + user.getRating()+ " ("+ user.getRatingsAmount()+")");
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