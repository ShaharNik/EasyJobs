package com.example.easyjobs.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.R;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;

public class PassResetActivity extends AppCompatActivity
{
    private ImageView backButt;
    private EditText enterMail;
    private EditText resetCode;
    private EditText enterNewPass;
    private EditText confirmNewPass;
    private Button submitMailButt;
    private Button confirmResetCodeButt;
    private Button changePassButt;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);

        findViews();
        activateButtons();
    }

    private void findViews()
    {
        backButt = findViewById(R.id.back_resetPass);
        enterMail = findViewById(R.id.enterMail_PR);
        resetCode = findViewById(R.id.resetCode_PR);
        enterNewPass = findViewById(R.id.enterNewPass_PR);
        confirmNewPass = findViewById(R.id.confirmNewPass_PR);
        submitMailButt = findViewById(R.id.submitMail_PR);
        confirmResetCodeButt = findViewById(R.id.confirmResetCode_PR);
        changePassButt = findViewById(R.id.changePass_PR);
    }

    private void activateButtons()
    {
        backButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PassResetActivity.super.onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        confirmResetCodeButt.setEnabled(false);
        confirmResetCodeButt.setBackgroundColor(Color.GRAY);
        changePassButt.setEnabled(false);
        changePassButt.setBackgroundColor(Color.GRAY);

        submitMailButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Validator.ValidateUserEmail(enterMail.getText().toString()))
                {
                    mAuth.sendPasswordResetEmail(enterMail.getText().toString());
                    submitMailButt.setEnabled(false);
                    submitMailButt.setBackgroundColor(Color.GRAY);
                    confirmResetCodeButt.setEnabled(true);
                    confirmResetCodeButt.setBackgroundColor(R.drawable.button_pretty);
                }
                else
                {
                    enterMail.setError("כתובת המייל שהזנת אינה חוקית");
                }
            }
        });

        confirmResetCodeButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mAuth.verifyPasswordResetCode(resetCode.getText().toString()).toString().compareTo(enterMail.getText().toString()) == 0)
                {
                    confirmResetCodeButt.setEnabled(false);
                    confirmResetCodeButt.setBackgroundColor(Color.GRAY);
                    changePassButt.setEnabled(true);
                    changePassButt.setBackgroundColor(R.drawable.button_pretty);
                }
                else
                {
                    resetCode.setError("קוד האיפוס שהזנת אינו תקין");
                }
            }
        });

        changePassButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String pass = enterNewPass.getText().toString();
                String passConfirm = confirmNewPass.getText().toString();

                if (pass.compareTo(passConfirm) == 0)
                {
                    if (Validator.ValidateUserPassword(pass))
                    {
                        mAuth.confirmPasswordReset(resetCode.getText().toString(), pass);
                    }
                    else
                    {
                        enterNewPass.setError("הסיסמה חייבת להיות באורך 6 תווים לפחות");
                    }
                }
                else
                {
                    confirmNewPass.setError("הסיסמאות שהזנת אינן תואמות");
                }
            }
        });
    }
}