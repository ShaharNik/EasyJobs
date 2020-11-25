package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.PremiumJob;
import com.example.easyjobs.Objects.PremiumProf;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.JobAdapter;
import com.example.easyjobs.adapters.profAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.RecyclerItemClickListener;
import com.example.easyjobs.utils.SizeUtils;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<PremiumProf> ProfList;
    private profAdapter ProfAdapter;

    private FirebaseAuth fa;

    private Button postProf;
    private ImageView backBLA;

    private Spinner spinnerPL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_list);

        findViews();
        activateButtonsAndViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        spinnerPL.setSelection(0); // Important - setting up new jobs and fix flickering

    }

    @Override
    protected void onPause() {
        super.onPause();
        spinnerPL.setSelection(1); //  Important - setting up new jobs and fix flickering
        recyclerView.setAdapter(null);
    }
    private void findViews(){
        backBLA = findViewById(R.id.back_prof_list);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_prof_list);
        postProf = findViewById(R.id.profList_to_PostProf);
        spinnerPL = findViewById(R.id.pickCategoryProfList);
    }

    private void activateButtonsAndViews(){
        backBLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfListActivity.super.onBackPressed();
            }
        });

        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                moveToProfProfile(position);
            }
        }));
        recyclerView.addItemDecoration(new CommonItemSpaceDecoration(16));
        //initAll();

        setUpSpinner();

        postProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fa = FirebaseAuth.getInstance();
                FirebaseUser user = fa.getCurrentUser();
                if(user!=null){
                    moveToPostProf();
                }
                else{
                    Dialog d= new Dialog(ProfListActivity.this);
                    d.setContentView(R.layout.activity_login);
                    d.setTitle("Login");
                    d.setCancelable(true);

                    ImageView iv = d.findViewById(R.id.back_login);
                    iv.setEnabled(false);
                    iv.setVisibility(View.GONE);

                    EditText ed1 = d.findViewById(R.id.emailEditText);
                    EditText ed2 = d.findViewById(R.id.editTextPassword);
                    Button log = d.findViewById(R.id.LoginButton);
                    Button reg = d.findViewById(R.id.button_login_toregister);
                    log.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String ed1s = ed1.getText().toString();
                            String ed2s = ed2.getText().toString();
                            if(!ed1s.isEmpty() && !ed2s.isEmpty()) {
                                fa.signInWithEmailAndPassword(ed1s, ed2s).addOnCompleteListener(ProfListActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = fa.getCurrentUser();
                                            Toast.makeText(ProfListActivity.this, "Hello Cruel World", Toast.LENGTH_SHORT).show();
                                            d.dismiss();
                                        }
                                        else {
                                            Toast.makeText(ProfListActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    reg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            Intent i = new Intent(ProfListActivity.this, RegisterActivity.class);
                            startActivity(i);
                        }
                    });
                    d.show();
                }
            }
        });
    }

    private void setUpSpinner(){
        //FirebaseDBCategories fb = new FirebaseDBCategories();
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> items = new ArrayList<>();
                for(DataSnapshot category : snapshot.getChildren()){
                    Category c = category.getValue(Category.class);
                    items.add(c);
                }
                Category[] catArray = new Category[items.size()];
                items.toArray(catArray);
                Arrays.sort(catArray);
                ArrayList<String> str = new ArrayList<>();
                for(int i=0;i<catArray.length;i++) {
                    str.add(catArray[i].getCat_name());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfListActivity.this, android.R.layout.simple_spinner_dropdown_item, str);
                spinnerPL.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        spinnerPL.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0){
            initAll();
        }
        else{
            initByCat(i);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    private void initAll() {
        ProfList = new ArrayList<>();
        ProfAdapter=new profAdapter(ProfListActivity.this);
        recyclerView.setAdapter(ProfAdapter);
        DatabaseReference dr = FirebaseDBProfs.getAllProfs();

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    ProfList.add(s.getValue(PremiumProf.class));
                }
                for (PremiumProf PP : ProfList) {
                    DatabaseReference dr = FirebaseDBUsers.getUserByID(PP.getUser_ID());
                    dr = dr.child("premium");
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PP.setPremium(snapshot.getValue(Boolean.class));
                            sortArray(ProfList);
                            ProfAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                ProfAdapter.setProfsFeed(ProfList);
                ProfAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initByCat(int chosenCategory){
        ProfList = new ArrayList<>();
        ProfAdapter=new profAdapter(ProfListActivity.this);
        recyclerView.setAdapter(ProfAdapter);

      //  FirebaseDBProfs dbdbj = new FirebaseDBProfs();
        DatabaseReference dr = FirebaseDBProfs.getAllProfs();

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    DataSnapshot categories = s.child("category");

                    // Passing all the Categories as an ArrayList
                    GenericTypeIndicator<ArrayList<Integer>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Integer>>() {};
                    ArrayList<Integer> categoriesList = categories.getValue(genericTypeIndicator );

                    if(categoriesList.contains(chosenCategory)){
                        ProfList.add(s.getValue(PremiumProf.class));
                    }
                    for (PremiumProf PP : ProfList) {
                        DatabaseReference dr = FirebaseDBUsers.getUserByID(PP.getUser_ID());
                        dr = dr.child("premium");
                        dr.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                PP.setPremium(snapshot.getValue(Boolean.class));
                                sortArray(ProfList);
                                ProfAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ProfList.sort(PremiumProf::compareTo);
            System.out.println(ProfList.toString());
        }
    }
    public void moveToProfProfile(int id){
        Intent i = new Intent(ProfListActivity.this, ProfProfileActivity.class);
        i.putExtra("prof_id",ProfList.get(id).getProf_ID());
        startActivity(i);
    }

    public void moveToPostProf(){
        Intent i = new Intent(ProfListActivity.this, PostProfActivity.class);
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