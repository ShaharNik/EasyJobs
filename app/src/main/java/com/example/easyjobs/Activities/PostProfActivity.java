package com.example.easyjobs.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PostProfActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backBPP;

    private Spinner spinnerPP;
    private int catNum = 0;
    private EditText descED;
    private EditText locED;
    private EditText IdED;
    private FirebaseAuth mAuth;

    private Button postProfB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prof);

        findViews();
        activateButtons();
        setUpSpinner();
    }

    private void findViews(){
        descED = findViewById(R.id.editDescPP);
        locED = findViewById(R.id.editLocPP);
        IdED = findViewById(R.id.editIdPP);
        backBPP = findViewById(R.id.back_post_prof);
        postProfB = findViewById(R.id.postProfBtn);
        spinnerPP = findViewById(R.id.pickCategoryPostProf);
    }

    private void activateButtons(){
        backBPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostProfActivity.super.onBackPressed();
            }
        });

        postProfB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postJobToDB();
            }
        });
    }

    //Gotta configure yet
    private void setUpSpinner(){
        String[] items = new String[]{"1", "2", "three"};
        // need to set up categories on db and make the string connecting there.
        String[] items2 = new String[7];

        //create an adapter to describe how the items are displayed.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinnerPP.setAdapter(adapter);
        spinnerPP.setOnItemSelectedListener(this);
    }

    public void postJobToDB(){//need to configure Multiple Category !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        FirebaseDBProfs db = new FirebaseDBProfs();
        String desc = descED.getText().toString();
        String loc = locED.getText().toString();
        String id = IdED.getText().toString();
        if (id.length() == 9){
            try { int temp = Integer.parseInt(id); }
            catch(Exception e){ id = "000000000"; } //Maybe pop-up window to tell user to insert int?????????????
            //Check ID Algorithm?????????????????????????????????????????????????????
        }
        else {
            id = "000000000";
            //Maybe pop-up window to tell user to insert 9-digits (include sifrat bikoret)????????????????????
        }
        List<Integer> temp = new ArrayList<Integer>();
        temp.add(5);
        temp.add(6);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db.addNewProf(user.getUid(), desc, temp, loc);
        PostProfActivity.super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        catNum = position;//Need to make it multiple choice.
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        catNum = 0;
    }
}