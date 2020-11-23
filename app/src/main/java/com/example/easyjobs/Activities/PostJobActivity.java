package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class PostJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backBPJ;
    private EditText descED;
    private EditText locED;
    private EditText priceED;
    private Button postJobB;

    private Spinner spinnerPJ;
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
    }

    private void setUpSpinner(){
        FirebaseDBCategories fb = new FirebaseDBCategories();
        DatabaseReference dr = fb.getAllCat();
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
        FirebaseDBJobs db = new FirebaseDBJobs();
        String desc = descED.getText().toString();
        String loc = locED.getText().toString();
        int price = 0;
        try { price = Integer.parseInt(priceED.getText().toString()); }
        catch(Exception e){ price = 0; } //Maybe pop-up window to tell user to insert int?????????????

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db.addNewJob(user.getUid(), desc, price, loc, new Date(1888880000), catNum);
        PostJobActivity.super.onBackPressed();
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