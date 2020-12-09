package com.example.easyjobs.Activities.Jobs;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.ImageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class JobProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int PHONE_CALL_APPROVE = 420;

    private ImageView backBJP;
    private TextView namesJPTV;
    private TextView descJPTV;
    private TextView locationJPTV;
    private TextView priceJPTV;
    private TextView datesJPTV;
    private TextView phoneJPTV;
    private ImageButton phoneCall;
    private ImageView JobProfileFirstPicture;
    private Button adminEditJob;
    private StorageReference mStorageRef;
    private ArrayList<Picture> localFile;
    private Job job;
    private User user;
    private ImageView whatsappButt;
    private Dialog d;
    private ViewPager vpPager;
    private viewPageAdapter vpa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_profile);

        findViews();
        inits();
        activateButtons();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        createDialog();
        setDataFromDB();
    }

    private void findViews()
    {
        backBJP = findViewById(R.id.back_job_profile);
        namesJPTV = findViewById(R.id.namesJP);
        descJPTV = findViewById(R.id.descriptionJP);
        locationJPTV = findViewById(R.id.locationJP);
        priceJPTV = findViewById(R.id.priceJP);
        datesJPTV = findViewById(R.id.dateJP);
        phoneJPTV = findViewById(R.id.phoneJP);
        phoneCall = findViewById(R.id.call_buttJ);
        JobProfileFirstPicture = findViewById(R.id.JobProfileFirstPicture);
        adminEditJob = findViewById(R.id.admin_edit_job);
        whatsappButt = findViewById(R.id.whatsappButtImageButt);
    }

    private void inits()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        localFile = new ArrayList<>();
        JobProfileFirstPicture.setEnabled(false);
        if (FirebaseDBUsers.isAdmin)
        {
            adminEditJob.setVisibility(View.VISIBLE);
            adminEditJob.setEnabled(true);
        }
        else
        {
            adminEditJob.setVisibility(View.GONE);
            adminEditJob.setEnabled(false);
        }
    }

    private void setDataFromDB()
    {
        if (vpa != null)
        {
            localFile.clear();
            vpa.notifyDataSetChanged();
        }
        String job_ID = getIntent().getStringExtra("job_id");
        DatabaseReference drJobs = FirebaseDBJobs.getJobByID(job_ID);
        drJobs.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                job = snapshot.getValue(Job.class);
                vpa = new viewPageAdapter(JobProfileActivity.this, localFile, true, false);
                vpPager.setAdapter(vpa);
                if (job == null)
                {
                    JobProfileActivity.this.onBackPressed();
                }
                else
                {
                    descJPTV.setText("תיאור: " + job.getDesc());
                    locationJPTV.setText("מיקום עבודה: " + job.getLocation());
                    priceJPTV.setText("מחיר: " + job.getPrice() + " שח");
                    DateFormat df = new SimpleDateFormat("dd/MM/yy");
                    datesJPTV.setText("תאריך: " + df.format(job.getStartDate()) + " - " + df.format(job.getEndDate()));
                    DatabaseReference drUser = FirebaseDBUsers.getUserByID(job.getUser_ID());
                    if (job.getUser_ID().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) == 0)
                    {
                        adminEditJob.setVisibility(View.VISIBLE);
                        adminEditJob.setEnabled(true);
                    }
                    drUser.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            user = snapshot.getValue(User.class);
                            namesJPTV.setText("שם: " + user.getFirstName() + " " + user.getLastName());
                            phoneJPTV.setText("טלפון: " + user.getPhoneNumber());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        setFirstPicture(job_ID);
    }

    private void activateButtons()
    {
        backBJP.setOnClickListener(this);
        phoneCall.setOnClickListener(this);
        adminEditJob.setOnClickListener(this);
        JobProfileFirstPicture.setOnClickListener(this);
        whatsappButt.setOnClickListener(this);
    }

    @Override
    public void onClick(View ClickedButton) {
        if(backBJP.equals(ClickedButton))
        {
            JobProfileActivity.super.onBackPressed();
        }
        if(phoneCall.equals(ClickedButton))
        {
            phoneCallMaker();
        }
        if(adminEditJob.equals(ClickedButton))
        {
            Intent i = new Intent(JobProfileActivity.this, AdminEditJobActivity.class);
            i.putExtra("Job", job);
            i.putExtra("User", user);
            i.putExtra("File", localFile);
            startActivity(i);
        }
        if(JobProfileFirstPicture.equals(ClickedButton))
        {
            showDialog();
        }
        if(whatsappButt.equals(ClickedButton))
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://wa.me/972" + user.getPhoneNumber()));
            startActivity(intent);
        }
    }

    private void setFirstPicture(String job_id)
    {
        StorageReference riversRef = mStorageRef.child("JobPictures/").child(job_id);
        riversRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>()
        {
            @Override
            public void onSuccess(ListResult listResult)
            {
                for (StorageReference sr : listResult.getItems())
                {
                    ImageHelper.pullImagesFromDBandInsertToArray(sr,JobProfileFirstPicture,localFile,vpa);
                }
            }
        });
    }

    private void createDialog()
    {
        d = new Dialog(JobProfileActivity.this);
        d.setContentView(R.layout.view_pager_layout);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.setTitle("Pictures");
        d.setCancelable(true);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
    }

    private void showDialog()
    {
        d.show();
    }

    private void phoneCallMaker()
    {
        if (ActivityCompat.checkSelfPermission(JobProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(JobProfileActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_APPROVE);
        }
        else
        {
            String s = "tel:" + phoneJPTV.getText().toString();
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse(s));
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PHONE_CALL_APPROVE:
                String s = "tel:" + phoneJPTV.getText().toString();
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse(s));
                startActivity(i);
                break;
        }
    }


}