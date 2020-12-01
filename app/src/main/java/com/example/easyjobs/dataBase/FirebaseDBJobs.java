package com.example.easyjobs.dataBase;

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.utils.idGenerator;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class FirebaseDBJobs {

    public static void addNewJob(String user_id, String desc, int price, String loc, Date startDate,Date endDate, String CatID){
        String id = idGenerator.tokenGenerator();
        Job j = new Job(id, user_id, desc, price, loc, startDate,endDate, CatID);
        FirebaseBaseModel.getRef().child("Jobs").child(id).setValue(j);
    }

    public static DatabaseReference getJobByID(String jobID){
        return FirebaseBaseModel.getRef().child("Jobs").child(jobID);
    }

    public static DatabaseReference getAllJobs(){
        return FirebaseBaseModel.getRef().child("Jobs");
    }
    public static void editJob(String job_id,String user_id, String desc, int price, String loc, Date startDate,Date endDate, String CatID)
    {
        Job j = new Job(job_id, user_id, desc, price, loc, startDate,endDate, CatID);
        FirebaseBaseModel.getRef().child("Jobs").child(job_id).setValue(j);
    }
    public static void RemoveJob(String jobId)
    {
        FirebaseBaseModel.getRef().child("Jobs").child(jobId).removeValue();

    }
}
