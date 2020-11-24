package com.example.easyjobs.dataBase;

import android.content.Context;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyjobs.Objects.Rating;
import com.example.easyjobs.Objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseDBUsers extends FirebaseBaseModel {

    public void addUserToDB(String User_ID, String firstName,String lastName,String phoneNumber, boolean isPremium) {
        //String id = idGenerator.tokenGenerator();
        User user = new User(User_ID, firstName,lastName,phoneNumber,isPremium,0, 0);
        ref.child("Users").child(User_ID).setValue(user);
    }
    public void changeUserByID(User user) {
        ref.child("Users").child(user.getUser_ID()).setValue(user);
    }

    public DatabaseReference getAllusers()
    {
        return ref.child("Users");
    }

    public DatabaseReference getUserByID(String id)
    {
        return ref.child("Users").child(id);
    }

    public void setPremiumToAUser(String id, Context context) {
        ref.child("Users").child(id).child("premium").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "payment successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setRating(String from_ID, String profID, float value, Context c, RatingBar ratingBar)
    {
        //if(ref.child("UserRatings").)
       // boolean IsChanged=true;
        DatabaseReference dr = ref.child("UserRatings").child(from_ID);
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
               r.getTo_ID().put(profID,value);
               dr.setValue(r);
               updateRatingForUser(profID,value,isChanged,oldRating,ratingBar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateRatingForUser(String profID,float value,boolean isChanged,float oldRatingAmount,RatingBar ratingBar)
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

}
