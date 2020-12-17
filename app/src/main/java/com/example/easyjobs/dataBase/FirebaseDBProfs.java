package com.example.easyjobs.dataBase;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyjobs.Activities.Profs.AdminEditProfActivity;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.utils.idGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDBProfs
{
    public static void addNewProf(String user_id, String desc, List<String> cats, String loc, ArrayList<Uri> picsUri)
    {
        String id = idGenerator.tokenGenerator();
        Prof profToAdd = new Prof(id, user_id, desc, cats, loc);
        FirebaseBaseModel.getRef().child("Profs").child(id).setValue(profToAdd);
        DatabaseReference DR = FirebaseBaseModel.getRef().child("badWords");
        DR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (desc.contains(ds.getKey()))
                    {
                        //System.err.println(ds.getKey() + " 555");
                        FirebaseBaseModel.getRef().child("suspiciousPosts").child(id).setValue(false); // false = prof, true = jobs
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        for (Uri u : picsUri)
        {
            StorageReference storageReference = mStorageRef.child("ProfPictures/" + id + "/" + u.getLastPathSegment());
            storageReference.putFile(u).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    System.out.println("GOOD !");
                }
            });
        }
    }

    public static DatabaseReference getProfByID(String ProfID)
    {
        return FirebaseBaseModel.getRef().child("Profs").child(ProfID);
    }

    public static DatabaseReference getAllProfs()
    {
        return FirebaseBaseModel.getRef().child("Profs");
    }

    public static void EditProf(String prof_id, String user_id, String desc, List<String> cats, String loc, AdminEditProfActivity c, ArrayList<Uri> PicsUri)
    {
        Prof p = new Prof(prof_id, user_id, desc, cats, loc);
        FirebaseBaseModel.getRef().child("Profs").child(prof_id).setValue(p).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
                for (Uri u : PicsUri)
                {
                    StorageReference storageReference = mStorageRef.child("ProfPictures/" + p.getProf_ID() + "/" + u.getLastPathSegment());
                    storageReference.putFile(u).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                        {
                            System.out.println("GOOD !");
                        }
                    });
                }
                //check for Offensive words to know id to add to suspicious
                DatabaseReference DR = FirebaseBaseModel.getRef().child("badWords");
                DR.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        boolean changed = false;
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            if (desc.contains(ds.getKey()))
                            {
                                changed = true;
                                FirebaseBaseModel.getRef().child("suspiciousPosts").child(prof_id).setValue(false); // false = prof, true = jobs
                                System.out.println(" Added !");
                            }
                        }
                        if (!changed)
                        {
                            RemoveProfFromSuspicious(prof_id);
                            System.out.println("Removed !");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(c, "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                c.onBackPressed();
            }
        });
    }

    public static void removeProf(String profID, AdminEditProfActivity c)
    {
        FirebaseBaseModel.getRef().child("Profs").child(profID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                c.finish();
            }
        });
        //TODO if he exist in suspicious remove him too
        RemoveProfFromSuspicious(profID);

    }
    public static void RemoveProfFromSuspicious(String profID)
    {
        FirebaseBaseModel.getRef().child("suspiciousPosts").child(profID).removeValue();
    }
}
