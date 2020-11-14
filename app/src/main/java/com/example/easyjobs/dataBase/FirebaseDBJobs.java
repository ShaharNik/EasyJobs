package com.example.easyjobs.dataBase;

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.utils.idGenerator;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class FirebaseDBJobs extends FirebaseBaseModel {

    public void addNewJob(String user_id, String desc, int price, String loc, Date date, int CatID){
        String id = idGenerator.tokenGenerator();
        Job j = new Job(id, user_id, desc, price, loc, date, CatID);
        ref.child("Jobs").child(id).setValue(j);
    }

    public DatabaseReference getJobByID(String jobID){
        return ref.child("Jobs").child(jobID);
    }

    public DatabaseReference getAllJobs(){
        return ref.child("Jobs");
    }
}
