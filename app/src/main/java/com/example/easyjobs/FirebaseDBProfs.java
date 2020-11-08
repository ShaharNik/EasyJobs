package com.example.easyjobs;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.List;

public class FirebaseDBProfs extends FirebaseBaseModel {

    public void addNewProf(String user_id, String desc, List<Integer> cats, String loc){
        String id = idGenerator.tokenGenerator();
        Prof p = new Prof(id,user_id, desc, cats, loc);
        ref.child("Profs").child(id).setValue(p);
    }

    public DatabaseReference getProfByID(String ProfID){
        return ref.child("Profs").child(ProfID);
    }

    public DatabaseReference getAllProfs(){
        return ref.child("Profs");
    }
}
