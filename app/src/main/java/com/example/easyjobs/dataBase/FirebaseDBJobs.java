package com.example.easyjobs.dataBase;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyjobs.Activities.Jobs.AdminEditJobActivity;
import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.utils.idGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class FirebaseDBJobs
{
    public static void addNewJob(String user_id, String desc, int price, String loc, Date startDate, Date endDate, String CatID, ArrayList<Uri> picsUri)
    {
        String id = idGenerator.tokenGenerator();
        Job j = new Job(id, user_id, desc, price, loc, startDate, endDate, CatID);
        FirebaseBaseModel.getRef().child("Jobs").child(id).setValue(j);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        for (Uri u : picsUri)
        {
            StorageReference storageReference = mStorageRef.child("JobPictures/" + id + "/" + u.getLastPathSegment());
            storageReference.putFile(u).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    System.out.println("GOOD !");
                }
            });
        }
    }

    public static DatabaseReference getJobByID(String jobID)
    {
        return FirebaseBaseModel.getRef().child("Jobs").child(jobID);
    }

    public static DatabaseReference getAllJobs()
    {
        return FirebaseBaseModel.getRef().child("Jobs");
    }

    public static void editJob(String job_id, String user_id, String desc, int price, String loc, Date startDate, Date endDate, String CatID, AdminEditJobActivity c, ArrayList<Uri> picsUri)
    {
        Job j = new Job(job_id, user_id, desc, price, loc, startDate, endDate, CatID);
        FirebaseBaseModel.getRef().child("Jobs").child(job_id).setValue(j).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
                for (Uri u : picsUri)
                {
                    StorageReference storageReference = mStorageRef.child("JobPictures/" + j.getJob_ID() + "/" + u.getLastPathSegment());
                    storageReference.putFile(u).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                        {
                            System.out.println("GOOD !");
                        }
                    });
                }
                Toast.makeText(c, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                c.onBackPressed();
            }
        });
    }

    public static void RemoveJob(String jobId, AdminEditJobActivity c)
    {
        FirebaseBaseModel.getRef().child("Jobs").child(jobId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                com.example.easyjobs.dataBase.FirebaseStorage.deleteJobPictures(jobId); //TODO Check!
                c.finish();
            }
        });
    }
}
