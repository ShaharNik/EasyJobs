package com.example.easyjobs.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.Activities.Jobs.JobsListActivity;
import com.example.easyjobs.Activities.Profs.ProfListActivity;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Console;
import java.io.File;
import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
   // private FirebaseDBUsers FDBU;

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
    private Button AdminsButt;
    private File localFile;
    private ImageView myImage;
    private StorageReference mStorageRef;
    private Button profileShowJobs;
    private Button profileShowProfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        findViews(); // Find all TextViews By ID

        if (FirebaseDBUsers.isAdmin == false)
            AdminsButt.setVisibility(View.GONE);
        activateButtonsAndViews();
        setProfilePicture();
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
        AdminsButt = findViewById(R.id.MoveToAdminButt);
        myImage = (ImageView) findViewById(R.id.ProfilePic);
        profileShowJobs = findViewById(R.id.profileShowJobs);
        profileShowProfs = findViewById(R.id.profileShowProfs);
    }

    private void activateButtonsAndViews(){
        profileShowJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToJobList();
            }
        });

        profileShowProfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToProfList();
            }
        });

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

      //  FDBU = new FirebaseDBUsers();
        DatabaseReference DR = FirebaseDBUsers.getUserByID(getIntent().getStringExtra("user_id"));
        DR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                fname.setText("שם פרטי: " + u.getFirstName());
                lname.setText("שם משפחה: " + u.getLastName());
                // HERE Update user profile
                rating.setText( "דירוג: "+ u.getRating());
                ratingAmount.setText(" (" + u.getRatingsAmount() + ")");

                if (u.isPremium() == false){
                    isPremium.setText("משתמש פרימיום: לא");
                }
                else{
                    isPremium.setText("משתמש פרימיום: כן");
                    UpgradeToPremium.setEnabled(false);
                    UpgradeToPremium.setBackgroundColor(Color.GRAY);
                }

                email.setText("אימייל: " + user_email);
                phone.setText("מספר טלפון: " + u.getPhoneNumber());
                //System.err.println(user.getDisplayName() + " " + user.getEmail() + " " + user.getPhoneNumber());
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
        AdminsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAdminActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            UserProfileActivity.super.onBackPressed();
        }
        else {
            activateButtonsAndViews();
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
    private void moveToAdminActivity() {
        Intent i = new Intent(UserProfileActivity.this, AdminActivity.class);
        startActivity(i);
    }
    private void uploadProfilePicture()
    {

    }

    private void setProfilePicture()
    {
        StorageReference riversRef = mStorageRef.child("UserProfilePicture/"+mAuth.getCurrentUser().getUid()+"/ProfilePic.jpg");
        localFile = null;
        try {
            localFile = File.createTempFile(mAuth.getCurrentUser().getUid(), "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
      //  mAuth.password
       // mAuth.confirmPasswordReset()
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            DisplayImage();
                        // Successfully downloaded data to local file
//                        riversRef.get

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }
    private void DisplayImage()
    {
        System.out.println("PIRCTURE");
        if(localFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            myImage.setMaxHeight(5);
            myImage.setMaxWidth(5);
            myImage.setImageBitmap(myBitmap);

        }
    }
    private void moveToJobList()
    {
        Intent i = new Intent(UserProfileActivity.this, JobsListActivity.class);
        i.putExtra("personal",true);
        startActivity(i);
    }

    private void moveToProfList()
    {
        Intent i = new Intent(UserProfileActivity.this, ProfListActivity.class);
        i.putExtra("personal",true);
        startActivity(i);
    }



}

