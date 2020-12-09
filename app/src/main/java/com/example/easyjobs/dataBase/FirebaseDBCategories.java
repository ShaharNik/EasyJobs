package com.example.easyjobs.dataBase;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.utils.idGenerator;
import com.google.firebase.database.DatabaseReference;

public class FirebaseDBCategories
{
    public static DatabaseReference getCatByID(String CatID)
    {
        return FirebaseBaseModel.getRef().child("Categories").child(CatID);
    }

    public static DatabaseReference getAllCat()
    {
        return FirebaseBaseModel.getRef().child("Categories");
    }

    public static void changeCatName(String catId, String newname)
    {
        Category c = new Category(catId, newname);
        FirebaseBaseModel.getRef().child("Categories").child(catId).setValue(c);
    }

    public static void addCat(String cat)
    {
        String id = idGenerator.tokenGenerator();
        Category c = new Category(id, cat);
        FirebaseBaseModel.getRef().child("Categories").child(id).setValue(c);
    }
}
