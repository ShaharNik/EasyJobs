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
    private Button camOpener; // Open Camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        // Open Camera
        camOpener.setOnClickListener(new View.OnClickListener() { // Open Camera
            @Override
            public void onClick(View v) {
                openCameraButton();
            }
        });// Open Camera


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

    void openCameraButton(){ // Open Camera
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);
    }

    void moveToLoginActivity(){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    void moveToJobList(){
        Intent i = new Intent(MainActivity.this, JobsListActivity.class);
        startActivity(i);
    }

    void moveToProfList(){
        Intent i = new Intent(MainActivity.this, ProfListActivity.class);
        startActivity(i);
    }
    void moveToProfileActivity() // **To be continued** //
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
        i.putExtra("user_id",user.getUid());
        startActivity(i);
    }
   void findViews()
   {
       welcomeText = findViewById(R.id.WelcomeText);
       camOpener = findViewById(R.id.openCam);
       jobListB = findViewById(R.id.button_mainto_joblist);
       proListB = findViewById(R.id.button_mainto_proflist);
       loginT = findViewById(R.id.button_mainto_login);

   }

}