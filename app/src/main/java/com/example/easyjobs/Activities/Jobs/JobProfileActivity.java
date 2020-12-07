package com.example.easyjobs.Activities.Jobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JobProfileActivity extends AppCompatActivity {

    private ImageView backBJP;
    private TextView namesJPTV;
    private TextView descJPTV;
    private TextView locationJPTV;
    private TextView priceJPTV;
    private TextView datesJPTV;
    private TextView phoneJPTV;
    private ImageView JobProfileFirstPicture;
    private Button adminEditJob;
    private StorageReference mStorageRef;
    private ArrayList<Picture> localFile;
    private Job job;
    private User user;
    private Dialog d;
    private ViewPager vpPager;
    private  viewPageAdapter vpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_profile);

        findViews();

        activateButtons();
        createDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataFromDB();
    }

    private void setDataFromDB(){
        if(vpa!=null) {
            localFile.clear();
            vpa.notifyDataSetChanged();
        }
        String job_ID = getIntent().getStringExtra("job_id");
        DatabaseReference drJobs = FirebaseDBJobs.getJobByID(job_ID);
        drJobs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                job = snapshot.getValue(Job.class);
                vpa = new viewPageAdapter(JobProfileActivity.this,localFile,job.getJob_ID(),true,false);
                vpPager.setAdapter(vpa);
                if(job == null)
                {
                    JobProfileActivity.this.onBackPressed();

                }
                else {
                    descJPTV.setText("תיאור: " + job.getDesc());
                    locationJPTV.setText("מיקום עבודה: " + job.getLocation());
                    priceJPTV.setText("מחיר: " + job.getPrice() + " שח");
                    DateFormat df = new SimpleDateFormat("dd/MM/yy");
                    datesJPTV.setText("תאריך: " + df.format(job.getStartDate()) + " - " + df.format(job.getEndDate()));
                    DatabaseReference drUser = FirebaseDBUsers.getUserByID(job.getUser_ID());
                    if(job.getUser_ID().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) ==0)
                    {
                        adminEditJob.setVisibility(View.VISIBLE);
                        adminEditJob.setEnabled(true);
                    }
                    drUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user = snapshot.getValue(User.class);
                            namesJPTV.setText("שם: " + user.getFirstName() + " " + user.getLastName());
                            phoneJPTV.setText("טלפון: " + user.getPhoneNumber());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        setFirstPicture(job_ID);
    }

    private void findViews(){
        mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        localFile = new ArrayList<>();
        backBJP = findViewById(R.id.back_job_profile);
        namesJPTV = findViewById(R.id.namesJP);
        descJPTV = findViewById(R.id.descriptionJP);
        locationJPTV = findViewById(R.id.locationJP);
        priceJPTV = findViewById(R.id.priceJP);
        datesJPTV = findViewById(R.id.dateJP);
        phoneJPTV = findViewById(R.id.phoneJP);
        JobProfileFirstPicture = findViewById(R.id.JobProfileFirstPicture);
        adminEditJob = findViewById(R.id.admin_edit_job);
        JobProfileFirstPicture.setEnabled(false);
    }

    private void activateButtons(){
        backBJP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobProfileActivity.super.onBackPressed();
            }
        });

        if(FirebaseDBUsers.isAdmin){
            adminEditJob.setVisibility(View.VISIBLE);
            adminEditJob.setEnabled(true);
        }
        else{
            adminEditJob.setVisibility(View.GONE);
            adminEditJob.setEnabled(false);
        }

        adminEditJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JobProfileActivity.this, AdminEditJobActivity.class);
                i.putExtra("Job", job);
                i.putExtra("User", user);
                i.putExtra("File",localFile);
                startActivity(i);
            }
        });

        JobProfileFirstPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void setFirstPicture(String job_id)
    {
        StorageReference riversRef = mStorageRef.child("JobPictures/").child(job_id);
        riversRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference sr : listResult.getItems())
                {
                    File Image=null;
                    try {
                       // System.out.println(sr.getName());
                        //System.out.println(sr.getPath());
                       // System.out.println(sr.);
                        Image = File.createTempFile(sr.getName(), ".jpg");
                       // Image.renameTo(sr.getName()+".jpg");
                        File finalImage = Image;
                        sr.getFile(finalImage).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                if(finalImage.exists()){
                                    Bitmap myBitmap = BitmapFactory.decodeFile(finalImage.getAbsolutePath());
                                    JobProfileFirstPicture.setImageBitmap(myBitmap);
                                    Picture pic = new Picture(finalImage,sr.getName());
                                    localFile.add(pic);
                                    vpa.notifyDataSetChanged();
                                    JobProfileFirstPicture.setEnabled(true);
                                }

                            }
                        });
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    private void createDialog()
    {
        d= new Dialog(JobProfileActivity.this);
        d.setContentView(R.layout.view_pager_layout);
        d.setTitle("Pictures");
        d.setCancelable(true);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
    }
    private void showDialog()
    {
        d.show();
    }
}