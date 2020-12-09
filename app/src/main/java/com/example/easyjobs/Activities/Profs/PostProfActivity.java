package com.example.easyjobs.Activities.Profs;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.easyjobs.Activities.Jobs.PostJobActivity;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.utils.ImageHelper;
import com.example.easyjobs.utils.dialogHelper;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class PostProfActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int PICK_FROM_GALLERY = 222;
    private ImageView backBPP;

    private Button spinnerPP;
    private EditText descED;
    private EditText locED;
    private EditText IdED;
    private FirebaseAuth mAuth;
    private Button postProfB;
    private MaterialDialog md;
    private ArrayList<String> CatChosen;
    private Button postProfUploadButton;
    private ImageView images;
    private Dialog d;
    private ViewPager vpPager;
    private viewPageAdapter vpa;
    private ArrayList<Uri> PicsUri;
    private ArrayList<Picture> localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prof);

        findViews();
        inits();
        createDialog();
        activateButtons();
        setUpSpinner();
    }

    private void findViews()
    {
        descED = findViewById(R.id.editDescPP);
        locED = findViewById(R.id.editLocPP);
        IdED = findViewById(R.id.editIdPP);
        backBPP = findViewById(R.id.back_post_prof);
        postProfB = findViewById(R.id.postProfBtn);
        spinnerPP = findViewById(R.id.pickCategoryPostProf);
        postProfUploadButton = findViewById(R.id.postProfUploadButton);
        images = findViewById(R.id.imagePostProf);
    }
    private void inits()
    {
        PicsUri = new ArrayList<>();
        localFile = new ArrayList<>();
        CatChosen = new ArrayList<>();
    }

    private void activateButtons()
    {
        spinnerPP.setOnClickListener(this);
        backBPP.setOnClickListener(this);
        postProfB.setOnClickListener(this);
        postProfUploadButton.setOnClickListener(this);
        images.setOnClickListener(this);
    }
    @Override
    public void onClick(View ClickedButton) {
        if(spinnerPP.equals(ClickedButton))
        {
            md.show();
        }
        else if(backBPP.equals(ClickedButton))
        {
            PostProfActivity.super.onBackPressed();
        }
        else if(postProfB.equals(ClickedButton))
        {
            postJobToDB();
        }
        else if(postProfUploadButton.equals(ClickedButton))
        {
            choosePictureFromGallery();
        }
        else if(images.equals(ClickedButton))
        {
            showDialog();
        }
    }

    private void setUpSpinner()
    {
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ArrayList<Category> items = new ArrayList<>();
                for (DataSnapshot category : snapshot.getChildren())
                {
                    Category c = category.getValue(Category.class);
                    items.add(c);
                }
                md = new MaterialDialog.Builder(PostProfActivity.this).title("בחר קטגוריות").items(items).itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text)
                    {
                        for (Integer i : which)
                        {
                            CatChosen.add(items.get(i).getCategory_id());
                        }
                        return true;
                    }
                }).positiveText("אישור").build();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void postJobToDB()
    {
        String desc = descED.getText().toString();
        String loc = locED.getText().toString(); // at least 3
        String id = IdED.getText().toString(); // 9 digits
        if (Validator.ValidateIsraeliId(id))
        {
            if (Validator.ValidateDescription(desc))
            {
                if (Validator.ValidateLocation(loc))
                {
                    // --=== Add new Prof and move user to back activity ==---
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    FirebaseDBProfs.addNewProf(user.getUid(), desc, CatChosen, loc, PicsUri);
                    Toast.makeText(PostProfActivity.this, "התפרסמת בהצלחה", Toast.LENGTH_SHORT).show();
                    PostProfActivity.super.onBackPressed();
                }
                else
                {
                    locED.setError("המיקום אינו תקין, חייב להיות באורך 3 לפחות");
                }
            }
            else
            {
                descED.setError("התיאור קצר מידי");
            }
        }
        else
        {
            IdED.setError("ת.ז שהזנת אינה תקינה");
        }
    }

    private void choosePictureFromGallery()
    {
        if (ActivityCompat.checkSelfPermission(PostProfActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(PostProfActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
        }
        else
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PICK_FROM_GALLERY) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
            } else {
                Toast.makeText(this, "לא סיפקת הרשאות מתאימות", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY)
        {
            ImageHelper.setImagesFromGallery(resultCode,data,PicsUri,images,localFile,vpa,this);
        }
    }

    private void createDialog()
    {
        d = dialogHelper.ImagesDialogBuilder(this,images,localFile);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, false, true);
        vpPager.setAdapter(vpa);
    }

    private void showDialog()
    {
        d.show();
    }


}