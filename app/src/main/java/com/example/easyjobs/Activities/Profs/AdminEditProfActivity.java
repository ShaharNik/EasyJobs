package com.example.easyjobs.Activities.Profs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.Objects.Prof;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.dataBase.FirebaseStorage;
import com.example.easyjobs.utils.ImageHelper;
import com.example.easyjobs.utils.dialogHelper;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEditProfActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int PICK_FROM_GALLERY = 420;
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
    private Button addImages;
    private MaterialDialog md;
    private AlertDialog.Builder builder;
    private ImageView adminEditPostImage;
    private ArrayList<Picture> localFile;
    private ArrayList<Picture> OriginalLocalFile;
    private Dialog d;
    private ViewPager vpPager;
    private viewPageAdapter vpa;
    private User user;
    private Prof prof;
    List<String> catList;
    private ArrayList<Uri> PicsUri;

    private boolean changedIt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_post);

        getExtra();
        findViews();
        inits();
        setUserCurrentData();
        activateButtons();
    }

    private void getExtra()
    {
        user = (User) getIntent().getSerializableExtra("User");
        prof = (Prof) getIntent().getSerializableExtra("Prof");
        localFile = (ArrayList<Picture>) getIntent().getSerializableExtra("File");
    }

    private void findViews()
    {
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
        addImages = findViewById(R.id.addImagesProf);
    }

    private void inits()
    {
        builder = new AlertDialog.Builder(this);
        PicsUri = new ArrayList<Uri>();
        catList = new ArrayList<String>();
        OriginalLocalFile = new ArrayList<>();
        for (Picture p : localFile)
        {
            OriginalLocalFile.add(p);
        }
    }

    private void setUserCurrentData()
    {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        rateText.setText(user.getRating() + " (" + user.getRatingsAmount() + ")");
        descText.setText(prof.getDesc());
        locText.setText(prof.getLocation());
        phoneText.setText(user.getPhoneNumber());
        if (localFile.size() >= 1)
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.get(0).getF().getAbsolutePath());
            adminEditPostImage.setImageBitmap(myBitmap);
        }
    }

    private void activateButtons()
    {
        backButton.setOnClickListener(this);
        spinner.setOnClickListener(this);
        approveChanges.setOnClickListener(this);
        deletePost.setOnClickListener(this);
        adminEditPostImage.setOnClickListener(this);
        addImages.setOnClickListener(this);
        setupDialog();
        createDialog();
        setUpSpinner();
    }

    @Override
    public void onClick(View ClickedButton)
    {
        if(backButton.equals(ClickedButton))
        {
            AdminEditProfActivity.super.onBackPressed();
        }
        if(spinner.equals(ClickedButton))
        {
            md.show();
        }
        if(approveChanges.equals(ClickedButton))
        {
            changedIt = true;
            if (!Validator.ValidateDescription(descText.getText().toString()))
            {
                changedIt = false;
                descText.setError("תיאור לא טוב");
            }
            if (!Validator.ValidateLocation(locText.getText().toString()))
            {
                changedIt = false;
                locText.setError("מיקום לא טוב");
            }
            if (changedIt)
            {
                for (Picture p : OriginalLocalFile)
                {
                    if (!localFile.contains(p))
                    {
                        FirebaseStorage.deleteSpecificProfPicture(prof.getProf_ID(), p.getName());
                    }
                }
                ArrayList<Uri> toDelete = new ArrayList<>();
                for (Uri u : PicsUri)
                {
                    boolean exists = false;
                    for (Picture p : localFile)
                    {
                        if (p.getName().compareTo(u.getLastPathSegment()) == 0)
                        {
                            exists = true;
                        }
                    }
                    if (!exists)
                    {
                        toDelete.add(u);
                    }
                }
                for (Uri u : toDelete)
                {
                    PicsUri.remove(u);
                }
                FirebaseDBProfs.EditProf(prof.getProf_ID(), user.getUser_ID(), descText.getText().toString(), catList, locText.getText().toString(), AdminEditProfActivity.this, PicsUri);
            }
        }
        if(deletePost.equals(ClickedButton))
        {
            FirebaseStorage.deleteProfPictures(prof.getProf_ID());
            builder.show();
        }
        if(adminEditPostImage.equals(ClickedButton))
        {
            showDialog();
        }
        if(addImages.equals(ClickedButton))
        {
            choosePictureFromGallery();
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
                ArrayList<Integer> itemIndexs = new ArrayList<Integer>();

                int index = 0;
                for (DataSnapshot category : snapshot.getChildren())
                {
                    Category c = category.getValue(Category.class);
                    if (prof.getCategory().contains(c.getCategory_id()))
                    {
                        catList.add(c.getCategory_id());
                        catText.setText(catText.getText().toString() + c.getCat_name() + " ");
                        itemIndexs.add(index);
                    }
                    index++;
                    items.add(c);
                }
                md = new MaterialDialog.Builder(AdminEditProfActivity.this).title("בחר קטגוריות").items(items).itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text)
                    {
                        catList.clear();
                        catText.setText("");
                        for (Integer i : which)
                        {
                            catList.add(items.get(i).getCategory_id());
                            catText.setText(catText.getText().toString() + items.get(i).getCat_name() + " ");
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
            public void onCancelled(@NonNull DatabaseError error){}
        });
    }

    private void setupDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseDBProfs.removeProf(prof.getProf_ID(), AdminEditProfActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(AdminEditProfActivity.this, ":)", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        builder.setMessage("האם אתה בטוח?").setPositiveButton("כן", dialogClickListener).setNegativeButton("לא", dialogClickListener);
    }

    private void choosePictureFromGallery()
    {
        if (ActivityCompat.checkSelfPermission(AdminEditProfActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(AdminEditProfActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
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
            ImageHelper.setImagesFromGallery(resultCode,data,PicsUri,adminEditPostImage,localFile,vpa,this);
        }
    }

    private void createDialog()
    {
        d = dialogHelper.ImagesDialogBuilder(this,adminEditPostImage,localFile);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, false, true);
        vpPager.setAdapter(vpa);
    }

    private void showDialog()
    {
        d.show();
    }
}