package com.example.easyjobs.Activities.Profs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.easyjobs.Activities.Jobs.AdminEditJobActivity;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.dataBase.FirebaseStorage;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEditProfActivity extends AppCompatActivity {

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
    private AlertDialog.Builder builder;
    private ImageView adminEditPostImage;
    private ArrayList<Picture> localFile;
    private Dialog d;
    private ViewPager vpPager;
    private viewPageAdapter vpa;
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
        localFile = (ArrayList<Picture>) getIntent().getSerializableExtra("File");
        catList = new ArrayList<String>();

        findViews();
        inputTempData();
        setUpSpinner();
        activateButtons();
        setupDialog();
        createDialog();
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
        adminEditPostImage = findViewById(R.id.adminEditPostImage);
        builder = new AlertDialog.Builder(this);
    }

    private void inputTempData() {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        rateText.setText(user.getRating()+ " ("+ user.getRatingsAmount()+")");
        descText.setText(prof.getDesc());
        locText.setText(prof.getLocation());
        phoneText.setText(user.getPhoneNumber());
        if(localFile.size()>=1)
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.get(0).getF().getAbsolutePath());
            adminEditPostImage.setImageBitmap(myBitmap);
            //editJobImage.setImageURI(localFile.get(0));
        }
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
                md = new MaterialDialog.Builder(AdminEditProfActivity.this).title("בחר קטגוריות").items(items).itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
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

    private void setupDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseDBProfs.removeProf(prof.getProf_ID(), AdminEditProfActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(AdminEditProfActivity.this, ":)", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        builder.setMessage("האם אתה בטוח?").setPositiveButton("כן", dialogClickListener)
                .setNegativeButton("לא", dialogClickListener);
    }

    private void activateButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminEditProfActivity.super.onBackPressed();
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
                    FirebaseDBProfs.EditProf(prof.getProf_ID(), user.getUser_ID(), descText.getText().toString(), catList, locText.getText().toString(), AdminEditProfActivity.this);
                }
            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseStorage.deleteProfPictures(prof.getProf_ID());
                builder.show();
            }

        });

        adminEditPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void createDialog() {

        d = new Dialog(AdminEditProfActivity.this);
        d.setContentView(R.layout.view_pager_layout);
        d.setTitle("Pictures");
        d.setCancelable(true);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, prof.getProf_ID(), false, true);
        vpPager.setAdapter(vpa);

    }
    private void showDialog()
    {
        d.show();
    }
}