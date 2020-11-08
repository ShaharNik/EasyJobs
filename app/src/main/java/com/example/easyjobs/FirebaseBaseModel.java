package com.example.easyjobs;

import com.google.firebase.database.*;

public class FirebaseBaseModel {
    protected DatabaseReference ref;
    public FirebaseBaseModel(){ ref = FirebaseDatabase.getInstance().getReference(); }
}