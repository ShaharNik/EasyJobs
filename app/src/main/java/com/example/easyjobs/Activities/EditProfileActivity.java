package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyjobs.Objects.User;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDBUsers FDBU;

    private ImageView backButt;

    private TextView fname;
    private TextView lname;
    private TextView phone;
    private TextView email;
    private TextView editTextOldPassword;
    private TextView editTextNewPassword;
    private TextView editTextCheckNewPassword;
    private User curUser;

    Boolean someFieldChanged = false;
    Boolean userOldPassIsRight = false;

    private Button UpdateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //validator = new Validator();
        //validator.
        findViews();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String nickname = user.getDisplayName();
        String user_email = user.getEmail();

        FDBU = new FirebaseDBUsers();
        DatabaseReference DR = FDBU.getUserByID(getIntent().getStringExtra("user_id"));
        DR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curUser = snapshot.getValue(User.class);
                fname.setHint(fname.getText().toString() + " " + curUser.getFirstName());
                lname.setHint(lname.getText().toString() + " " + curUser.getLastName());

                email.setText(email.getText().toString() + " " + user_email);
                email.setEnabled(false);
                phone.setHint(phone.getText().toString() + " " + curUser.getPhoneNumber());
                //System.err.println(user.getDisplayName() + " " + user.getEmail() + " " + user.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        // Update Button Pressed !
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When clicking On UPDATE:
                // 1) check user current password is correct.
                // 2) check validation of changed fields.
                // 3) update fire-base with the new inputs.
                // 4) logout user and send him to main activity
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDBUsers UsersDB = new FirebaseDBUsers();


                // password validation
                if (!editTextOldPassword.getText().toString().isEmpty()) // Required field
                {
                    if (Validator.ValidateUserPassword(editTextOldPassword.getText().toString())) {
                        // CHECK PASSWORD IS RIGHT

                        // Get auth credentials from the user for re-authentication. The example below shows
                        // email and password credentials but there are multiple possible providers,
                        // such as GoogleAuthProvider or FacebookAuthProvider.
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(curUser.getUser_ID(), editTextOldPassword.getText().toString());
                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                    user.updatePassword(newPassword)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
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
                                        else // new pass or new passAuth is empty
                                        {
                                            if (!newPassword.isEmpty() || !new_passAuth.isEmpty()) // If one of them is not empty
                                            {
                                                if (newPassword.isEmpty())
                                                    editTextNewPassword.setError("חייב להקליד סיסמא חדשה פעמיים");
                                                else
                                                    editTextCheckNewPassword.setError("חייב להקליד סיסמא חדשה פעמיים");
                                            }

                                        }
                                        //----------------------------------------------------------
                                        // If field has changed , validate update cueUser and set the bool variable to true.
                                        firstNameUpdate();
                                        lastNameUpdate();
                                        phoneUpdate();

                                        if (someFieldChanged) {
                                            // Update User in DB
                                            UsersDB.changeUserByID(curUser);
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(curUser.getFirstName() + " " + curUser.getLastName())
                                                    .build();
                                            user.updateProfile(profileUpdates);
                                            Toast.makeText(EditProfileActivity.this, "העדכון בוצע בהצלחה, התחבר מחדש", Toast.LENGTH_LONG).show();
                                            // Sign user out and move to main
                                            signOutAndMoveToMainActivity();
                                        }
                                        else
                                        {
                                            Toast.makeText(EditProfileActivity.this, "לא ביצעת שינוי", Toast.LENGTH_SHORT).show();
                                        }

                                        //Log.d(TAG, "User re-authenticated.");
                                    }
                                });
                    }
                    else
                    {
                        editTextOldPassword.setError("אורך הסיסמא צריך להיות לפחות 6");
                    }

                }
                else // current password empty
                {
                    editTextOldPassword.setError("הסיסמה הנוכחית שלך אינה תקינה!");
                }
            }
        });

    }

    private void findViews()
    {
        email = findViewById(R.id.user_email);
        editTextOldPassword = findViewById((R.id.editTextOldPassword));
        phone = findViewById(R.id.editTextPhone);
        fname = findViewById(R.id.FirstNameEditText);
        lname = findViewById(R.id.LastNameEditText);
        backButt = findViewById(R.id.back_registerEP);
        UpdateButton = findViewById(R.id.UpdateButton);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextCheckNewPassword = findViewById(R.id.editTextCheckNewPassword);
    }
    private void firstNameUpdate()
    {
        if (!fname.getText().toString().isEmpty()) {
            if (Validator.ValidateUserFName(fname.getText().toString())) {
                curUser.setFirstName(fname.getText().toString());
                someFieldChanged = true;
            }
            else
                fname.setError("שם פרטי לא אמור להכיל מספרים");

        }

    }
    private void lastNameUpdate()
    {
        if (!lname.getText().toString().isEmpty()) {
            if (Validator.ValidateUserLName(lname.getText().toString())) {
                curUser.setLastName(lname.getText().toString());
                someFieldChanged = true;
            }
            else
            {
                fname.setError("שם משפחה לא אמור להכיל מספרים");
            }
        }
    }
    private void phoneUpdate() {
        if (!phone.getText().toString().isEmpty()) {
            if (Validator.ValidateUserPhone(phone.getText().toString())) {
                curUser.setPhoneNumber(phone.getText().toString());
                someFieldChanged = true;
            }
            else
                phone.setError("מספר פלאפון תקין מכיל מספרים בלבד ובאורך 10");
        }
    }

    private void signOutAndMoveToMainActivity()
    {
        mAuth.signOut();
        EditProfileActivity.super.onBackPressed();
    }

}