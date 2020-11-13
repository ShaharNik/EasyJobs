package com.example.easyjobs.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.easyjobs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TextView welcomeText;
    private Button jobListB;
    private Button proListB;
    private TextView loginT;
    private FirebaseAuth mAuth; // For User Email & Password authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        activateButtonsAndViews();
        logedInModifier();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) // logged in
        {
            String disp_name = user.getDisplayName();
            welcomeText.setText("Hello, " + disp_name + " :)");
            loginT.setText("Profile");
        }
        else
        {
            welcomeText.setText("Hello, Guest :)");
            loginT.setText("Login/Register");
        }
    }

    private void findViews(){
        welcomeText = findViewById(R.id.WelcomeText);
        jobListB = findViewById(R.id.button_mainto_joblist);
        proListB = findViewById(R.id.button_mainto_proflist);
        loginT = findViewById(R.id.button_mainto_login);
    }

    private void activateButtonsAndViews(){
        loginT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) // logged in
                    moveToLoginActivity();
                else
                    moveToProfileActivity();
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
        if (user != null) // user is logged in
        {
            String disp_name = user.getDisplayName();
            welcomeText.setText("Hello, " + disp_name + " :)");
            loginT.setText("Profile");
        }
        else
        {
            welcomeText.setText("Hello, Guest :)");
            loginT.setText("Login/Register");
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

    private void moveToProfileActivity() // **To be continued** //
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
        i.putExtra("user_id",user.getUid());
        startActivity(i);
    }
}