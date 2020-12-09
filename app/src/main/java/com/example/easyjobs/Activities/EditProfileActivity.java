package com.example.easyjobs.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity
{
    public static final int RequestPermissionCode = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int PICK_FROM_GALLERY = 333;
    private FirebaseAuth mAuth;
    private User curUser;

    private ImageView backButt;

    private TextView fname;
    private TextView lname;
    private TextView phone;
    private TextView email;
    private TextView editTextOldPassword;
    private TextView editTextNewPassword;
    private TextView editTextCheckNewPassword;
    private ImageView EditProfileImage;
    private Boolean someFieldChanged = false;
    private Button ChoosePictureButton;
    private Button UpdateButton;
    private StorageReference mStorageRef;
    private Bitmap bitmap;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mStorageRef = FirebaseStorage.getInstance().getReference().getRoot();
        findViews();
        activateButtonsAndViews();
    }

    private void findViews()
    {
        email = findViewById(R.id.user_email);
        editTextOldPassword = findViewById((R.id.editTextOldPassword));
        phone = findViewById(R.id.editTextPhone);
        fname = findViewById(R.id.FirstNameEditText);
        lname = findViewById(R.id.LastNameEditText);
        backButt = findViewById(R.id.back_edit_profile);
        UpdateButton = findViewById(R.id.UpdateButton);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextCheckNewPassword = findViewById(R.id.editTextCheckNewPassword);
        EditProfileImage = findViewById(R.id.EditProfileImage);
        ChoosePictureButton = findViewById(R.id.ChoosePictureButton);
    }

    private void activateButtonsAndViews()
    {
        backButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditProfileActivity.super.onBackPressed();
            }
        });

        ChoosePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uploadPicture();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user_email = user.getEmail();

        DatabaseReference DR = FirebaseDBUsers.getUserByID(getIntent().getStringExtra("user_id"));
        DR.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                curUser = snapshot.getValue(User.class);
                fname.setHint(fname.getText().toString() + " " + curUser.getFirstName());
                lname.setHint(lname.getText().toString() + " " + curUser.getLastName());

                email.setText(email.getText().toString() + " " + user_email);
                email.setEnabled(false);
                phone.setHint(phone.getText().toString() + " " + curUser.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        // Update Button Pressed !
        UpdateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseUser user = mAuth.getCurrentUser();
                // password validation
                if (!editTextOldPassword.getText().toString().isEmpty())
                {
                    // Required field
                    if (Validator.ValidateUserPassword(editTextOldPassword.getText().toString()))
                    {
                        // CHECK PASSWORD IS RIGHT

                        // Get auth credentials from the user for re-authentication. The example below shows
                        // email and password credentials but there are multiple possible providers,
                        // such as GoogleAuthProvider or FacebookAuthProvider.
                        AuthCredential credential = EmailAuthProvider.getCredential(curUser.getUser_ID(), editTextOldPassword.getText().toString());
                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                // --==OLD Password is OK ==--
                                // NEW PASSWORD
                                String newPassword = editTextNewPassword.getText().toString();
                                String new_passAuth = editTextCheckNewPassword.getText().toString();
                                if (!newPassword.isEmpty() && !new_passAuth.isEmpty())
                                {
                                    if (Validator.ValidateUserPassword(newPassword) && Validator.ValidateUserPassword(new_passAuth))
                                    {
                                        if (newPassword.equals(new_passAuth))
                                        {
                                            someFieldChanged = true;
                                            // Update user password
                                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        System.out.println("successful" + newPassword);
                                                        Toast.makeText(EditProfileActivity.this, "הסיסמא שונתה בהצלחה", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        System.out.println("NOT Successful !!");
                                                        Toast.makeText(EditProfileActivity.this, "הסיסמא אינה השתנתה, נסה שנית", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            editTextNewPassword.setError("הסיסמאות אינן תואמות");
                                            editTextCheckNewPassword.setError("הסיסמאות אינן תואמות");
                                        }
                                    }
                                    else
                                    {
                                        editTextNewPassword.setError("אורך הסיסמא צריך להיות לפחות 6");
                                        editTextCheckNewPassword.setError("אורך הסיסמא צריך להיות לפחות 6");
                                    }
                                }
                                else
                                {
                                    // new pass or new passAuth is empty
                                    if (!newPassword.isEmpty() || !new_passAuth.isEmpty())
                                    {
                                        // If one of them is not empty
                                        if (newPassword.isEmpty())
                                        {
                                            editTextNewPassword.setError("חייב להקליד סיסמא חדשה פעמיים");
                                        }
                                        else
                                        {
                                            editTextCheckNewPassword.setError("חייב להקליד סיסמא חדשה פעמיים");
                                        }
                                    }
                                }
                                // If field has changed , validate update cueUser and set the bool variable to true.
                                firstNameUpdate();
                                lastNameUpdate();
                                phoneUpdate();

                                if (someFieldChanged)
                                {
                                    // Update User in DB
                                    FirebaseDBUsers.changeUserByID(curUser);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(curUser.getFirstName() + " " + curUser.getLastName()).build();
                                    user.updateProfile(profileUpdates);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();
                                    StorageReference storageReference = mStorageRef.child("UserProfilePicture/" + mAuth.getCurrentUser().getUid() + "/profilePic.jpg");
                                    storageReference.putBytes(data);
                                    Toast.makeText(EditProfileActivity.this, "העדכון בוצע בהצלחה, התחבר מחדש", Toast.LENGTH_LONG).show();
                                    // Sign user out and move to main
                                    signOutAndMoveToMainActivity();
                                }
                                else
                                {
                                    Toast.makeText(EditProfileActivity.this, "לא ביצעת שינוי", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        editTextOldPassword.setError("אורך הסיסמא צריך להיות לפחות 6");
                    }
                }
                else
                {
                    // current password empty
                    editTextOldPassword.setError("הסיסמה הנוכחית שלך אינה תקינה!");
                }
            }
        });
    }

    private void firstNameUpdate()
    {
        if (!fname.getText().toString().isEmpty())
        {
            if (Validator.ValidateUserFName(fname.getText().toString()))
            {
                curUser.setFirstName(fname.getText().toString());
                someFieldChanged = true;
            }
            else
            {
                fname.setError("שם פרטי לא אמור להכיל מספרים");
            }
        }
    }

    private void lastNameUpdate()
    {
        if (!lname.getText().toString().isEmpty())
        {
            if (Validator.ValidateUserLName(lname.getText().toString()))
            {
                curUser.setLastName(lname.getText().toString());
                someFieldChanged = true;
            }
            else
            {
                fname.setError("שם משפחה לא אמור להכיל מספרים");
            }
        }
    }

    private void phoneUpdate()
    {
        if (!phone.getText().toString().isEmpty())
        {
            if (Validator.ValidateUserPhone(phone.getText().toString()))
            {
                curUser.setPhoneNumber(phone.getText().toString());
                someFieldChanged = true;
            }
            else
            {
                phone.setError("מספר פלאפון תקין מכיל מספרים בלבד ובאורך 10");
            }
        }
    }

    private void signOutAndMoveToMainActivity()
    {
        if (!editTextNewPassword.getText().toString().isEmpty())
        {
            mAuth.signOut();
        }
        EditProfileActivity.super.onBackPressed();
    }

    private static final int CAMERA_REQUEST = 1888;

    private void uploadPicture()
    {
        // Dialog Camera or Upload
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        cameraPermission();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        galleryPermission();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setMessage("איך תרצה להעלות תמונה?").setPositiveButton("מצלמה", dialogClickListener).setNegativeButton("גלריה", dialogClickListener).show();
    }

    private void galleryPermission()
    {
        if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
        }
        else
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        }
    }

    private void cameraPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, Manifest.permission.CAMERA))
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
        else
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "בחר תמונות..."), PICK_IMAGE_REQUEST); // 2
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            // CameraPermission and UserAprroval
            bitmap = (Bitmap) data.getExtras().get("data");
            EditProfileImage.setImageBitmap(bitmap);
            someFieldChanged = true;
        }

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            // Get the Uri of data
            filePath = data.getData();
            try
            {
                // Setting image on image view using Bitmap
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                EditProfileImage.setImageBitmap(bitmap);
                someFieldChanged = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] result)
    {
        switch (requestCode)
        {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(EditProfileActivity.this, "האישור התקבל, ניתן להשתמש במצלמה", Toast.LENGTH_LONG).show();
                    takePicture();
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this, "עליך לאשר שימוש במצלמה..", Toast.LENGTH_LONG).show();
                }
                break;

            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage();
                }
                else
                {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(EditProfileActivity.this, "עליך לאשר שימוש בגלריה..", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void takePicture()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
}