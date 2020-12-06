package com.example.easyjobs.dataBase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;


public class FirebaseStorage {
    private static StorageReference mStorageRef;
    public static void deleteProfPictures(String profUID)
    {
        mStorageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference().getRoot();
        mStorageRef.child("ProfPictures/").child(profUID);
        // Delete the file
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                System.err.println("כל התמונות של המקצוע המבוקש נמחקו");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

    }
    public static void deleteJobPictures(String jobUID)
    {
        mStorageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference().getRoot();
        mStorageRef.child("JobPictures/").child(jobUID);

        // Delete the file
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                System.err.println("כל התמונות של הבקשה המבוקשת נמחקו");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

    }
    public static void deleteSpecificProfPicture(String profUID, String pictureName)
    {
        mStorageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference().getRoot();
        mStorageRef.child("ProfPictures/").child(profUID).child(pictureName);
        // Delete the file
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        mStorageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference().getRoot();
        mStorageRef.child("JobPictures/").child(jobUID).child(pictureName);
        // Delete the file
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
