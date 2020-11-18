package com.example.easyjobs.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class PremiumPaymentActivity extends AppCompatActivity {

    private EditText dateTextView;
    private EditText cardNumberTextView;
    private EditText cardNumberCVC;
    private Button acceptButton;
    private ImageView backBLA;
    private FirebaseAuth fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_payment_activity);

        findViews();
        acvtivateButtonsAndViews();

        //setNormalPicker();

        showDatePickerDialog();
    }

    private void showDatePickerDialog() {// Get open DatePickerDialog button.
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        StringBuffer strBuf = new StringBuffer();
                        //strBuf.append("You select date is ");
                        strBuf.append(year);
                        strBuf.append("/");
                        strBuf.append(month+1);
                        strBuf.append("/");
                        strBuf.append(dayOfMonth);

                        //TextView datePickerValueTextView = (TextView)findViewById(R.id.datePickerValue);
                        dateTextView.setText(strBuf.toString());
                    }
                };

                // Get current year, month and day.
                Calendar now = Calendar.getInstance();
                int year = now.get(java.util.Calendar.YEAR);
                int month = now.get(java.util.Calendar.MONTH);
                int day = now.get(java.util.Calendar.DAY_OF_MONTH);

                //Create the new DatePickerDialog instance.
                //DatePickerDialog datePickerDialog = new DatePickerDialog(DateTimePickerDialogActivity.this, onDateSetListener, year, month, day);
                //DatePickerDialog datePickerDialog = new DatePickerDialog(PremiumPayment_activity.this, android.R.style.Theme_Holo_Dialog, onDateSetListener, year, month, day);
                DatePickerDialog datePickerDialog = new DatePickerDialog(PremiumPaymentActivity.this, android.R.style.Theme_Holo_Dialog, onDateSetListener, year, month, day);

                //Set dialog icon and title.
                //datePickerDialog.setIcon(R.drawable.if_snowman);
                datePickerDialog.setTitle("Please select date.");

                //Popup the dialog.
                datePickerDialog.show();
            }
        });
    }

    private void findViews(){
        backBLA = findViewById(R.id.back_premium_payment);
        dateTextView = findViewById(R.id.avtivity_premium_payment_cardExprire);
        cardNumberTextView = findViewById(R.id.avtivity_premium_payment_cardNumber_PlainText);
        cardNumberCVC = findViewById(R.id.avtivity_premium_payment_cardNumberCVC);
        acceptButton = findViewById(R.id.activity_premium_payment_acceptButton);
    }

    private void acvtivateButtonsAndViews(){
        backBLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PremiumPaymentActivity.super.onBackPressed();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fa = FirebaseAuth.getInstance();
                FirebaseUser user = fa.getCurrentUser();
                FirebaseDBUsers userDB = new FirebaseDBUsers();
                userDB.setPremiumToAUser(user.getUid(), PremiumPaymentActivity.this);
            }
        });
    }
}