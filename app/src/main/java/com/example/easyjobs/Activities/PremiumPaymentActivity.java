package com.example.easyjobs.Activities;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class PremiumPaymentActivity extends AppCompatActivity {

    private EditText cardNumberTextView;
    private EditText cardNumberCVC;
    private Button acceptButton;
    private ImageView backBLA;
    private FirebaseAuth fa;

    private Spinner monthsSpinner;
    private  Spinner yearsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_payment_activity);

        findViews();
        //showDatePickerDialog();
        setUpMonthsSpinner();
        setUpYearsSpinner();
        activateButtonsAndViews();

    }

//    private void showDatePickerDialog() {// Get open DatePickerDialog button.
//        dateTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.
//                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
//                        StringBuffer strBuf = new StringBuffer();
//                        //strBuf.append("You select date is ");
//                        strBuf.append(year);
//                        strBuf.append("/");
//                        strBuf.append(month+1);
//                        strBuf.append("/");
//                        strBuf.append(dayOfMonth);
//
//                        //TextView datePickerValueTextView = (TextView)findViewById(R.id.datePickerValue);
//                        dateTextView.setText(strBuf.toString());
//                    }
//                };
//
//                // Get current year, month and day.
//                Calendar now = Calendar.getInstance();
//                int year = now.get(java.util.Calendar.YEAR);
//                int month = now.get(java.util.Calendar.MONTH);
//                int day = now.get(java.util.Calendar.DAY_OF_MONTH);
//
//                //Create the new DatePickerDialog instance.
//             //   DatePickerDialog datePickerDialog = new DatePickerDialog(PremiumPaymentActivity.this, onDateSetListener, year, month, day);
//                //DatePickerDialog datePickerDialog = new DatePickerDialog(PremiumPayment_activity.this, android.R.style.Theme_Holo_Dialog, onDateSetListener, year, month, day);
//               DatePickerDialog datePickerDialog = new DatePickerDialog(PremiumPaymentActivity.this, android.R.style.Theme_Holo_Dialog, onDateSetListener, year, month, day);
//
//                //Set dialog icon and title.
//                //datePickerDialog.setIcon(R.drawable.if_snowman);
//                datePickerDialog.setTitle("אנא בחר תאריך");
//
//                //Popup the dialog.
//                datePickerDialog.show();
//            }
//        });
//    }

    private void findViews(){
        backBLA = findViewById(R.id.back_premium_payment);
        //dateTextView = findViewById(R.id.avtivity_premium_payment_cardExprire);
        cardNumberTextView = findViewById(R.id.avtivity_premium_payment_cardNumber_PlainText);
        cardNumberCVC = findViewById(R.id.avtivity_premium_payment_cardNumberCVC);
        acceptButton = findViewById(R.id.activity_premium_payment_acceptButton);
        monthsSpinner = findViewById(R.id.monthSpinner);
        yearsSpinner = findViewById(R.id.yearSpinner);
    }
    private  void setUpMonthsSpinner() //TODO
    {
        Resources res = getResources();
        String[] MonthsValues = res.getStringArray(R.array.months_array);
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, MonthsValues);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsSpinner.setAdapter(adp1);
    }
    private  void setUpYearsSpinner()//TODO
    {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> yearsValues = new ArrayList<>();
        for (int i=0;i<10;i++)
            yearsValues.add(year + i);
        ArrayAdapter<Integer> adp1 = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_list_item_1, yearsValues);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsSpinner.setAdapter(adp1);
    }
    private boolean validateFields()
    {
        // Validate card number
        if (cardNumberTextView.getText().toString().length() != 16) {
            cardNumberTextView.setError("עליך להקליד 16 ספרות בדיוק");
            return false;
        }
        else if (!cardNumberTextView.getText().toString().matches("[0-9]+"))
        {
            cardNumberCVC.setError("עליך להקליד ספרות בלבד");
            return false;
        }
        // Validate CVV
        if (cardNumberCVC.getText().toString().length() != 3) {
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
    private void activateButtonsAndViews(){
        backBLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PremiumPaymentActivity.super.onBackPressed();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields())
                {
                    fa = FirebaseAuth.getInstance();
                    FirebaseUser user = fa.getCurrentUser();
                  //  FirebaseDBUsers userDB = new FirebaseDBUsers();
                    FirebaseDBUsers.setPremiumToAUser(user.getUid(), PremiumPaymentActivity.this);
                    PremiumPaymentActivity.super.onBackPressed(); // move him back
                }
                else
                    Toast.makeText(PremiumPaymentActivity.this, "השדרוג נכשל.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
