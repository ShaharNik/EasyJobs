package com.example.easyjobs.Activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PremiumPaymentActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText cardNumberTextView;
    private EditText cardNumberCVC;
    private Button acceptButton;
    private ImageView backBLA;
    private FirebaseAuth fa;

    private Spinner monthsSpinner;
    private Spinner yearsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_payment_activity);

        findViews();
        activateButtons();
    }

    private void findViews()
    {
        backBLA = findViewById(R.id.back_premium_payment);
        cardNumberTextView = findViewById(R.id.avtivity_premium_payment_cardNumber_PlainText);
        cardNumberCVC = findViewById(R.id.avtivity_premium_payment_cardNumberCVC);
        acceptButton = findViewById(R.id.activity_premium_payment_acceptButton);
        monthsSpinner = findViewById(R.id.monthSpinner);
        yearsSpinner = findViewById(R.id.yearSpinner);
    }

    private void activateButtons()
    {
        setUpMonthsSpinner();
        setUpYearsSpinner();
        backBLA.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View ClickedButton)
    {
        if (backBLA.equals(ClickedButton))
        {
            PremiumPaymentActivity.super.onBackPressed();
        }
        else if (acceptButton.equals(ClickedButton))
        {
            if (validateFields())
            {
                fa = FirebaseAuth.getInstance();
                FirebaseUser user = fa.getCurrentUser();
                FirebaseDBUsers.setPremiumToAUser(user.getUid(), PremiumPaymentActivity.this);
                PremiumPaymentActivity.super.onBackPressed(); // move him back
            }
            else
            {
                Toast.makeText(PremiumPaymentActivity.this, "השדרוג נכשל.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpMonthsSpinner()
    {
        Resources res = getResources();
        String[] MonthsValues = res.getStringArray(R.array.months_array);
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MonthsValues);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsSpinner.setAdapter(adp1);
    }

    private void setUpYearsSpinner()
    {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> yearsValues = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            yearsValues.add(year + i);
        }
        ArrayAdapter<Integer> adp1 = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, yearsValues);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsSpinner.setAdapter(adp1);
    }

    private boolean validateFields()
    {
        // Validate card number
        if (cardNumberTextView.getText().toString().length() != 16)
        {
            cardNumberTextView.setError("עליך להקליד 16 ספרות בדיוק");
            return false;
        }
        else if (!cardNumberTextView.getText().toString().matches("[0-9]+"))
        {
            cardNumberCVC.setError("עליך להקליד ספרות בלבד");
            return false;
        }
        // Validate CVC
        if (cardNumberCVC.getText().toString().length() != 3)
        {
            cardNumberCVC.setError("עליך להקליד 3 ספרות בלבד");
            return false;
        }
        else if (!cardNumberCVC.getText().toString().matches("[0-9]+"))
        {
            cardNumberCVC.setError("עליך להקליד ספרות בלבד");
            return false;
        }
        return true;
    }
}
