package com.example.easyjobs.dataBase;

import com.google.firebase.database.DatabaseReference;

public class FirebaseDBCategories extends FirebaseBaseModel {

    public DatabaseReference getCatByID(String CatID){
        return ref.child("Categories").child(CatID);
    }

    public DatabaseReference getAllCat(){
        return ref.child("Categories");
    }
}
