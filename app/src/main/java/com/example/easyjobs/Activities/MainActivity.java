package com.example.easyjobs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.Activities.Jobs.JobsListActivity;
import com.example.easyjobs.Activities.Profs.ProfListActivity;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
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
        logedInModifier();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        logedInModifier();
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

    private void logedInModifier()
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