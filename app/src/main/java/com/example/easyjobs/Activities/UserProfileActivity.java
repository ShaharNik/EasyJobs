package com.example.easyjobs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private ImageView backButt;

    private TextView fname;
    private TextView lname;
    private TextView email;
    private TextView phone;
    private TextView rating;
    private TextView ratingAmount;
    private TextView isPremium;

    private Button LogoutButt;
    private Button UpgradeToPremium;
    private Button EditProfile;
    private Button profileEditButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        findViews(); // Find all TextViews By ID
        activateButtonsAndViews();
    }

    private void findViews(){
        // Image Views
        backButt = findViewById(R.id.back_from_user_profile);
        // Texts
        fname = findViewById(R.id.FNamePtextView);
        lname = findViewById(R.id.LNamePtextView);
        email = findViewById(R.id.emailPtextView);
        phone = findViewById(R.id.phonePtextView);
        rating = findViewById(R.id.ratingPtextView);
        ratingAmount = findViewById(R.id.ratingPAmounttextView);
        isPremium = findViewById(R.id.isPremiumPtextView);
        // Buttons
        LogoutButt = findViewById(R.id.logoutButt);
        UpgradeToPremium = findViewById(R.id.premiumButt);
        EditProfile = findViewById(R.id.profileEditButt);
        profileEditButt = findViewById(R.id.profileEditButt);
    }

    private void activateButtonsAndViews(){
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.super.onBackPressed();
            }
        });

        LogoutButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                registerB.setEnabled(true);
                registerB.setVisibility(View.VISIBLE);
                emailED.setEnabled(true);
                pass.setEnabled(true);
                LoginB.setText("Login");

                 */
                FirebaseAuth.getInstance().signOut();
                UserProfileActivity.super.onBackPressed();
            }
        });
        profileEditButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToEditProfile_activity();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String nickname = user.getDisplayName();
        String user_email = user.getEmail();

        FDBU = new FirebaseDBUsers();
        DatabaseReference DR = FDBU.getUserByID(getIntent().getStringExtra("user_id"));
        DR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                fname.setText(fname.getText().toString() + " " + u.getFirstName());
                lname.setText(lname.getText().toString() + " " + u.getLastName());
                // HERE Update user profile
                rating.setText(rating.getText().toString() + " " + u.getRating());
                ratingAmount.setText(" (" + u.getRatingsAmount() + ")");

                if (u.isPremium() == false){
                    isPremium.setText(isPremium.getText().toString() + " לא");
                }
                else{
                    isPremium.setText(isPremium.getText().toString() + " כן");
                }

                email.setText(email.getText().toString() + " " + user_email);
                phone.setText(phone.getText().toString() + " " + u.getPhoneNumber());
                System.err.println(user.getDisplayName() + " " + user.getEmail() + " " + user.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        UpgradeToPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToPremiumPaymentActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            super.onBackPressed();
        }
    }

    private void moveToPremiumPaymentActivity(){
        Intent i = new Intent(UserProfileActivity.this, PremiumPaymentActivity.class);
        startActivity(i);
    }
    private void moveToEditProfile_activity(){
        Intent i = new Intent(UserProfileActivity.this, EditProfileActivity.class);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        i.putExtra("user_id", user.getUid());
        startActivity(i);
    }
}
