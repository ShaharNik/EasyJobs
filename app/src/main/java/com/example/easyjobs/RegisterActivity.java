package com.example.easyjobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Console;

public class RegisterActivity extends AppCompatActivity {

    private ImageView backBR;
    private Button RegBut;
    EditText user_emailEditText;
    EditText editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        // Get user registration email&password
        user_emailEditText = findViewById(R.id.user_email);
        editTextPassword = findViewById((R.id.editTextPassword));


        backBR = findViewById(R.id.back_register);
        backBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.super.onBackPressed();
            }
        });
        RegBut = findViewById(R.id.RegisterButton);
        RegBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = user_emailEditText.getText().toString();
                String user_password = editTextPassword.getText().toString();
                if (validateEmailAndPassword(user_email,user_password))
                    createAccount(user_email, user_password);
                else
                    Toast.makeText(RegisterActivity.this, "Validation failed.",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }
    void createAccount(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    boolean validateEmailAndPassword(String email, String password)
    {
        return true;
    }
    void updateUI(FirebaseUser user) // we need update something from user information?
    {
        TextView RegisterDummy = findViewById(R.id.RegisterDummy);
        RegisterDummy.setText(user.getUid());
        //user.sendEmailVerification();
        //System.err.println(user.updateProfile(new )
        //System.err.println(user.updatePassword());
        System.err.println(user.getEmail());
        System.err.println(user.getDisplayName());
    }


}