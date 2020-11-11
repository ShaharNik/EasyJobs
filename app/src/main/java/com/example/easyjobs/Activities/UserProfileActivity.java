package com.example.easyjobs.Activities;

import android.os.Bundle;
import android.text.NoCopySpan;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDBUsers FDBU;

    private TextView fname;
    private TextView lname;
    private TextView email;
    private TextView phone;
    private TextView rating;
    private TextView ratingAmount;
    private TextView isPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String nickname = user.getDisplayName();
        String email = user.getEmail();
        String phoneNum = user.getPhoneNumber();

        findViews(); // Find all TextViews By ID

        FDBU = new FirebaseDBUsers();
        DatabaseReference DR = FDBU.getUserByID(getIntent().getStringExtra("user_id"));
        DR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                // HERE Update user profile
                rating.setText(rating.getText().toString() + " " + u.getRating());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void findViews()
    {
        fname = findViewById(R.id.FNamePtextView);
        lname = findViewById(R.id.LNamePtextView);
        email = findViewById(R.id.emailPtextView);
        phone = findViewById(R.id.phonePtextView);
        rating = findViewById(R.id.ratingPtextView);
        ratingAmount = findViewById(R.id.ratingPAmounttextView);
        isPremium = findViewById(R.id.isPremiumPtextView);
    }


}
