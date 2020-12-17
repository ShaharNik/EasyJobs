package com.example.easyjobs.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.easyjobs.Activities.Jobs.JobProfileActivity;
import com.example.easyjobs.Activities.Jobs.JobsListActivity;
import com.example.easyjobs.Activities.Profs.ProfListActivity;
import com.example.easyjobs.Activities.Profs.ProfProfileActivity;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseBaseModel;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String CHANNEL_ID = "75";
    private TextView welcomeText;
    private Button jobListB;
    private Button proListB;
    private Button loginT;
    private ImageView logo;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        activateButtons();

    }
    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "תוכן פוגעני", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("פרסום התראה פוגענית");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void callNotification(String key, Boolean isJobBool)
    {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        // Create PendingIntent to take us to DetailsActivity
        // as a result of notification action
        //Intent detailsIntent = new Intent(MainActivity.this, UserProfileActivity.class);
        Intent detailsIntent;
        if (isJobBool)
        {
            detailsIntent = new Intent(MainActivity.this, JobProfileActivity.class);
            detailsIntent.putExtra("job_id", key);
        }
        else
        {
            detailsIntent = new Intent(MainActivity.this, ProfProfileActivity.class);
            detailsIntent.putExtra("prof_id",key);
        }
        PendingIntent detailsPendingIntent = PendingIntent.getActivity(
                MainActivity.this,
                0,
                detailsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        Random rnd = new Random();
        int notificationId = rnd.nextInt(1000);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("מודעה חשודה התפרסמה!")
                .setContentText("זוהתה מודעה בעלת תוכן חשוד, לחץ לבדיקה")
                .setContentIntent(detailsPendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, mBuilder.build());
    }
    private void suspiciousPostsChecker()
    {
        DatabaseReference DR = FirebaseBaseModel.getRef().child("suspiciousPosts");
        DR.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.err.println("Reached Added");
                Boolean JobBool = snapshot.getValue(boolean.class);
                callNotification(snapshot.getKey(), JobBool);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                System.err.println("Reached Removed");
                //callNotification(snapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        DR.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                System.err.println("Reached Here");
//                for (DataSnapshot dr : snapshot.getChildren())
//                {
//                    System.err.println("Reached FOR");
//                    callNotification(dr.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        loggedInModifier();
    }

    private void findViews()
    {
        logo = findViewById(R.id.imageView);
        welcomeText = findViewById(R.id.WelcomeText);
        jobListB = findViewById(R.id.button_mainto_joblist);
        proListB = findViewById(R.id.button_mainto_proflist);
        loginT = findViewById(R.id.button_mainto_login);
    }

    private void activateButtons()
    {
        setLogoSize();
        loginT.setOnClickListener(this);
        jobListB.setOnClickListener(this);
        proListB.setOnClickListener(this);
    }

    @Override
    public void onClick(View ClickedButton)
    {
        if (loginT.equals(ClickedButton)){
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null)
            {
                // not logged in
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
            else
            {
                Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
                i.putExtra("user_id", user.getUid());
                startActivity(i);
            }
        }
        else if (jobListB.equals(ClickedButton)){
            Intent i = new Intent(MainActivity.this, JobsListActivity.class);
            i.putExtra("personal", false);
            startActivity(i);
        }
        else if (proListB.equals(ClickedButton)){
            Intent i = new Intent(MainActivity.this, ProfListActivity.class);
            i.putExtra("personal", false);
            startActivity(i);
        }
    }

    private void setLogoSize()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        int imgWidth = (int) (screenWidth * 1);
        int imgHeight = (int) (screenHeight * 0.45);

        logo.getLayoutParams().height = imgHeight;
        logo.getLayoutParams().width = imgWidth;
    }

    private void loggedInModifier()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
        {
            // user is logged in
            String disp_name = user.getDisplayName();
            welcomeText.setText("שלום, " + disp_name + " :)");
            loginT.setText("פרופיל");
            DatabaseReference dr = FirebaseDBUsers.CheckAdmin();
            dr.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (snapshot.exists())
                    {
                        if (!FirebaseDBUsers.isAdmin) { // only the first time he isn't admin..
                            createNotificationChannel();
                            suspiciousPostsChecker();
                        }
                        FirebaseDBUsers.isAdmin = true;

                        System.out.println("ADMIN OKAY");
                    }
                    else
                    {
                        FirebaseDBUsers.isAdmin = false;
                        System.out.println("NOT ADMIN OKAY");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
        else
        {
            welcomeText.setText("שלום, אורח :)");
            loginT.setText("התחבר/הירשם");
        }
    }
}