package com.example.easyjobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JobsListActivity extends AppCompatActivity {

    private Button jobProfile;
    private Button postJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_list);

        jobProfile = findViewById(R.id.jobList_to_JobProfile);
        jobProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToJobProfile();
            }
        });

        postJob = findViewById(R.id.jobList_to_PostJob);
        postJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPostJob();
            }
        });
    }

    public void moveToJobProfile(){
        Intent i = new Intent(JobsListActivity.this, JobProfileActivity.class);
        startActivity(i);
    }

    public void moveToPostJob(){
        Intent i = new Intent(JobsListActivity.this, PostJobActivity.class);
        startActivity(i);
    }

    public void moveToMain(View v){
        Intent i = new Intent(JobsListActivity.this, MainActivity.class);
        startActivity(i);
    }
}