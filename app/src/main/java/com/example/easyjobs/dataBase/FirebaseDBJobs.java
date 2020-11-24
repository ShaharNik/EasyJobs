package com.example.easyjobs.dataBase;

import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.utils.idGenerator;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class FirebaseDBJobs {

    public static void addNewJob(String user_id, String desc, int price, String loc, Date startDate,Date endDate, int CatID){
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
}
