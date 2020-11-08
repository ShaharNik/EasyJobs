package com.example.easyjobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class JobProfileActivity extends AppCompatActivity {

    private ImageView backBJP;
    TextView tw1;
    TextView tw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_profile);

        backBJP = findViewById(R.id.back_job_profile);
        backBJP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobProfileActivity.super.onBackPressed();
            }
        });

        tw1 = findViewById(R.id.twb1);
        tw2 = findViewById(R.id.twb2);

        FirebaseDBJobs db = new FirebaseDBJobs();
        DatabaseReference dr = db.getJobByID(getIntent().getStringExtra("job_id"));
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job j = snapshot.getValue(Job.class);
                tw1.setText(j.desc);
                tw2.setText(j.location);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
}