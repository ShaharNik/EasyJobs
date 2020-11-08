package com.example.easyjobs;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class FirebaseDBJobs extends FirebaseBaseModel {

    public void addNewJob(String user_id, String desc, int price, String loc, Date date, int CatID){
        Job j = new Job(111, user_id, desc, price, loc, date, CatID);
        String id = idGenerator.tokenGenerator();
        ref.child("Jobs").child(id).setValue(j);
    }

    public DatabaseReference getJobByID(String jobID){
        return ref.child("Jobs").child(jobID);
    }

    public DatabaseReference getAllJobs(){
        return ref.child("Jobs");
    }
}
