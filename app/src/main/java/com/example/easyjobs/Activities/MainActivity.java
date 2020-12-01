package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private TextView welcomeText;
    private Button jobListB;
    private Button proListB;
    private Button loginT;
    private FirebaseAuth mAuth; // For User Email & Password authentication

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViews();
        setLogoSize();
        activateButtonsAndViews();
        logedInModifier();

    }

    @Override
    protected void onResume() {
        super.onResume();
        logedInModifier();
    }

    private void findViews(){
        logo = findViewById(R.id.imageView);
        welcomeText = findViewById(R.id.WelcomeText);
        jobListB = findViewById(R.id.button_mainto_joblist);
        proListB = findViewById(R.id.button_mainto_proflist);
        loginT = findViewById(R.id.button_mainto_login);
    }

    private void setLogoSize(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        int imgWidth = (int) (screenWidth * 1);
        int imgHeight = (int) (screenHeight * 0.45);

        logo.getLayoutParams().height = imgHeight;
        logo.getLayoutParams().width = imgWidth;
    }

    private void activateButtonsAndViews(){
        loginT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) { // logged in
                    moveToLoginActivity();
                }
                else {
                    moveToProfileActivity();
                }
            }
        });

        jobListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToJobList();
            }
        });

        proListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToProfList();
            }
        });
    }

    private void logedInModifier(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){ // user is logged in
            String disp_name = user.getDisplayName();
            welcomeText.setText("שלום, " + disp_name + " :)");
            loginT.setText("פרופיל");
            DatabaseReference dr = FirebaseDBUsers.CheckAdmin();
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        FirebaseDBUsers.isAdmin=true;
                        System.out.println("ADMIN OKAY");
                    }
                    else
                    {
                        FirebaseDBUsers.isAdmin=false;
                        System.out.println("NOT ADMIN OKAY");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            welcomeText.setText("שלום, אורח :)");
            loginT.setText("התחבר/הירשם");
        }
    }

    private void moveToLoginActivity(){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    private void moveToJobList(){
        Intent i = new Intent(MainActivity.this, JobsListActivity.class);
        startActivity(i);
    }

    private void moveToProfList(){
        Intent i = new Intent(MainActivity.this, ProfListActivity.class);
        startActivity(i);
    }

    private void moveToProfileActivity(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
        i.putExtra("user_id",user.getUid());
        startActivity(i);
    }
}