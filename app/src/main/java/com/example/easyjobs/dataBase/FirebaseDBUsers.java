package com.example.easyjobs.dataBase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyjobs.Objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public class FirebaseDBUsers extends FirebaseBaseModel {

    public void addUserToDB(String User_ID, String firstName,String lastName,String phoneNumber, boolean isPremium) {
        //String id = idGenerator.tokenGenerator();
        User user = new User(User_ID, firstName,lastName,phoneNumber,isPremium,0, 0);
        ref.child("Users").child(User_ID).setValue(user);
    }
    public void changeUserByID(User user) {
        ref.child("Users").child(user.getUser_ID()).setValue(user);
    }

    public DatabaseReference getAllusers()
    {
        return ref.child("Users");
    }

    public DatabaseReference getUserByID(String id)
    {
        return ref.child("Users").child(id);
    }

    public void setPremiumToAUser(String id, Context context) {
        ref.child("Users").child(id).child("premium").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "payment successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
