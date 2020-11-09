package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TextView welcomeText;
    private Button jobListB;
    private Button proListB;
    private TextView loginT;
    private FirebaseAuth mAuth; // For User Email & Password authentication

    private Button camOpener; // Open Camera
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
            loginT.setText("SignOut");
        }
        else
        {
            welcomeText.setText("Hello, Guest :)");
            loginT.setText("Login/Register");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.WelcomeText);
        // Open Camera
        camOpener = findViewById(R.id.openCam);
        camOpener.setOnClickListener(new View.OnClickListener() { // Open Camera
            @Override
            public void onClick(View v) {
                openCameraButton();
            }
        });// Open Camera

        loginT = findViewById(R.id.button_mainto_login);
        loginT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLoginActivity();
            }
        });

        jobListB = findViewById(R.id.button_mainto_joblist);
        jobListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToJobList();
            }
        });

        proListB = findViewById(R.id.button_mainto_proflist);
        proListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToProfList();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) // logged in
        {
            String disp_name = user.getDisplayName();
            welcomeText.setText("Hello, " + disp_name + " :)");
            loginT.setText("SignOut");
        }
        else
        {
            welcomeText.setText("Hello, Guest :)");
            loginT.setText("Login/Register");
        }
    }

    public void openCameraButton(){ // Open Camera
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);
    }

    public void moveToLoginActivity(){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void moveToJobList(){
        Intent i = new Intent(MainActivity.this, JobsListActivity.class);
        startActivity(i);
    }

    public void moveToProfList(){
        Intent i = new Intent(MainActivity.this, ProfListActivity.class);
        startActivity(i);
    }

}