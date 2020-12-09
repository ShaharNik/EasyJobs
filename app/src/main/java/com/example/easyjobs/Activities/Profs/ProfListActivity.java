package com.example.easyjobs.Activities.Profs;

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
import com.example.easyjobs.Objects.PremiumProf;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.profAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<PremiumProf> ProfList;
    private profAdapter ProfAdapter;
    private FirebaseAuth fa;
    private Button postProf;
    private ImageView backBLA;
    private boolean personal;
    private Dialog loginDialog;
    private Spinner spinnerPL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_list);

        getExtra();
        findViews();
        setupRecyclerView();
        buildLogInDialog();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        activateButtonsAndViews(); // Important - setting up new jobs and fix flickering
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        spinnerPL.setSelection(1); //  Important - setting up new jobs and fix flickering
        recyclerView.setAdapter(null);
    }

    private void getExtra()
    {
        personal = getIntent().getBooleanExtra("personal", false);
    }
    private void findViews()
    {
        backBLA = findViewById(R.id.back_prof_list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_prof_list);
        postProf = findViewById(R.id.profList_to_PostProf);
        spinnerPL = findViewById(R.id.pickCategoryProfList);
    }

    private void setupRecyclerView()
    {
        backBLA.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new CommonItemSpaceDecoration(16));
        postProf.setOnClickListener(this);
    }

    private void buildLogInDialog()
    {
        loginDialog = dialogHelper.loginDialogBuilder(this);
    }
    private void openDialog()
    {
        loginDialog.show();
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
                    moveToProfProfile(position);
                }
                else
                {
                    openDialog();
                }
            }
        }));
        setUpSpinner();
    }

    private void setUpSpinner()
    {
        SpinnerHelper.setUpSpinnerWithAllCategories(spinnerPL,this);
        spinnerPL.setOnItemSelectedListener(this);
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
    public void onNothingSelected(AdapterView<?> adapterView) {}

    private void initAll()
    {
        ProfList = new ArrayList<>();
        ProfAdapter = new profAdapter(ProfListActivity.this);
        recyclerView.setAdapter(ProfAdapter);
        DatabaseReference dr = FirebaseDBProfs.getAllProfs();

        dr.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot s : snapshot.getChildren())
                {
                    PremiumProf pp = s.getValue(PremiumProf.class);
                    if (!personal)
                    {
                        ProfList.add(pp);
                    }
                    else
                    {
                        if (pp.getUser_ID().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) == 0)
                        {
                            ProfList.add(pp);
                        }
                    }
                }
                for (PremiumProf PP : ProfList)
                {
                    DatabaseReference dr = FirebaseDBUsers.getUserByID(PP.getUser_ID());
                    dr = dr.child("premium");
                    dr.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            PP.setPremium(snapshot.getValue(Boolean.class));
                            sortArray(ProfList);
                            ProfAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error){}
                    });
                }
                ProfAdapter.setProfsFeed(ProfList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initByCat(int chosenCategory)
    {
        ProfList = new ArrayList<>();
        ProfAdapter = new profAdapter(ProfListActivity.this);
        recyclerView.setAdapter(ProfAdapter);
        Category x = (Category) spinnerPL.getAdapter().getItem(chosenCategory);

        DatabaseReference dr = FirebaseDBProfs.getAllProfs();
        dr.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot s : snapshot.getChildren())
                {
                    DataSnapshot categories = s.child("category");
                    // Passing all the Categories as an ArrayList
                    GenericTypeIndicator<ArrayList<String>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<String>>(){};
                    ArrayList<String> categoriesList = categories.getValue(genericTypeIndicator);
                    if (categoriesList.contains(x.getCategory_id()))
                    {
                        PremiumProf pp = s.getValue(PremiumProf.class);
                        if (!personal)
                        {
                            ProfList.add(pp);
                        }
                        else
                        {
                            if (pp.getUser_ID().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) == 0)
                            {
                                ProfList.add(pp);
                            }
                        }
                    }
                    for (PremiumProf PP : ProfList)
                    {
                        DatabaseReference dr = FirebaseDBUsers.getUserByID(PP.getUser_ID());
                        dr = dr.child("premium");
                        dr.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                PP.setPremium(snapshot.getValue(Boolean.class));
                                sortArray(ProfList);
                                ProfAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error){}
                        });
                    }
                }
                ProfAdapter.setProfsFeed(ProfList);
                ProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void sortArray(List<PremiumProf> ProfList)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            ProfList.sort(PremiumProf::compareTo);
            System.out.println(ProfList.toString());
        }
    }

    public void moveToProfProfile(int id)
    {
        Intent i = new Intent(ProfListActivity.this, ProfProfileActivity.class);
        i.putExtra("prof_id", ProfList.get(id).getProf_ID());
        startActivity(i);
    }

    public void moveToPostProf()
    {
        Intent i = new Intent(ProfListActivity.this, PostProfActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View ClickedButton) {
        if(backBLA.equals(ClickedButton))
        {
            ProfListActivity.super.onBackPressed();
        }
        else if(postProf.equals(ClickedButton))
        {
            fa = FirebaseAuth.getInstance();
            FirebaseUser user = fa.getCurrentUser();
            if (user != null)
            {
                moveToPostProf();
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