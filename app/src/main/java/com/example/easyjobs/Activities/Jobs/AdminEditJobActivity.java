package com.example.easyjobs.Activities.Jobs;

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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.dataBase.FirebaseStorage;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminEditJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PICK_FROM_GALLERY = 420;

    private ImageView backButton;
    private TextView namesText;
    private EditText descText;
    private EditText locText;
    private EditText priceText;
    private TextView dateText;
    private Spinner spinner;
    private Button approveChanges;
    private Button deletePost;
    private Button addImages;
    private ImageView editJobImage;
    private ArrayList<Picture> localFile;
    private ArrayList<Uri> PicsUri;
    private User user;
    private Job job;
    private Dialog d;
    private ViewPager vpPager;
    private viewPageAdapter vpa;
    String cat_id;

    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_job);

        user = (User) getIntent().getSerializableExtra("User");
        job = (Job) getIntent().getSerializableExtra("Job");
        localFile = (ArrayList<Picture>) getIntent().getSerializableExtra("File");
        findViews();
        inputTempData();
        setUpSpinner();
        activateButtons();
        setupDialog();
        createDialog();
    }

    private void findViews() {
        backButton = findViewById(R.id.back_admin_editPost);
        namesText = findViewById(R.id.namesEJ);
        descText = findViewById(R.id.descriptionEJ);
        locText = findViewById(R.id.locationEJ);
        priceText = findViewById(R.id.priceEJ);
        dateText = findViewById(R.id.dateEJ);
        spinner = findViewById(R.id.AdminPickCatEditJob);
        approveChanges = findViewById(R.id.changeJobDetails);
        deletePost = findViewById(R.id.deleteJobButton);
        builder = new AlertDialog.Builder(this);
        editJobImage = findViewById(R.id.editJobImage);
        addImages = findViewById(R.id.addImagesJob);
        PicsUri = new ArrayList<>();
    }

    private void inputTempData() {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        descText.setText(job.getDesc());
        locText.setText(job.getLocation());
        priceText.setText(job.getPrice()+"");

        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        dateText.setText(df.format(job.getStartDate())+" - " + df.format(job.getEndDate()));
        if(localFile.size()>=1)
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.get(0).getF().getAbsolutePath());
            editJobImage.setImageBitmap(myBitmap);
            //editJobImage.setImageURI(localFile.get(0));
        }
    }

    private void setUpSpinner(){
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> items = new ArrayList<>();
                int index=-1;
                int rightIndex = 0;
                for(DataSnapshot category : snapshot.getChildren()){
                    Category c = category.getValue(Category.class);
                    items.add(c);
                    index++;
                    if(c.getCategory_id().compareTo(job.getCategory_ID())==0) {
                        rightIndex=index;
                    }

                }

                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(AdminEditJobActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);
                spinner.setSelection(rightIndex,false);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        spinner.setOnItemSelectedListener(this);

    }

    private void setupDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        FirebaseDBJobs.RemoveJob(job.getJob_ID(),AdminEditJobActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(AdminEditJobActivity.this, ":)", Toast.LENGTH_SHORT).show();
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
                AdminEditJobActivity.super.onBackPressed();
            }
        });

        approveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean changedIt = true;
                if (!Validator.ValidateDescription(descText.getText().toString())) {
                    changedIt = false;
                    descText.setError("תיאור לא טוב");
                }
                if (!Validator.ValidateLocation(locText.getText().toString())) {
                    changedIt = false;
                    locText.setError("מיקום לא טוב");
                }
                if (changedIt) {
                    FirebaseDBJobs.editJob(job.getJob_ID(), user.getUser_ID(), descText.getText().toString(), Integer.parseInt(priceText.getText().toString()), locText.getText().toString(), job.getStartDate(), job.getEndDate(), cat_id, AdminEditJobActivity.this, PicsUri);
                    //TODO Try to figure out how know what shit to delete and what shit to keep
                }
            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        editJobImage.setOnClickListener(new View.OnClickListener() {
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
        if (ActivityCompat.checkSelfPermission(AdminEditJobActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AdminEditJobActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cat_id = ((Category)spinner.getAdapter().getItem(position)).getCategory_id();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        job.getCategory_ID();
    }

    private void createDialog() {

        d = new Dialog(AdminEditJobActivity.this);
        d.setContentView(R.layout.view_pager_layout);
        d.setTitle("Pictures");
        d.setCancelable(true);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, job.getJob_ID(), true, true);
        vpPager.setAdapter(vpa);

    }

    private void showDialog()
    {
        d.show();
    }
}