package com.example.easyjobs.Activities.Profs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
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
import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBProfs;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class PostProfActivity extends AppCompatActivity
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
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prof);

        findViews();
        createDialog();
        activateButtons();
        setUpSpinner();
        CatChosen = new ArrayList<>();
        spinnerPP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                md.show();
            }
        });
    }

    private void findViews()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        descED = findViewById(R.id.editDescPP);
        locED = findViewById(R.id.editLocPP);
        IdED = findViewById(R.id.editIdPP);
        backBPP = findViewById(R.id.back_post_prof);
        postProfB = findViewById(R.id.postProfBtn);
        spinnerPP = findViewById(R.id.pickCategoryPostProf);
        postProfUploadButton = findViewById(R.id.postProfUploadButton);
        PicsUri = new ArrayList<>();
        localFile = new ArrayList<>();
        images = findViewById(R.id.imagePostProf);
    }

    private void activateButtons()
    {
        backBPP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PostProfActivity.super.onBackPressed();
            }
        });

        postProfB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postJobToDB();
            }
        });

        postProfUploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                choosePictureFromGallery();
            }
        });
        images.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showDialog();
            }
        });
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
        switch (requestCode)
        {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                }
                else
                {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (data.getClipData() != null)
                {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++)
                    {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        PicsUri.add(imageUri);
                        try
                        {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            File Image = File.createTempFile("Picture", ".jpg");
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos); // YOU can also save it in JPEG
                            byte[] bitmapdata = bos.toByteArray();

                            //write the bytes in file
                            FileOutputStream fos = new FileOutputStream(Image);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            Picture p = new Picture(Image, imageUri.getLastPathSegment());
                            localFile.add(p);

                            vpa.notifyDataSetChanged();

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (localFile.size() > 0)
                    {
                        Bitmap myBitmap = BitmapFactory.decodeFile(localFile.get(0).getF().getAbsolutePath());
                        images.setImageBitmap(myBitmap);
                        images.setVisibility(View.VISIBLE);
                        images.setEnabled(true);
                    }
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
            }
            else if (data != null && data.getData() != null)
            {
                PicsUri.add(data.getData());
                //do something with the image (save it to some directory or whatever you need to do with it here)
                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    File Image = File.createTempFile("Picture", ".jpg");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos); // YOU can also save it in JPEG
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(Image);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    Picture p = new Picture(Image, data.getData().getLastPathSegment());
                    localFile.add(p);
                    vpa.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createDialog()
    {
        d = new Dialog(PostProfActivity.this);
        d.setContentView(R.layout.view_pager_layout);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.setTitle("Pictures");
        d.setOnDismissListener(dialog->{
            System.out.println("DISMISS");
            if (localFile.isEmpty())
            {
                images.setVisibility(View.INVISIBLE);
                images.setEnabled(false);
            }
        });
        d.setCancelable(true);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, false, true);
        vpPager.setAdapter(vpa);
    }

    private void showDialog()
    {
        d.show();
    }
}