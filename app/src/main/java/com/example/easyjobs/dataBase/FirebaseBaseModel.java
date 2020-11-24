package com.example.easyjobs.dataBase;

import com.google.firebase.database.*;

public class FirebaseBaseModel {

    protected static DatabaseReference ref;

    private FirebaseBaseModel(){

    }
    public static synchronized DatabaseReference getRef()
    {
        if(ref == null)
        {
            ref = FirebaseDatabase.getInstance().getReference();
        }
        return ref;
    }

}