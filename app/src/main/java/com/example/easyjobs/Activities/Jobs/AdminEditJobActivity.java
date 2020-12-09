package com.example.easyjobs.Activities.Jobs;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.Objects.Job;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBJobs;
import com.example.easyjobs.dataBase.FirebaseStorage;
import com.example.easyjobs.utils.ImageHelper;
import com.example.easyjobs.utils.dialogHelper;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminEditJobActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final int PICK_FROM_GALLERY = 420;
    String cat_id;
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
    private ArrayList<Picture> OriginalLocalFile;
    private ArrayList<Uri> PicsUri;
    private User user;
    private Job job;
    private Dialog d;
    private ViewPager vpPager;
    private viewPageAdapter vpa;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_job);

        getExtra();
        findViews();
        inits();
        setUserCurrentData();
        activateButtons();
    }

    private void getExtra() {
        user = (User) getIntent().getSerializableExtra("User");
        job = (Job) getIntent().getSerializableExtra("Job");
        localFile = (ArrayList<Picture>) getIntent().getSerializableExtra("File");
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
        editJobImage = findViewById(R.id.editJobImage);
        addImages = findViewById(R.id.addImagesJob);
    }

    private void inits() {
        builder = new AlertDialog.Builder(this);
        PicsUri = new ArrayList<>();
        OriginalLocalFile = new ArrayList<>();
        for (Picture p : localFile) {
            OriginalLocalFile.add(p);
        }
    }

    private void setUserCurrentData() {
        namesText.setText(user.getFirstName() + " " + user.getLastName());
        descText.setText(job.getDesc());
        locText.setText(job.getLocation());
        priceText.setText(job.getPrice() + "");
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        dateText.setText(df.format(job.getStartDate()) + " - " + df.format(job.getEndDate()));
        if (localFile.size() >= 1) {
            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.get(0).getF().getAbsolutePath());
            editJobImage.setImageBitmap(myBitmap);
        }
    }

    private void activateButtons() {
        backButton.setOnClickListener(this);
        approveChanges.setOnClickListener(this);
        deletePost.setOnClickListener(this);
        editJobImage.setOnClickListener(this);
        addImages.setOnClickListener(this);
        setUpSpinner();
        setupDialog();
        createDialog();
    }

    @Override
    public void onClick(View ClickedButton) {
        if (backButton.equals(ClickedButton)) {
            AdminEditJobActivity.super.onBackPressed();
        }
        if (approveChanges.equals(ClickedButton)) {
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
                for (Picture p : OriginalLocalFile) {
                    if (!localFile.contains(p)) {
                        FirebaseStorage.deleteSpecificJobPicture(job.getJob_ID(), p.getName());
                    }
                }
                ArrayList<Uri> toDelete = new ArrayList<>();
                for (Uri u : PicsUri) {
                    boolean exists = false;
                    for (Picture p : localFile) {
                        if (p.getName().compareTo(u.getLastPathSegment()) == 0) {
                            exists = true;
                        }
                    }
                    if (!exists) {
                        toDelete.add(u);
                    }
                }
                for (Uri u : toDelete) {
                    PicsUri.remove(u);
                }
                FirebaseDBJobs.editJob(job.getJob_ID(), user.getUser_ID(), descText.getText().toString(), Integer.parseInt(priceText.getText().toString()), locText.getText().toString(), job.getStartDate(), job.getEndDate(), cat_id, AdminEditJobActivity.this, PicsUri);
            }
        }
        if (deletePost.equals(ClickedButton)) {
            builder.show();
        }
        if (editJobImage.equals(ClickedButton)) {
            showDialog();
        }
        if (addImages.equals(ClickedButton)) {
            choosePictureFromGallery();
        }
    }

    private void setUpSpinner() {
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> items = new ArrayList<>();
                int index = -1;
                int rightIndex = 0;
                for (DataSnapshot category : snapshot.getChildren()) {
                    Category c = category.getValue(Category.class);
                    items.add(c);
                    index++;
                    if (c.getCategory_id().compareTo(job.getCategory_ID()) == 0) {
                        rightIndex = index;
                    }
                }
                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(AdminEditJobActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);
                spinner.setSelection(rightIndex, false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        spinner.setOnItemSelectedListener(this);
    }

    private void setupDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseDBJobs.RemoveJob(job.getJob_ID(), AdminEditJobActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(AdminEditJobActivity.this, ":)", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        builder.setMessage("האם אתה בטוח?").setPositiveButton("כן", dialogClickListener).setNegativeButton("לא", dialogClickListener);
    }

    private void choosePictureFromGallery() {
        if (ActivityCompat.checkSelfPermission(AdminEditJobActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AdminEditJobActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_FROM_GALLERY) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY) {
                ImageHelper.setImagesFromGallery(resultCode,data,PicsUri,editJobImage,localFile,vpa,this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cat_id = ((Category) spinner.getAdapter().getItem(position)).getCategory_id();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void createDialog() {
        d = dialogHelper.ImagesDialogBuilder(this,editJobImage,localFile);
        vpPager = (ViewPager) d.findViewById(R.id.vpPager);
        vpa = new viewPageAdapter(this, localFile, true, true);
        vpPager.setAdapter(vpa);
    }

    private void showDialog() {
        d.show();
    }
}