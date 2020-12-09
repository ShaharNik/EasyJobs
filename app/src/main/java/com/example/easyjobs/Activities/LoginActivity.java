package com.example.easyjobs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.R;
import com.example.easyjobs.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button LoginB;
    private EditText emailED;
    private EditText pass;
    private Button registerB;
    private Button resetButton;
    private ImageView backBL;
    private FirebaseAuth mAuth; // For User Email & Password authentication

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        activateButtonsAndViews();
    }

    private void findViews()
    {
        registerB = findViewById(R.id.button_login_toregister);
        emailED = findViewById(R.id.emailEditText);
        pass = findViewById(R.id.editTextPassword);
        LoginB = findViewById(R.id.LoginButton);
        backBL = findViewById(R.id.back_login);
        resetButton = findViewById(R.id.resetButton);
    }
    @Override
    public void onClick(View ClickedButton) {
        if (backBL.equals(ClickedButton))
        {
            LoginActivity.super.onBackPressed();
        }
        if (registerB.equals(ClickedButton))
        {
            moveToRegister();
        }
        if (resetButton.equals(ClickedButton))
        {
            if (Validator.ValidateUserEmail(emailED.getText().toString()) && !emailED.getText().toString().isEmpty())
            {
                mAuth.sendPasswordResetEmail(emailED.getText().toString());
            }
            else
            {
                emailED.setError("המייל שהזנת אינו תקין");
            }
        }
        if (LoginB.equals(ClickedButton))
        {
            String email = emailED.getText().toString();
            String password = pass.getText().toString();
            if (!validation(email,password))
            { return; }
            if (mAuth.getCurrentUser() == null)
            {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "האימות נכשל.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
            }
            else
            {
                // User is Logged In
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
    }

    private void activateButtonsAndViews()
    {
        backBL.setOnClickListener(this);
        registerB.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
        {
            // logged in
            registerB.setEnabled(false);
            registerB.setVisibility(View.GONE);
            emailED.setEnabled(false);
            pass.setEnabled(false);
            LoginB.setText("התנתק");
            resetButton.setVisibility(View.GONE);
            resetButton.setEnabled(false);
        }

        LoginB.setOnClickListener(this);
    }

    private boolean validation(String email, String password) {
        if (email.isEmpty()) {
            emailED.setError("עליך להזין אימייל");
            return false;
        }
        if (password.isEmpty())
        {
            pass.setError("עליך להזין סיסמא");
            return false;
        }
        if (!Validator.ValidateUserEmail(email))
        {
            emailED.setError("אימייל אינו תקין");
            return false;
        }
        if (!Validator.ValidateUserPassword(password))
        {
            pass.setError("סיסמא אינה תקינה");
            return false;
        }
        return true;
    }
    private void updateUI(FirebaseUser user)
    {
        if (user != null)
        {

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

    private void moveToRegister()
    {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
}