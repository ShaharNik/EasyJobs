package com.example.easyjobs.Activities.Profs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminEditProfActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_post);

        user = (User) getIntent().getSerializableExtra("User");
        prof = (Prof) getIntent().getSerializableExtra("Prof");
        localFile = (ArrayList<Picture>) getIntent().getSerializableExtra("File");
        catList = new ArrayList<String>();
        OriginalLocalFile = new ArrayList<>();
        for (Picture p :
                localFile) {
            OriginalLocalFile.add(p);
        }
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
        addImages = findViewById(R.id.addImagesProf);
        PicsUri = new ArrayList<Uri>();
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
                    for(Picture p : OriginalLocalFile)
                    {
                        if(!localFile.contains(p))
                        {
                            FirebaseStorage.deleteSpecificProfPicture(prof.getProf_ID(), p.getName());
                        }
                    }
                    ArrayList<Uri> toDelete = new ArrayList<>();
                    for(Uri u : PicsUri)
                    {
                        boolean exists=false;
                        for(Picture p : localFile)
                        {
                            if(p.getName().compareTo(u.getLastPathSegment())==0)
                            {
                                exists=true;
                            }
                        }
                        if(!exists)
                        {
                            toDelete.add(u);
                        }
                    }
                    for(Uri u : toDelete)
                    {
                        PicsUri.remove(u);
                    }
                    FirebaseDBProfs.EditProf(prof.getProf_ID(), user.getUser_ID(), descText.getText().toString(), catList, locText.getText().toString(), AdminEditProfActivity.this, PicsUri);
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

        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePictureFromGallery();
            }
        });
    }

    private void choosePictureFromGallery()
    {
        if (ActivityCompat.checkSelfPermission(AdminEditProfActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AdminEditProfActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
        }
        else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_GALLERY) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for(int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        PicsUri.add(imageUri);
                        try {
                            Bitmap bitmap = MediaStore
                                    .Images.Media.getBitmap(
                                            getContentResolver(),
                                            imageUri);
                            File Image = File.createTempFile("Picture", ".jpg");
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
                            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                            FileOutputStream fos = new FileOutputStream(Image);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            Picture p = new Picture(Image,imageUri.getLastPathSegment());
                            localFile.add(p);

                            vpa.notifyDataSetChanged();

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
//                        localFile.add()
//                        File f = new File(imageUri.toString());
//                        Picture p = new Picture(f,imageUri.getLastPathSegment());
//                        localFile.add(p);
//                        vpa.notifyDataSetChanged();
                    }
                    if(localFile.size()>0)
                    {
                        Bitmap myBitmap = BitmapFactory.decodeFile(localFile.get(0).getF().getAbsolutePath());
                        adminEditPostImage.setImageBitmap(myBitmap);
                        adminEditPostImage.setVisibility(View.VISIBLE);
                        adminEditPostImage.setEnabled(true);
                    }
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
            } else if(data!= null && data.getData() != null) {
                PicsUri.add(data.getData());
                try {
                    Bitmap bitmap = MediaStore
                            .Images.Media.getBitmap(
                                    getContentResolver(),
                                    data.getData());
                    File Image = File.createTempFile("Picture", ".jpg");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
                    byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                    FileOutputStream fos = new FileOutputStream(Image);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    Picture p = new Picture(Image,data.getData().getLastPathSegment());
                    localFile.add(p);
                    vpa.notifyDataSetChanged();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
//                File f = new File(data.getData().toString());
//                Picture p = new Picture(f,data.getData().getLastPathSegment());
//                localFile.add(p);
//                vpa.notifyDataSetChanged();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }
    }

    private void createDialog() {

        d = new Dialog(AdminEditProfActivity.this);

        d.setContentView(R.layout.view_pager_layout);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.setTitle("Pictures");
        d.setCancelable(true);
        d.setOnDismissListener(dialog -> {
            System.out.println("DISMISS");
            if(localFile.isEmpty())
            {
                adminEditPostImage.setVisibility(View.INVISIBLE);
                adminEditPostImage.setEnabled(false);
            }
        });
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, prof.getProf_ID(), false, true);
        vpPager.setAdapter(vpa);

    }
    private void showDialog()
    {
        d.show();
    }
}