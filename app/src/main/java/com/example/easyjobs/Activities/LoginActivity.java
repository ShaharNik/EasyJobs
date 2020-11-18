package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.easyjobs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginB;
    private EditText emailED;
    private EditText pass;
    private Button registerB;
    private ImageView backBL;
    private FirebaseAuth mAuth; // For User Email & Password authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        acvtivateButtonsAndViews();
    }

    public void moveToRegister(){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    public void moveToProfile(){
        Intent i = new Intent(LoginActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    public void updateUI(FirebaseUser user) {
        registerB.setEnabled(false);
        registerB.setVisibility(View.GONE);
        emailED.setEnabled(false);
        pass.setEnabled(false);
        LoginB.setText("Logout");
        LoginActivity.super.onBackPressed(); // get back
        //moveToProfile(); // after user logged in, move him to profile
    }

    private void findViews(){
        registerB = findViewById(R.id.button_login_toregister);
        emailED = findViewById(R.id.emailEditText);
        pass = findViewById(R.id.editTextPassword);
        LoginB = findViewById(R.id.LoginButton);
        backBL = findViewById(R.id.back_login);
    }

    private void acvtivateButtonsAndViews(){
        backBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.super.onBackPressed();
            }
        });

        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegister();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) { // logged in
            registerB.setEnabled(false);
            registerB.setVisibility(View.GONE);
            emailED.setEnabled(false);
            pass.setEnabled(false);
            LoginB.setText("Logout");
        }

        LoginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailED.getText().toString();
                String password = pass.getText().toString();
                if (mAuth.getCurrentUser() == null){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
                            else {
                                //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
                }
                else { // User is Logged In
                    FirebaseAuth.getInstance().signOut();
                    registerB.setEnabled(true);
                    registerB.setVisibility(View.VISIBLE);
                    emailED.setEnabled(true);
                    pass.setEnabled(true);
                    LoginB.setText("Login");
                }
            }
        });
    }
}