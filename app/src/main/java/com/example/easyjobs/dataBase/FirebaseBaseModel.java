package com.example.easyjobs.dataBase;

import com.google.firebase.database.*;

public class FirebaseBaseModel {
    protected DatabaseReference ref;
    public FirebaseBaseModel(){ ref = FirebaseDatabase.getInstance().getReference(); }
}