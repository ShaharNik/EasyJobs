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
    public void addUserToDB(String firstName,String lastName,String phoneNumber,String email, boolean isPremium,String password)
    {
        User user = new User(firstName,lastName,phoneNumber,email,isPremium,password,0);
        String id = idGenerator.tokenGenerator();
        ref.child("Users").child(id).setValue(user);
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
