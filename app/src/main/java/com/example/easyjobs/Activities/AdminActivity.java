package com.example.easyjobs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyjobs.Objects.Category;
import com.example.easyjobs.R;
import com.example.easyjobs.dataBase.FirebaseDBCategories;
import com.example.easyjobs.dataBase.FirebaseDBUsers;
import com.example.easyjobs.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        findViews();

        activateBackButt();
        changeUserToAdmin();
        removeAdmin();
        setUpCategoriesSpinner();
        addNewCategory();
        editCategory();
    }

    private void activateBackButt() {
        BackButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               AdminActivity.super.onBackPressed();
            }
        });
    }

    private void editCategory() {
        removeFromCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validator.ValidateCategoryName(categoryName.getText().toString()))
                {
                    categoryName.setError("שם הקטגוריה שהכנסת אינה תקינה");
                }
                else {
                    if (Validator.isStringExistsInAdapter(categoryName.getText().toString(), spinnerCA.getAdapter()))
                    {
                        categoryName.setError("שם הקטגוריה כבר קיים");
                    }
                    else {
                        FirebaseDBCategories.changeCatName(catUID, categoryName.getText().toString());
                        setUpCategoriesSpinner();
                        Toast.makeText(AdminActivity.this, "הקטגוריה נערכה בהצלחה", Toast.LENGTH_SHORT).show(); // maybe better to move to the function
                    }
                }
            }
        });
    }

    private void addNewCategory() {
        addToCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validator.ValidateCategoryName(categoryName.getText().toString()))
                {
                    categoryName.setError("שם הקטגוריה שהכנסת אינה תקינה");
                }
                else {
                    if (Validator.isStringExistsInAdapter(categoryName.getText().toString(), spinnerCA.getAdapter()))
                    {
                        categoryName.setError("שם הקטגוריה כבר קיים");
                    }
                    else {
                        FirebaseDBCategories.addCat(categoryName.getText().toString());
                        setUpCategoriesSpinner();
                        Toast.makeText(AdminActivity.this, "הקטגוריה נוספה בהצלחה", Toast.LENGTH_SHORT).show();// maybe better to move to the function
                    }
                }
            }
        });
    }

    private void changeUserToAdmin()
    {
        changeToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailToAdmin.getText().toString().isEmpty())
                    emailToAdmin.setError("לא הזנת כתובת אימייל");
                if (!Validator.ValidateUserEmail(emailToAdmin.getText().toString())) {
                    emailToAdmin.setError("האימייל שהזנת אינו תקין!");
                }
                else {
                    FirebaseDBUsers.makeNewAdmin(emailToAdmin.getText().toString());
                    Toast.makeText(AdminActivity.this, "האדמין נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                }
            }});
    }
    private void removeAdmin()
    {
        removeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailToAdmin.setText("");
                if (!Validator.ValidateUserEmail(emailToAdmin.getText().toString())) {
                    emailToAdmin.setError("האימייל שהזנת אינו תקין!");
                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.getEmail().compareTo(emailToAdmin.getText().toString()) == 0)
                    {
                        Toast.makeText(AdminActivity.this, "למה שתמחוק לעצמך הרשאת מנהל?!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // TODO dialog "are you sure?"
                        FirebaseDBUsers.removeAdmin(emailToAdmin.getText().toString());
                        Toast.makeText(AdminActivity.this, "הורדת הרשאת מנהל בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
    }

    private void setUpCategoriesSpinner(){
        DatabaseReference dr = FirebaseDBCategories.getAllCat();
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> items = new ArrayList<>();
                for(DataSnapshot category : snapshot.getChildren()){
                    Category c = category.getValue(Category.class);
                    items.add(c);
                }
                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(AdminActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                spinnerCA.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        spinnerCA.setOnItemSelectedListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        catUID = ((Category)spinnerCA.getAdapter().getItem(position)).getCategory_id();
        // Setting the EditText text to the Category name
        categoryName.setText(((Category)spinnerCA.getAdapter().getItem(position)).getCat_name());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}