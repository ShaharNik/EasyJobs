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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.utils.Validator;
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


public class PostProfActivity extends AppCompatActivity {

    private ImageView backBPP;

    private Button spinnerPP;
    private EditText descED;
    private EditText locED;
    private EditText IdED;
    private FirebaseAuth mAuth;
    private Button postProfB;
    private MaterialDialog md;
    private ArrayList<String> CatChosen;
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
//                Category[] catArray = new Category[items.size()];
//                items.toArray(catArray);
//                Arrays.sort(catArray);
//                ArrayList<String> str = new ArrayList<>();
//                for(int i=0;i<catArray.length;i++)
//                {
//                    str.add(catArray[i].getCat_name());
//                }
                md = new MaterialDialog.Builder(PostProfActivity.this)
                        .title("בחר קטגוריות")
                        .items(items)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                for (Integer i :
                                        which) {
                                    CatChosen.add(items.get(i).getCategory_id());
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
        String loc = locED.getText().toString(); // at least 3
        String id = IdED.getText().toString(); // 9 digits
        if (Validator.ValidateIsraeliId(id)){
            if (Validator.ValidateDescription(desc))
            {
                if (Validator.ValidateLocation(loc))
                {
                    // --=== Add new Prof and move user to back activity ==---
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    FirebaseDBProfs.addNewProf(user.getUid(), desc, CatChosen, loc);
                    Toast.makeText(PostProfActivity.this, "התפרסמת בהצלחה", Toast.LENGTH_SHORT).show();
                    PostProfActivity.super.onBackPressed();
                }
                else
                {
                    locED.setError("המיקום אינו תקין, חייב להיות באורך 3 לפחות");
                }
            }
            else
                descED.setError("התיאור קצר מידי");
        }
        else {
            IdED.setError("ת.ז שהזנת אינה תקינה");
        }

    }


}