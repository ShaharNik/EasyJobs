package com.example.easyjobs.dataBase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;


public class FirebaseStorage {
    private static StorageReference mStorageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference().getRoot();
    public static void deleteProfPictures(String profUID)
    {
        StorageReference SR = mStorageRef.child("ProfPictures/").child(profUID);
        SR.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference sr : listResult.getItems())
                {
                    sr.delete();
                }
            }
        });
    }
    public static void deleteJobPictures(String jobUID)
    {
        StorageReference SR = mStorageRef.child("JobPictures/").child(jobUID);
        SR.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference sr : listResult.getItems())
                {
                    sr.delete();
                }
            }
        });
    }

    public static void deleteSpecificProfPicture(String profUID, String pictureName)
    {
        StorageReference SR = mStorageRef.child("ProfPictures/").child(profUID).child(pictureName);
        // Delete the file
        SR.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                System.err.println("התמונה הספציפית של המקצוען המבוקשת נמחקה");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }
    public static void deleteSpecificJobPicture(String jobUID, String pictureName)
    {
        StorageReference SR = mStorageRef.child("JobPictures/").child(jobUID).child(pictureName);
        // Delete the file
        SR.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                System.err.println("התמונה הספציפית של העבודה המבוקשת נמחקה");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }
}
