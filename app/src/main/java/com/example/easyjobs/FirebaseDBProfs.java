package com.example.easyjobs;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.List;

public class FirebaseDBProfs extends FirebaseBaseModel {

    public void addNewProf(String user_id, String desc, List<Integer> cats, String loc){
        Prof p = new Prof(user_id, desc, cats, loc);
        String id = idGenerator.tokenGenerator();
        ref.child("Profs").child(id).setValue(p);
    }

    public DatabaseReference getJobByID(String ProfID){
        return ref.child("Prof").child(ProfID);
    }

    public DatabaseReference getAllJobs(){
        return ref.child("Prof");
    }
}
