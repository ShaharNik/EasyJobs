package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminEditJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backButton;
    private TextView namesText;
    private EditText descText;
    private EditText locText;
    private EditText priceText;
    private TextView dateText;
    private Spinner spinner;
    private Button approveChanges;
    private Button deletePost;

    private User user;
    private Job job;
    String cat_id;

    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_job);

        user = (User) getIntent().getSerializableExtra("User");
        job = (Job) getIntent().getSerializableExtra("Job");

        findViews();
        inputTempData();
        setUpSpinner();
        activateButtons();
        setupDialog();
    }

    private void findViews() {
        backButton = findViewById(R.id.back_admin_editPost);
        namesText = findViewById(R.id.namesEJ);
        descText = findViewById(R.id.descriptionEJ);
        locText = findViewById(R.id.locationEJ);
        priceText = findViewById(R.id.priceEJ);
        dateText = findViewById(R.id.dateEJ);
        spinner = findViewById(R.id.AdminPickCatEditJob);
        approveChanges = findViewById(R.id.changeJobDetails);
        deletePost = findViewById(R.id.deleteJobButton);
        builder = new AlertDialog.Builder(this);
    }

    private void inputTempData() {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        descText.setText(job.getDesc());
        locText.setText(job.getLocation());
        priceText.setText(job.getPrice()+"");

        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        dateText.setText(df.format(job.getStartDate())+" - " + df.format(job.getEndDate()));
    }

    private void setUpSpinner(){
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> items = new ArrayList<>();
                int index=-1;
                int rightIndex = 0;
                for(DataSnapshot category : snapshot.getChildren()){
                    Category c = category.getValue(Category.class);
                    items.add(c);
                    index++;
                    if(c.getCategory_id().compareTo(job.getCategory_ID())==0) {
                        rightIndex=index;
                    }

                }

                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(AdminEditJobActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);
                spinner.setSelection(rightIndex,false);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        spinner.setOnItemSelectedListener(this);

    }

    private void setupDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        FirebaseDBJobs.RemoveJob(job.getJob_ID(),AdminEditJobActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(AdminEditJobActivity.this, ":)", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        builder.setMessage("האם אתה בטוח?").setPositiveButton("כן", dialogClickListener)
                .setNegativeButton("לא", dialogClickListener);
    }
    private void activateButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEditJobActivity.super.onBackPressed();
            }
        });

        approveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean changedIt = true;
                if (!Validator.ValidateDescription(descText.getText().toString())) {
                    changedIt = false;
                    descText.setError("תיאור לא טוב");
                }
                if (!Validator.ValidateLocation(locText.getText().toString())) {
                    changedIt = false;
                    locText.setError("מיקום לא טוב");
                }
                if (changedIt) {
                    FirebaseDBJobs.editJob(job.getJob_ID(), user.getUser_ID(), descText.getText().toString(), Integer.parseInt(priceText.getText().toString()), locText.getText().toString(), job.getStartDate(), job.getEndDate(), cat_id, AdminEditJobActivity.this);
                }
            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   builder.show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cat_id = ((Category)spinner.getAdapter().getItem(position)).getCategory_id();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        job.getCategory_ID();
    }
}