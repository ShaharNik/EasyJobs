package com.example.easyjobs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Job> JobList;
    private JobAdapter JobAdapter;
    private Button jobProfile;
    private Button postJob;
    private ImageView backBJL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_list);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        jobProfile = findViewById(R.id.jobList_to_JobProfile);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                      // System.err.println(position);
                        moveToJobProfile(position);
                    }
                }));
        recyclerView.addItemDecoration(new CommonItemSpaceDecoration(16));
        backBJL = findViewById(R.id.back_jobs_list);
        init();
        backBJL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobsListActivity.super.onBackPressed();
            }
        });




        postJob = findViewById(R.id.jobList_to_PostJob);
        postJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPostJob();
            }
        });
    }
    private void init()
    {
        JobList = new ArrayList<>();
        JobAdapter=new JobAdapter(JobsListActivity.this);
        recyclerView.setAdapter(JobAdapter);

        FirebaseDBJobs dbdbj = new FirebaseDBJobs();
        DatabaseReference dr = dbdbj.getAllJobs();

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s :
                        snapshot.getChildren()) {
                    JobList.add(s.getValue(Job.class));
                }
                JobAdapter.setJobsFeed(JobList);
                JobAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void moveToJobProfile(int Position){
        Intent i = new Intent(JobsListActivity.this, JobProfileActivity.class);
        i.putExtra("job_id",JobList.get(Position).job_ID);
        startActivity(i);
    }
    public void moveToPostJob(){
        Intent i = new Intent(JobsListActivity.this, PostJobActivity.class);
        startActivity(i);
    }





    public class CommonItemSpaceDecoration extends RecyclerView.ItemDecoration {

        private int mSpace = 0;
        private boolean mVerticalOrientation = true;

        public CommonItemSpaceDecoration(int space) {
            this.mSpace = space;
        }

        public CommonItemSpaceDecoration(int space, boolean verticalOrientation) {
            this.mSpace = space;
            this.mVerticalOrientation = verticalOrientation;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = SizeUtils.dp2px(view.getContext(), mSpace);
            if (mVerticalOrientation) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.set(0, SizeUtils.dp2px(view.getContext(), mSpace), 0, SizeUtils.dp2px(view.getContext(), mSpace));
                } else {
                    outRect.set(0, 0, 0, SizeUtils.dp2px(view.getContext(), mSpace));
                }
            } else {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.set(SizeUtils.dp2px(view.getContext(), mSpace), 0, 0, 0);
                } else {
                    outRect.set(SizeUtils.dp2px(view.getContext(), mSpace), 0, SizeUtils.dp2px(view.getContext(), mSpace), 0);
                }
            }
        }
    }




}