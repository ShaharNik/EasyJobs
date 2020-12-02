package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminEditPostActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView namesText;
    private TextView rateText;
    private EditText descText;
    private EditText catText;
    private Button spinner;
    private EditText locText;
    private TextView phoneText;
    private Button approveChanges;
    private Button deletePost;
    private MaterialDialog md;

    private User user;
    private Prof prof;
    List<String> catList;

    private boolean changedIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_post);

        user = (User) getIntent().getSerializableExtra("User");
        prof = (Prof) getIntent().getSerializableExtra("Prof");
        catList = new ArrayList<String>();

        findViews();
        inputTempData();
        setUpSpinner();
        activateButtons();
    }

    private void findViews() {
        backButton = findViewById(R.id.back_admin_editPost);
        namesText = findViewById(R.id.namesEP);
        rateText = findViewById(R.id.ratingEP);
        descText = findViewById(R.id.descriptionEP);
        catText = findViewById(R.id.catEP);
        spinner = findViewById(R.id.pickCategoryEditProf);
        locText = findViewById(R.id.locationEP);
        phoneText = findViewById(R.id.phoneEP);
        approveChanges = findViewById(R.id.changeProfDetails);
        deletePost = findViewById(R.id.deleteProfButton);
    }

    private void inputTempData() {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        rateText.setText(user.getRating()+ " ("+ user.getRatingsAmount()+")");
        descText.setText(prof.getDesc());
        locText.setText(prof.getLocation());
        phoneText.setText(user.getPhoneNumber());
    }

    private void setUpSpinner(){
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> items = new ArrayList<>();
                ArrayList<Integer> itemIndexs = new ArrayList<Integer>();

                int index=0;
                for(DataSnapshot category : snapshot.getChildren()){
                    Category c = category.getValue(Category.class);
                    if(prof.getCategory().contains(c.getCategory_id())) {
                        catList.add(c.getCategory_id());
                        catText.setText(catText.getText().toString() + c.getCat_name()+" ");
                        itemIndexs.add(index);
                    }
                    index++;
                    items.add(c);
                }
                md = new MaterialDialog.Builder(AdminEditPostActivity.this).title("בחר קטגוריות").items(items).itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        catList.clear();
                        catText.setText("");
                        for (Integer i : which) {
                            catList.add(items.get(i).getCategory_id());
                            catText.setText(catText.getText().toString() + items.get(i).getCat_name()+" ");
                        }
                        return true;
                    }
                }).positiveText("אישור").build();
                Integer[] ArrayIndexs = new Integer[itemIndexs.size()];
                ArrayIndexs = itemIndexs.toArray(ArrayIndexs);
                itemIndexs.toArray(ArrayIndexs);
                md.setSelectedIndices(ArrayIndexs);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void activateButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEditPostActivity.super.onBackPressed();
            }
        });

        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                md.show();
            }
        });

        approveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changedIt = true;
                if(!Validator.ValidateDescription(descText.getText().toString())){
                    changedIt = false;
                    descText.setError("תיאור לא טוב");
                }
                if(!Validator.ValidateLocation(locText.getText().toString())){
                    changedIt = false;
                    locText.setError("מיקום לא טוב");
                }
                if(changedIt){
                    FirebaseDBProfs.EditProf(prof.getProf_ID(), user.getUser_ID(), descText.getText().toString(), catList, locText.getText().toString(),AdminEditPostActivity.this);
                    AdminEditPostActivity.super.onBackPressed();
                }
            }
        });
    }
}