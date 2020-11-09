package com.example.easyjobs;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseDBUsers extends FirebaseBaseModel  {

    //get all users
    //get specific user
    //add user
    /*
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    boolean isPremium;
    String password; //Think if we need here?
    // PROF PIC // THINK HOW?
    float rating;
     */
    public void addUserToDB(String User_ID, String firstName,String lastName,String phoneNumber, boolean isPremium)
    {
        //String id = idGenerator.tokenGenerator();
        User user = new User(User_ID, firstName,lastName,phoneNumber,isPremium,0);
        ref.child("Users").child(User_ID).setValue(user);
    }
    public DatabaseReference getAllusers()
    {
        return ref.child("Users");
    }
    public DatabaseReference getUserByID(String id)
    {
        return ref.child("Users").child(id);
    }
}
