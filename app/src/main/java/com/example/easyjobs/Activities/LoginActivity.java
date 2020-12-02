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
import com.example.easyjobs.utils.Validator;
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
    private Button resetButton;
    private ImageView backBL;
    private FirebaseAuth mAuth; // For User Email & Password authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        acvtivateButtonsAndViews();
    }

    private void findViews(){
        registerB = findViewById(R.id.button_login_toregister);
        emailED = findViewById(R.id.emailEditText);
        pass = findViewById(R.id.editTextPassword);
        LoginB = findViewById(R.id.LoginButton);
        backBL = findViewById(R.id.back_login);
        resetButton = findViewById(R.id.resetButton);
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

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validator.ValidateUserEmail(emailED.getText().toString())){
                    mAuth.sendPasswordResetEmail(emailED.getText().toString());
                }
               else{
                   emailED.setError("המייל שהזנת אינו תקין");
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) { // logged in
            registerB.setEnabled(false);
            registerB.setVisibility(View.GONE);
            emailED.setEnabled(false);
            pass.setEnabled(false);
            LoginB.setText("התנתק");
            resetButton.setVisibility(View.GONE);
            resetButton.setEnabled(false);
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
                                Toast.makeText(LoginActivity.this, "האימות נכשל.", Toast.LENGTH_SHORT).show();
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
                    LoginB.setText("התחבר");
                    resetButton.setVisibility(View.VISIBLE);
                    resetButton.setEnabled(true);
                }
            }
        });
    }

    public void updateUI(FirebaseUser user) {
        if(user != null) {
            registerB.setEnabled(false);
            registerB.setVisibility(View.GONE);
            emailED.setEnabled(false);
            pass.setEnabled(false);
            resetButton.setVisibility(View.GONE);
            resetButton.setEnabled(false);
            LoginB.setText("התנתק");
            LoginActivity.super.onBackPressed(); // get back
        }
        else
        {
            emailED.setError("שם משתמש או סיסמא אינם נכונים");
            pass.setError("שם משתמש או סיסמא אינם נכונים");
            pass.setText("");
            emailED.setText("");
        }
    }

    public void moveToRegister(){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
}