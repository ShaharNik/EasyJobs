package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ImageView backBR;
    private Button RegBut;
    private EditText user_emailEditText;
    private EditText editTextPassword;
    private EditText user_phoneNumber;
    private EditText user_firstName;
    private EditText user_lastName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();
        acvtivateButtonsAndViews();
    }

    private void findViews(){
        user_emailEditText = findViewById(R.id.user_email);
        editTextPassword = findViewById((R.id.editTextPassword));
        user_phoneNumber = findViewById(R.id.editTextPhone);
        user_firstName = findViewById(R.id.FirstNameEditText);
        user_lastName = findViewById(R.id.LastNameEditText);
        backBR = findViewById(R.id.back_register);
        RegBut = findViewById(R.id.RegisterButton);
    }

    private void acvtivateButtonsAndViews(){
        mAuth = FirebaseAuth.getInstance();

        backBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.super.onBackPressed();
            }
        });

        RegBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = user_emailEditText.getText().toString();
                String user_password = editTextPassword.getText().toString();
                String phoneNumber = user_phoneNumber.getText().toString();
                String fname = user_firstName.getText().toString();
                String lname = user_lastName.getText().toString();
                if (ValidateUserInformation(user_email,user_password, phoneNumber, fname, lname)) {
                    createAccount(user_email, user_password, phoneNumber, fname, lname);
                }
            }
        });
    }

    void createAccount(String email, String password, String phoneNumber, String fname, String lname) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user, phoneNumber, fname, lname);
                    Toast.makeText(RegisterActivity.this, "התחברת!", Toast.LENGTH_SHORT).show();
                    premiumDialog(fname,lname);
                    //RegisterActivity.super.onBackPressed(); // Go previous page

                }
                else {
                    //If sign in fails, display a message to the user.
                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "האימות נכשל.", Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }

    boolean ValidateUserInformation(String email, String password, String phone, String fname, String lname) {
        // if there is an empty field
        if (email.isEmpty() || password.isEmpty() || phone.isEmpty() || fname.isEmpty() || lname.isEmpty()){
            Toast.makeText(RegisterActivity.this, "מלא את כל השדות בבקשה", Toast.LENGTH_SHORT).show();
            return false;
        }

        // email validation
        if (!Validator.ValidateUserEmail(email)) {
            user_emailEditText.setError("אימייל אינו חוקי");
            return false;
        }

        // check if password is at least 6
        if (!Validator.ValidateUserPassword(password)) {
            editTextPassword.setError("סיסמא אינה חוקית, צריכה להיות באורך 6 לפחות");
            return false;
        }

        // check if first name contains only letters
        if (!Validator.ValidateUserFName(fname)) {
            user_firstName.setError("שם פרטי אינו חוקי, צריך להכיל אותיות בלבד");
            return false;
        }

        // check if last name contains only letters
        if (!Validator.ValidateUserLName(lname)) {
            user_lastName.setError("שם משפחה אינו חוקי, צריך להכיל אותיות בלבד");
            return false;
        }

        // check if phone number contains only numbers
        if (!Validator.ValidateUserPhone(phone)) {
            user_phoneNumber.setError("מספר פלאפון תקין מכיל מספרים בלבד ובאורך 10");
            return false;
        }

        return true;
    }

    void updateUI(FirebaseUser user, String phoneNumber, String fname, String lname) {// we need update something from user information?
        TextView RegisterDummy = findViewById(R.id.RegisterDummy);
        RegisterDummy.setText(user.getUid());
        //user.sendEmailVerification(); // WORK!
        //System.err.println(user.updateProfile(new )
        //System.err.println(user.updatePassword());

        String User_ID = user.getUid(); // the unique userID Token (string)
        System.err.println(user.getEmail());
        UserProfileChangeRequest upcg = new UserProfileChangeRequest.Builder().setDisplayName(fname + " " + lname).setPhotoUri(Uri.parse("/test/")).build();
        user.updateProfile(upcg); // WORKS!
        System.err.println(user.getDisplayName()); // don't display asynchronic maybe

      //  FirebaseDBUsers UsersDB = new FirebaseDBUsers();
        FirebaseDBUsers.addUserToDB(User_ID,fname,lname,phoneNumber,false,user.getEmail());
    }

    private void premiumDialog(String fname,String lname) {
        Dialog d= new Dialog(RegisterActivity.this);
        d.setContentView(R.layout.activity_dialog_premium);
        d.setTitle("premium");
        //d.setCancelable(true);

        Button yesButton = d.findViewById(R.id.activity_dialog_yesButton);
        Button noButton = d.findViewById(R.id.activity_dialog_noButton);
        TextView nameTextView = d.findViewById(R.id.activity_dialog_NameTextView);
        nameTextView.setText(nameTextView.getText()+" "+ fname + " " + lname);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                RegisterActivity.super.onBackPressed(); // Go previous page
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveToPremiumPayment();
            }
        });
        d.show();
    }

    public void moveToPremiumPayment(){
        Intent i = new Intent(RegisterActivity.this, PremiumPaymentActivity.class);
        startActivity(i);
    }
}