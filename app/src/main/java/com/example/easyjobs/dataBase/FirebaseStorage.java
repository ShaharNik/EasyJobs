package com.example.easyjobs.dataBase;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorage
{
    private static final StorageReference mStorageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference().getRoot();

    public static void deleteProfPictures(String profUID)
    {
        StorageReference SR = mStorageRef.child("ProfPictures/").child(profUID);
        SR.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>()
        {
            @Override
            public void onSuccess(ListResult listResult)
            {
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
        SR.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>()
        {
            @Override
            public void onSuccess(ListResult listResult)
            {
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
        SR.delete();
    }

    public static void deleteSpecificJobPicture(String jobUID, String pictureName)
    {
        StorageReference SR = mStorageRef.child("JobPictures/").child(jobUID).child(pictureName);
        // Delete the file
        SR.delete();
    }
}
