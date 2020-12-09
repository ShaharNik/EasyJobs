package com.example.easyjobs.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyjobs.Activities.Jobs.JobsListActivity;
import com.example.easyjobs.Activities.RegisterActivity;
import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class dialogHelper {
    public static Dialog ImagesDialogBuilder(Context c, ImageView image, ArrayList<Picture> localFile)
    {
        Dialog d = new Dialog(c);
        d.setContentView(R.layout.view_pager_layout);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.setTitle("Pictures");
        d.setCancelable(true);
        d.setOnDismissListener(dialog -> {
            System.out.println("DISMISS");
            if (localFile.isEmpty()) {
                image.setVisibility(View.INVISIBLE);
                image.setEnabled(false);
            }
        });
        return d;
    }
    public static Dialog loginDialogBuilder(Context c)
    {
        Dialog d = new Dialog(c);
        FirebaseAuth fa=FirebaseAuth.getInstance();
        d.setContentView(R.layout.activity_login);
        d.setTitle("Login");
        d.setCancelable(true);

        ImageView iv = d.findViewById(R.id.back_login);
        iv.setEnabled(false);
        iv.setVisibility(View.GONE);

        EditText ed1 = d.findViewById(R.id.emailEditText);
        EditText ed2 = d.findViewById(R.id.editTextPassword);
        Button log = d.findViewById(R.id.LoginButton);
        Button reg = d.findViewById(R.id.button_login_toregister);
        Button forgotPass = d.findViewById(R.id.resetButton);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validator.ValidateUserEmail(ed1.getText().toString()) && !ed1.getText().toString().isEmpty())
                {
                    fa.sendPasswordResetEmail(ed1.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(c, "נשלח מייל לאיפוס סיסמא", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    ed1.setError("המייל שהזנת אינו תקין");
                }
            }
        });
        log.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String ed1s = ed1.getText().toString();
                String ed2s = ed2.getText().toString();
                if (!ed1s.isEmpty() && !ed2s.isEmpty())
                {
                    fa.signInWithEmailAndPassword(ed1s, ed2s).addOnCompleteListener((Activity) c, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = fa.getCurrentUser();
                                Toast.makeText(c, "התחבר בהצלחה", Toast.LENGTH_SHORT).show();
                                d.dismiss();
                            }
                            else
                            {
                                Toast.makeText(c, "אימות נכשל", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        reg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                d.dismiss();
                Intent i = new Intent(c, RegisterActivity.class);
                c.startActivity(i);
            }
        });
        return d;
    }
}
