package com.example.easyjobs.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.R;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;

public class PassResetActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageView backButt;
    private EditText enterMail;
    private Button submitMailButt;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);

        findViews();
        mAuth = FirebaseAuth.getInstance();
        activateButtons();
    }

    private void findViews()
    {
        backButt = findViewById(R.id.back_resetPass);
        enterMail = findViewById(R.id.enterMail_PR);
        submitMailButt = findViewById(R.id.submitMail_PR);
    }

    private void activateButtons()
    {
        backButt.setOnClickListener(this);
        submitMailButt.setOnClickListener(this);
    }

    @Override
    public void onClick(View ClickedButton)
    {
        if (backButt.equals(ClickedButton))
        {
            PassResetActivity.super.onBackPressed();
        }
        else if (submitMailButt.equals(ClickedButton))
        {
            if (Validator.ValidateUserEmail(enterMail.getText().toString()))
            {
                mAuth.sendPasswordResetEmail(enterMail.getText().toString());
            }
            else
            {
                enterMail.setError("כתובת המייל שהזנת אינה חוקית");
            }
        }
    }
}