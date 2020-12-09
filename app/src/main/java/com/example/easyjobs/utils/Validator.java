package com.example.easyjobs.utils;

import android.widget.SpinnerAdapter;

import com.example.easyjobs.Objects.Category;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator
{
    public static boolean ValidateUserEmail(String email)
    {
        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        // user_emailEditText.setError("Email is invalid");
        return matcher.matches();
    }

    public static boolean ValidateUserPassword(String user_pass)
    {
        // check if password is at least 6
        return user_pass.length() >= 6;
    }

    public static boolean ValidateUserFName(String first_name)
    {
        // check if first name contains only letters
        return first_name.matches("[a-zA-Z]+");
    }

    public static boolean ValidateUserLName(String last_name)
    {
        // check if last name contains only letters
        return last_name.matches("[a-zA-Z]+");
    }

    public static boolean ValidateUserPhone(String phone)
    {
        // check if phone number contains only numbers
        return phone.matches("[0-9]+") && phone.length() == 10;
    }

    public static boolean ValidateIsraeliId(String id)
    {
        return id.matches("[0-9]+") && id.length() == 9;
        /*
        String strId = id.trim();
        if (strId.length() > 9) {
            return false;
        }
        if (strId.length() < 9) {
            while (strId.length() < 9) strId = "0" + strId;
        }
        int counter = 0, rawVal, actualVal;
        for (int i = 0; i < strId.length(); i++) {
            rawVal = strId.charAt(i) * ((i % 2) + 1); // CHECK
            actualVal = rawVal > 9 ? (rawVal - 9) : rawVal;
            counter += actualVal;
        }
        return (counter % 10 == 0);
         */
    }

    public static boolean ValidateDescription(String desc)
    {
        return desc.length() >= 3;
    }

    public static boolean ValidateLocation(String loc)
    {
        return loc.length() >= 3;
    }

    public static boolean ValidatePrice(String price)
    {
        return price.matches("[0-9]+") && price.length() <= 4;
    }

    public static boolean ValidateCategoryName(String catName)
    {
        System.err.println(catName);
        if (catName.length() < 3)
        {
            return false;
        }
        return !catName.matches(("[0-9]+"));
    }

    public static boolean isStringExistsInAdapter(String categoryName, SpinnerAdapter adapter)
    {
        for (int i = 0; i < adapter.getCount(); i++)
        {
            Category cat = (Category) adapter.getItem(i);
            if (cat.getCat_name().compareTo(categoryName) == 0)
            {
                return true;
            }
        }
        return false;
    }
}



