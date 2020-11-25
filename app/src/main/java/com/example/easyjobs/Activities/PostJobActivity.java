package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.utils.Validator;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class PostJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backBPJ;
    private EditText descED;
    private EditText locED;
    private EditText priceED;
    private Button postJobB;
    private Button chooseDate_postJobButton;
    private EditText dateEditTextPostJob;
    private Spinner spinnerPJ;
    private Date startD;
    private Date endD;
    private int catNum = 0;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        findViews();
        activateButtons();
        setUpSpinner();
    }

    private void findViews(){
        backBPJ = findViewById(R.id.back_post_job);
        descED = findViewById(R.id.editDescPJ);
        locED = findViewById(R.id.editLocPJ);
        priceED = findViewById(R.id.editPricePJ);
        postJobB = findViewById(R.id.postJobBtn);
        spinnerPJ = findViewById(R.id.pickCategoryPostJob);
        chooseDate_postJobButton =  findViewById(R.id.chooseDate_postJobButton);
        dateEditTextPostJob = findViewById(R.id.dateEditTextPostJob);
    }

    private void activateButtons(){
        backBPJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { PostJobActivity.super.onBackPressed(); }
        });

        postJobB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postJobToDB();
            }
        });

        chooseDate_postJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
    }

    private void pickDate()
    {
        MaterialDatePicker.Builder<Pair<Long, Long>> builderRange = MaterialDatePicker.Builder.dateRangePicker();
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        Date min = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.setTime(min);
        c.add(Calendar.MONTH, 3);
        Date currentDatePlusOne = c.getTime();
        CalendarConstraints.DateValidator dateValidatorMin = DateValidatorPointForward.from(min.getTime());
        CalendarConstraints.DateValidator dateValidatorMax = DateValidatorPointBackward.before(currentDatePlusOne.getTime());

        ArrayList<CalendarConstraints.DateValidator> listValidators =
                new ArrayList<CalendarConstraints.DateValidator>();
        listValidators.add(dateValidatorMin);
        listValidators.add(dateValidatorMax);
        CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);
        constraintsBuilderRange.setValidator(validators);

        builderRange.setCalendarConstraints(constraintsBuilderRange.build());
        MaterialDatePicker<Pair<Long, Long>> pickerRange = builderRange.build();
        pickerRange.show(getSupportFragmentManager(), pickerRange.toString());
        pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                Pair<Long,Long> s = pickerRange.getSelection();
                startD = new Date(s.first);
                endD = new Date(s.second);
                DateFormat df = new SimpleDateFormat("dd/MM/yy");
                dateEditTextPostJob.setText(df.format(startD)+" - "+ df.format(endD));
            }
        });
    }

    private void setUpSpinner(){
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> items = new ArrayList<>();
                for(DataSnapshot category : snapshot.getChildren()){
                    Category c = category.getValue(Category.class);
                    items.add(c);
                }
                Category[] catArray = new Category[items.size()];
                items.toArray(catArray);
                Arrays.sort(catArray);
                ArrayList<String> str = new ArrayList<>();
                for(int i=0;i<catArray.length;i++) {
                    str.add(catArray[i].getCat_name());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostJobActivity.this, android.R.layout.simple_spinner_dropdown_item, str);
                spinnerPJ.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        spinnerPJ.setOnItemSelectedListener(this);
    }

    private void postJobToDB(){//need to configure Date !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        String desc = descED.getText().toString();
        String loc = locED.getText().toString();
        String price = priceED.getText().toString();
        String date = dateEditTextPostJob.getText().toString();
        if (Validator.ValidateDescription(desc))
        {
            if (Validator.ValidateLocation(loc))
            {
                if (!date.isEmpty())
                {
                    if (Validator.ValidatePrice(price))
                    {
                        // --=== Add new Job and move user to back activity ==---
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseDBJobs.addNewJob(user.getUid(), desc, Integer.parseInt(price), loc, startD, endD, catNum);
                        Toast.makeText(PostJobActivity.this, "העבודה פורסמה בהצלחה", Toast.LENGTH_SHORT).show();
                        PostJobActivity.super.onBackPressed();
                    }
                    else
                        priceED.setError("אנא הזן מחיר");
                }
                else
                    dateEditTextPostJob.setError("אנא בחר תאריכים");
            }
            else
                locED.setError("המיקום אינו תקין, חייב להיות באורך 3 לפחות");
        }
        else
            descED.setError("התיאור קצר מידי");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        catNum = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        catNum = 0;
    }
}