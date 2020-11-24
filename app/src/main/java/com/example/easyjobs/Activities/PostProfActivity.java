package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PostProfActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backBPP;

    private Button spinnerPP;
    private int catNum = 0;
    private EditText descED;
    private EditText locED;
    private EditText IdED;
    private FirebaseAuth mAuth;
    private Button postProfB;
    private MaterialDialog md;
    private ArrayList<Integer> CatChosen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prof);

        findViews();
        activateButtons();
        setUpSpinner();
        CatChosen=new ArrayList<>();
        spinnerPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                md.show();
            }
        });
    }

    private void findViews(){
        descED = findViewById(R.id.editDescPP);
        locED = findViewById(R.id.editLocPP);
        IdED = findViewById(R.id.editIdPP);
        backBPP = findViewById(R.id.back_post_prof);
        postProfB = findViewById(R.id.postProfBtn);
        spinnerPP = findViewById(R.id.pickCategoryPostProf);

    }

    private void activateButtons(){
        backBPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostProfActivity.super.onBackPressed();
            }
        });

        postProfB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postJobToDB();
            }
        });
    }
    private void setUpSpinner(){
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
                for(int i=0;i<catArray.length;i++)
                {
                    str.add(catArray[i].getCat_name());
                }
                md = new MaterialDialog.Builder(PostProfActivity.this)
                        .title("בחר קטגוריות")
                        .items(str)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                for (Integer i :
                                        which) {
                                    CatChosen.add(i);
                                }

                                return true;
                            }
                        })
                        .positiveText("אישור").build();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public void postJobToDB(){
        String desc = descED.getText().toString(); // TODO Length should be at least 3 characters
        String loc = locED.getText().toString();
        String id = IdED.getText().toString();
        if (id.length() == 9){
            try { int temp = Integer.parseInt(id); }
            catch(Exception e){ id = "000000000"; } //Maybe pop-up window to tell user to insert int?????????????
            //Check ID Algorithm?????????????????????????????????????????????????????
        }
        else {
            id = "000000000";
            //Maybe pop-up window to tell user to insert 9-digits (include sifrat bikoret)????????????????????
        }
//        List<Integer> temp = new ArrayList<Integer>();
//        temp.add(catNum);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDBProfs.addNewProf(user.getUid(), desc, CatChosen, loc);
        PostProfActivity.super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        catNum = position;//Need to make it multiple choice.
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        catNum = 0;
    }

}