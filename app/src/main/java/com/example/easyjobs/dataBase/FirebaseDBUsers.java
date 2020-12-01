package com.example.easyjobs.dataBase;

import android.content.Context;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyjobs.Objects.Rating;
import com.example.easyjobs.Objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseDBUsers {

    public static boolean isAdmin;
    public static void addUserToDB(String User_ID, String firstName,String lastName,String phoneNumber, boolean isPremium) {
        //String id = idGenerator.tokenGenerator();
        User user = new User(User_ID, firstName,lastName,phoneNumber,isPremium,0, 0);
        FirebaseBaseModel.getRef().child("Users").child(User_ID).setValue(user);
    }
    public static void changeUserByID(User user) {
        FirebaseBaseModel.getRef().child("Users").child(user.getUser_ID()).setValue(user);
    }

    public static DatabaseReference getAllusers()
    {
        return FirebaseBaseModel.getRef().child("Users");
    }

    public static DatabaseReference getUserByID(String id)
    {
        return FirebaseBaseModel.getRef().child("Users").child(id);
    }

    public static void setPremiumToAUser(String id, Context context) {
        FirebaseBaseModel.getRef().child("Users").child(id).child("premium").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "payment successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void setRating(String from_ID, String profID, float value, Context c, RatingBar ratingBar)
    {
        //if(ref.child("UserRatings").)
       // boolean IsChanged=true;
        DatabaseReference dr = FirebaseBaseModel.getRef().child("UserRatings").child(from_ID);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Rating r = snapshot.getValue(Rating.class);
                boolean isChanged=true;
                if(r == null)
                {
                    isChanged=false;
                    r = new Rating();
                    r.setUserId_from(from_ID);
                }
               if(r.getTo_ID()== null)
               {
                   isChanged=false;
                   r.setTo_ID(new HashMap<>());

               }
               float oldRating=0f;
               if(r.getTo_ID().get(profID) != null)
               {
                   isChanged=true;
                   oldRating=r.getTo_ID().get(profID);
               }
               else
               {
                   isChanged=false;
               }
               r.getTo_ID().put(profID,value);
               dr.setValue(r);
               updateRatingForUser(profID,value,isChanged,oldRating,ratingBar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void updateRatingForUser(String profID,float value,boolean isChanged,float oldRatingAmount,RatingBar ratingBar)
    {
        DatabaseReference dr = getUserByID(profID);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                float oldRating = u.getRating()*u.getRatingsAmount();
                if(!isChanged)
                {
                    oldRating+=value;
                    u.setRatingsAmount(u.getRatingsAmount()+1);
                    u.setRating(oldRating/u.getRatingsAmount());
                }
                else
                {
                    oldRating-=oldRatingAmount;
                    oldRating+=value;
                    u.setRating(oldRating/u.getRatingsAmount());
//                    u.setRatingsAmount(u.getRatingsAmount() );
//                    u.setRating(value/u.getRatingsAmount());
                }
                dr.setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ratingBar.setEnabled(true);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void makeNewAdmin(String Uid)
    {
        FirebaseBaseModel.getRef().child("Admins").child(Uid).setValue(true);
    }

    public static DatabaseReference CheckAdmin()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseBaseModel.getRef().child("Admins").child(uid);
    }

}
