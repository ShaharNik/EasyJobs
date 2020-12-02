package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEditJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backButton;
    private TextView namesText;
    private EditText descText;
    private EditText locText;
    private EditText priceText;
    private TextView dateText;
    private TextView catText;
    private Spinner spinner;
    private Button approveChanges;
    private Button deletePost;

    private User user;
    private Job job;
    String cat_id;

    private boolean changedIt;

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
    }

    private void findViews() {
        backButton = findViewById(R.id.back_admin_editPost);
        namesText = findViewById(R.id.namesEJ);
        descText = findViewById(R.id.descriptionEJ);
        locText = findViewById(R.id.locationEJ);
        priceText = findViewById(R.id.priceEJ);
        dateText = findViewById(R.id.dateEJ);
        catText = findViewById(R.id.catEJ);
        spinner = findViewById(R.id.AdminPickCatEditJob);
        approveChanges = findViewById(R.id.changeJobDetails);
        deletePost = findViewById(R.id.deleteJobButton);
    }

    private void inputTempData() {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        descText.setText(job.getDesc());
        locText.setText(job.getLocation());
        priceText.setText(job.getPrice());
        dateText.setText(job.getStartDate() + " - " + job.getEndDate());
        catText.setText(job.getCategory_ID());
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
                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(AdminEditJobActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        spinner.setOnItemSelectedListener(this);
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
                FirebaseDBJobs.editJob(job.getJob_ID(), user.getUser_ID(), descText.getText().toString(), Integer.parseInt(priceText.getText().toString()), locText.getText().toString(), job.getStartDate(), job.getEndDate(), cat_id);
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