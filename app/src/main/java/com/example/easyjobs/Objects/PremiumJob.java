package com.example.easyjobs.Objects;

import androidx.annotation.NonNull;

import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PremiumJob extends Job implements Comparable<PremiumJob>{
    private boolean isPremium;

    public PremiumJob() {
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    @Override
    public int compareTo(PremiumJob o) {
        if(o.isPremium == isPremium)
        {
            return this.startDate.compareTo(o.startDate);
        }
        return isPremium ? -1 : 1;

    }

}
