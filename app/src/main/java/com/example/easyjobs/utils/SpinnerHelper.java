package com.example.easyjobs.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.easyjobs.Activities.Jobs.JobsListActivity;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpinnerHelper {
    public static void setUpSpinnerWithAllCategories(Spinner spinner, Context c)
    {
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ArrayList<Category> items = new ArrayList<>();
                for (DataSnapshot category : snapshot.getChildren())
                {
                    Category c = category.getValue(Category.class);
                    items.add(c);
                }
                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(c, android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
