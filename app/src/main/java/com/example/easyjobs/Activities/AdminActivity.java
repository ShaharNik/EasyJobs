package com.example.easyjobs.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.SpinnerHelper;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener
{
    private FirebaseAuth mAuth;
    private TextView emailToAdmin;
    private TextView categoryName;
    private Spinner spinnerCA;
    private Button changeToAdmin;
    private Button removeAdmin;
    private Button addToCategory;
    private Button removeFromCategory;
    private ImageView BackButt;
    private String catUID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        findViews();
        activateButtons();
        setUpCategoriesSpinner();
    }

    private void findViews()
    {
        emailToAdmin = findViewById(R.id.emailForAdmin);
        changeToAdmin = findViewById(R.id.changeToAdmin);
        removeAdmin = findViewById(R.id.removeAdminButt);
        spinnerCA = findViewById(R.id.spinnerCat);
        addToCategory = findViewById(R.id.addToCatButt);
        removeFromCategory = findViewById(R.id.editFromCatButt);
        categoryName = findViewById(R.id.catEditText);
        BackButt = findViewById(R.id.back_admin_activity);
        mAuth = FirebaseAuth.getInstance();
    }

    private void activateButtons()
    {
        BackButt.setOnClickListener(this);
        removeFromCategory.setOnClickListener(this);
        addToCategory.setOnClickListener(this);
        changeToAdmin.setOnClickListener(this);
        removeAdmin.setOnClickListener(this);
    }
    @Override
    public void onClick(View ClickedButton) {
        if(BackButt.equals(ClickedButton))
        {
            AdminActivity.super.onBackPressed();
        }
        else if(removeFromCategory.equals(ClickedButton))
        {
            if (!Validator.ValidateCategoryName(categoryName.getText().toString()))
            {
                categoryName.setError("שם הקטגוריה שהכנסת אינה תקינה");
            }
            else
            {
                if (Validator.isStringExistsInAdapter(categoryName.getText().toString(), spinnerCA.getAdapter()))
                {
                    categoryName.setError("שם הקטגוריה כבר קיים");
                }
                else
                {
                    areYouSureDialog("עריכת קטגוריה");
                }
            }
        }
        else if(addToCategory.equals(ClickedButton))
        {
            if (!Validator.ValidateCategoryName(categoryName.getText().toString()))
            {
                categoryName.setError("שם הקטגוריה שהכנסת אינה תקינה");
            }
            else
            {
                if (Validator.isStringExistsInAdapter(categoryName.getText().toString(), spinnerCA.getAdapter()))
                {
                    categoryName.setError("שם הקטגוריה כבר קיים");
                }
                else
                {
                    areYouSureDialog("הוספת קטגוריה");
                }
            }
        }
        else if(changeToAdmin.equals(ClickedButton))
        {
            if (emailToAdmin.getText().toString().isEmpty())
            {
                emailToAdmin.setError("לא הזנת כתובת אימייל");
            }
            if (!Validator.ValidateUserEmail(emailToAdmin.getText().toString()))
            {
                emailToAdmin.setError("האימייל שהזנת אינו תקין!");
            }
            else
            {
                areYouSureDialog("הוספת אדמין חדש");
            }
        }
        else if(removeAdmin.equals(ClickedButton))
        {
            if (!Validator.ValidateUserEmail(emailToAdmin.getText().toString()))
            {
                emailToAdmin.setError("האימייל שהזנת אינו תקין!");
            }
            else
            {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user.getEmail().compareTo(emailToAdmin.getText().toString()) == 0)
                {
                    Toast.makeText(AdminActivity.this, "למה שתמחוק לעצמך הרשאת מנהל?!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    areYouSureDialog("מחיקת אדמין");
                }
            }
        }
    }
    private void areYouSureDialog(String action)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if (action.compareTo("הוספת אדמין חדש") == 0)
                        {
                            FirebaseDBUsers.makeNewAdmin(emailToAdmin.getText().toString());
                            Toast.makeText(AdminActivity.this, "האדמין נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                        }
                        else if (action.compareTo("מחיקת אדמין") == 0)
                        {
                            FirebaseDBUsers.removeAdmin(emailToAdmin.getText().toString());
                            emailToAdmin.setText("");
                            Toast.makeText(AdminActivity.this, "הורדת הרשאת מנהל בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                        }
                        else if (action.compareTo("הוספת קטגוריה") == 0)
                        {
                            FirebaseDBCategories.addCat(categoryName.getText().toString());
                            setUpCategoriesSpinner();
                            Toast.makeText(AdminActivity.this, "הקטגוריה נוספה בהצלחה", Toast.LENGTH_SHORT).show();// maybe better to move to the function
                        }
                        else if (action.compareTo("עריכת קטגוריה") == 0)
                        {
                            FirebaseDBCategories.changeCatName(catUID, categoryName.getText().toString());
                            setUpCategoriesSpinner();
                            Toast.makeText(AdminActivity.this, "הקטגוריה נערכה בהצלחה", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setMessage("האם אתה בטוח?").setPositiveButton("כן", dialogClickListener).setNegativeButton("לא", dialogClickListener).show();
    }

    private void setUpCategoriesSpinner()
    {
        SpinnerHelper.setUpSpinnerWithAllCategories(spinnerCA,this);
        spinnerCA.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
    {
        catUID = ((Category) spinnerCA.getAdapter().getItem(position)).getCategory_id();
        categoryName.setText(((Category) spinnerCA.getAdapter().getItem(position)).getCat_name());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}