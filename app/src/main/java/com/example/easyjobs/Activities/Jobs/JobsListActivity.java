package com.example.easyjobs.Activities.Jobs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyjobs.Activities.RegisterActivity;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.PremiumJob;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.JobAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.RecyclerItemClickListener;
import com.example.easyjobs.utils.SizeUtils;
import com.example.easyjobs.utils.SpinnerHelper;
import com.example.easyjobs.utils.dialogHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobsListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<PremiumJob> JobList;
    private JobAdapter JobAdapter;
    private FirebaseAuth fa;
    private FirebaseUser user;
    private Button postJob;
    private ImageView backBJL;
    private boolean personal;
    private Dialog loginDialog;
    private Spinner spinnerJL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_list);

        getExtra();
        findViews();
        setupRecyclerView();
        buildLogInDialog();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        activateButtonsAndViews();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        spinnerJL.setSelection(1); //  Important - setting up new jobs and fix flickering
        recyclerView.setAdapter(null);
    }

    private void getExtra()
    {
        personal = getIntent().getBooleanExtra("personal", false);
    }
    private void findViews()
    {
        backBJL = findViewById(R.id.back_jobs_list);
        postJob = findViewById(R.id.jobList_to_PostJob);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        spinnerJL = findViewById(R.id.pickCategoryJobList);
    }

    private void setupRecyclerView()
    {
        backBJL.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new CommonItemSpaceDecoration(16));
        postJob.setOnClickListener(this);
    }

    private void activateButtonsAndViews()
    {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                fa = FirebaseAuth.getInstance();
                FirebaseUser user = fa.getCurrentUser();
                if (user != null)
                {
                    moveToJobProfile(position);
                }
                else
                {
                    openDialog();
                }
            }
        }));
        setUpSpinner();
    }

    private void buildLogInDialog()
    {
        loginDialog = dialogHelper.loginDialogBuilder(this);
    }
    private void openDialog()
    {
        loginDialog.show();
    }

    private void setUpSpinner()
    {
        SpinnerHelper.setUpSpinnerWithAllCategories(spinnerJL,this);
        spinnerJL.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        if (i == 0)
        {
            initAll();
        }
        else
        {
            initByCat(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){}

    private void initAll()
    {
        JobList = new ArrayList<>();
        JobAdapter = new JobAdapter(JobsListActivity.this);
        recyclerView.setAdapter(JobAdapter);

        DatabaseReference dr = FirebaseDBJobs.getAllJobs();
        dr.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot s : snapshot.getChildren())
                {
                    PremiumJob pj = s.getValue(PremiumJob.class);
                    if (pj.getEndDate().after(Calendar.getInstance().getTime()))
                    {
                        if (!personal)
                        {
                            JobList.add(pj);
                        }
                        else
                        {
                            if (pj.getUser_ID().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) == 0)
                            {
                                JobList.add(pj);
                            }
                        }
                    }
                }
                for (PremiumJob pj : JobList)
                {
                    DatabaseReference dr = FirebaseDBUsers.getUserByID(pj.getUser_ID());
                    dr = dr.child("premium");
                    dr.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            pj.setPremium(snapshot.getValue(Boolean.class));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            {
                                JobList.sort(PremiumJob::compareTo);
                            }
                            JobAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error){}
                    });
                }
                JobAdapter.setJobsFeed(JobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initByCat(int chosenCategory)
    {
        JobList = new ArrayList<>();
        JobAdapter = new JobAdapter(JobsListActivity.this);
        recyclerView.setAdapter(JobAdapter);
        Category x = (Category) spinnerJL.getAdapter().getItem(chosenCategory);
        DatabaseReference dr = FirebaseDBJobs.getAllJobs();

        dr.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot s : snapshot.getChildren())
                {
                    s.child("category_ID").getValue();
                    if (((String) s.child("category_ID").getValue()).compareTo(x.getCategory_id()) == 0)
                    {
                        PremiumJob pj = s.getValue(PremiumJob.class);
                        if (pj.getEndDate().after(Calendar.getInstance().getTime()))
                        {
                            if (!personal)
                            {
                                JobList.add(pj);
                            }
                            else
                            {
                                if (pj.getUser_ID().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) == 0)
                                {
                                    JobList.add(pj);
                                }
                            }
                        }
                    }
                }
                for (PremiumJob pj : JobList)
                {
                    DatabaseReference dr = FirebaseDBUsers.getUserByID(pj.getUser_ID());
                    dr = dr.child("premium");
                    dr.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            pj.setPremium(snapshot.getValue(Boolean.class));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            {
                                JobList.sort(PremiumJob::compareTo);
                            }
                            JobAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error){}
                    });
                }
                JobAdapter.setJobsFeed(JobList);
                JobAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void moveToJobProfile(int Position)
    {
        Intent i = new Intent(JobsListActivity.this, JobProfileActivity.class);
        i.putExtra("job_id", JobList.get(Position).getJob_ID());
        startActivity(i);
    }

    public void moveToPostJob()
    {
        Intent i = new Intent(JobsListActivity.this, PostJobActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View Clickable) {
        if(backBJL.equals(Clickable))
        {
            JobsListActivity.super.onBackPressed();
        }
        if(postJob.equals(Clickable))
        {
            fa = FirebaseAuth.getInstance();
            user = fa.getCurrentUser();
            if (user != null)
            {
                moveToPostJob();
            }
            else
            {
                openDialog();
            }
        }
    }

    public class CommonItemSpaceDecoration extends RecyclerView.ItemDecoration
    {
        private int mSpace = 0;
        private boolean mVerticalOrientation = true;

        public CommonItemSpaceDecoration(int space)
        {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            outRect.top = SizeUtils.dp2px(view.getContext(), mSpace);
            if (mVerticalOrientation)
            {
                if (parent.getChildAdapterPosition(view) == 0)
                {
                    outRect.set(0, SizeUtils.dp2px(view.getContext(), mSpace), 0, SizeUtils.dp2px(view.getContext(), mSpace));
                }
                else
                {
                    outRect.set(0, 0, 0, SizeUtils.dp2px(view.getContext(), mSpace));
                }
            }
            else
            {
                if (parent.getChildAdapterPosition(view) == 0)
                {
                    outRect.set(SizeUtils.dp2px(view.getContext(), mSpace), 0, 0, 0);
                }
                else
                {
                    outRect.set(SizeUtils.dp2px(view.getContext(), mSpace), 0, SizeUtils.dp2px(view.getContext(), mSpace), 0);
                }
            }
        }
    }
}