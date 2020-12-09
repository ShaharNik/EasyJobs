package com.example.easyjobs.Activities.Jobs;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.utils.ImageHelper;
import com.example.easyjobs.utils.ImagesDialog;
import com.example.easyjobs.utils.Validator;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PostJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private static final int PICK_FROM_GALLERY = 222;
    private ImageView backBPJ;
    private EditText descED;
    private EditText locED;
    private EditText priceED;
    private Button postJobB;
    private Button chooseDate_postJobButton;
    private EditText dateEditTextPostJob;
    private Spinner spinnerPJ;
    private Date startD;
    private Date endD;
    private String catNum = "0";
    private Button postJobUploadButton;
    private ImageView images;
    private Dialog d;
    private ViewPager vpPager;
    private viewPageAdapter vpa;
    private ArrayList<Uri> PicsUri;
    private ArrayList<Picture> localFile;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        findViews();
        createDialog();
        activateButtons();
        setUpSpinner();
    }

    private void findViews()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        backBPJ = findViewById(R.id.back_post_job);
        descED = findViewById(R.id.editDescPJ);
        locED = findViewById(R.id.editLocPJ);
        priceED = findViewById(R.id.editPricePJ);
        postJobB = findViewById(R.id.postJobBtn);
        spinnerPJ = findViewById(R.id.pickCategoryPostJob);
        chooseDate_postJobButton = findViewById(R.id.chooseDate_postJobButton);
        dateEditTextPostJob = findViewById(R.id.dateEditTextPostJob);
        postJobUploadButton = findViewById(R.id.postJobUploadButton);
        images = findViewById(R.id.imagePostJob);
        PicsUri = new ArrayList<>();
        localFile = new ArrayList<>();
    }

    private void activateButtons()
    {
        backBPJ.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { PostJobActivity.super.onBackPressed(); }
        });

        postJobB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postJobToDB();
            }
        });

        chooseDate_postJobButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pickDate();
            }
        });

        postJobUploadButton.setOnClickListener(new View.OnClickListener()
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
            public void onClick(View v)
            {
                showDialog();
            }
        });
    }

    private void pickDate()
    {
        MaterialDatePicker.Builder<Pair<Long, Long>> builderRange = MaterialDatePicker.Builder.dateRangePicker();
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        Date min = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(min);
        c.add(Calendar.MONTH, 3);
        Date currentDatePlusOne = c.getTime();
        CalendarConstraints.DateValidator dateValidatorMin = DateValidatorPointForward.from(min.getTime());
        CalendarConstraints.DateValidator dateValidatorMax = DateValidatorPointBackward.before(currentDatePlusOne.getTime());

        ArrayList<CalendarConstraints.DateValidator> listValidators = new ArrayList<CalendarConstraints.DateValidator>();
        listValidators.add(dateValidatorMin);
        listValidators.add(dateValidatorMax);
        CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);
        constraintsBuilderRange.setValidator(validators);

        builderRange.setCalendarConstraints(constraintsBuilderRange.build());
        MaterialDatePicker<Pair<Long, Long>> pickerRange = builderRange.build();
        pickerRange.show(getSupportFragmentManager(), pickerRange.toString());
        pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>()
        {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection)
            {
                Pair<Long, Long> s = pickerRange.getSelection();
                startD = new Date(s.first);
                endD = new Date(s.second);
                DateFormat df = new SimpleDateFormat("dd/MM/yy");
                dateEditTextPostJob.setText(df.format(startD) + " - " + df.format(endD));
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
                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(PostJobActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                spinnerPJ.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        spinnerPJ.setOnItemSelectedListener(this);
    }

    private void postJobToDB()
    {
        //need to configure Date !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        String desc = descED.getText().toString();
        String loc = locED.getText().toString();
        String price = priceED.getText().toString();
        String date = dateEditTextPostJob.getText().toString();
        if (Validator.ValidateDescription(desc))
        {
            if (Validator.ValidateLocation(loc))
            {
                if (!date.isEmpty())
                {
                    if (Validator.ValidatePrice(price))
                    {
                        // --=== Add new Job and move user to back activity ==---
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseDBJobs.addNewJob(user.getUid(), desc, Integer.parseInt(price), loc, startD, endD, catNum, PicsUri);

                        Toast.makeText(PostJobActivity.this, "העבודה פורסמה בהצלחה", Toast.LENGTH_SHORT).show();
                        PostJobActivity.super.onBackPressed();
                    }
                    else
                    {
                        priceED.setError("אנא הזן מחיר");
                    }
                }
                else
                {
                    dateEditTextPostJob.setError("אנא בחר תאריכים");
                }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
    {
        catNum = ((Category) spinnerPJ.getAdapter().getItem(position)).getCategory_id();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        catNum = "0";
    }

    private void choosePictureFromGallery()
    {
        if (ActivityCompat.checkSelfPermission(PostJobActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(PostJobActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
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
        if (requestCode == PICK_FROM_GALLERY) {
            ImageHelper.setImagesFromGallery(resultCode,data,PicsUri,images,localFile,vpa,this);
        }
    }

    private void createDialog()
    {
        d = ImagesDialog.ImagesDialogBuilder(this,images,localFile);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, true, true);
        vpPager.setAdapter(vpa);
    }

    private void showDialog()
    {
        d.show();
    }
}

