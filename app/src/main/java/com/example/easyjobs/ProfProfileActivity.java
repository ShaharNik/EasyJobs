package com.example.easyjobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ProfProfileActivity extends AppCompatActivity {

    private ImageView backBPP;
    TextView tw1;
    TextView tw2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_profile);

        backBPP = findViewById(R.id.back_prof_profile);
        backBPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfProfileActivity.super.onBackPressed();
            }
        });
        tw1 = findViewById(R.id.prof_twb1);
        tw2 = findViewById(R.id.prof_twb2);

        FirebaseDBProfs db = new FirebaseDBProfs();

        DatabaseReference dr = db.getProfByID(getIntent().getStringExtra("prof_id"));
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Prof j = snapshot.getValue(Prof.class);
                tw1.setText(j.desc);
                tw2.setText(j.location);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}