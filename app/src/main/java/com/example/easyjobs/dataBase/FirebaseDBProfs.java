package com.example.easyjobs.dataBase;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyjobs.Activities.Profs.AdminEditPostActivity;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.utils.idGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class FirebaseDBProfs {

    public static void addNewProf(String user_id, String desc, List<String> cats, String loc){
        String id = idGenerator.tokenGenerator();
        Prof p = new Prof(id,user_id, desc, cats, loc);
        FirebaseBaseModel.getRef().child("Profs").child(id).setValue(p);
    }

    public static DatabaseReference getProfByID(String ProfID){
        return FirebaseBaseModel.getRef().child("Profs").child(ProfID);
    }

    public static  DatabaseReference getAllProfs(){
        return FirebaseBaseModel.getRef().child("Profs");

    }
    public static void EditProf(String prof_id, String user_id, String desc, List<String> cats, String loc, AdminEditPostActivity c)
    {
        Prof p = new Prof(prof_id,user_id, desc, cats, loc);
        FirebaseBaseModel.getRef().child("Profs").child(prof_id).setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(c, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                c.onBackPressed();

            }
        });
    }
    public static void removeProf(String profID,AdminEditPostActivity c)
    {
        FirebaseBaseModel.getRef().child("Profs").child(profID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                c.finish();
            }
        });
    }


}
