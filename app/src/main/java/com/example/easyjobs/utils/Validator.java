package com.example.easyjobs.utils;

import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean ValidateUserEmail(String email) {
        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            // user_emailEditText.setError("Email is invalid");
            return false;
        }
        return true;
    }

    public static boolean ValidateUserPassword(String user_pass)
    {
        // check if password is at least 6
        if (user_pass.length() < 6) {
            return false;
        }
        return true;
    }
    public static boolean ValidateUserFName (String first_name)
    {
        // check if first name contains only letters
        if (!first_name.matches("[a-zA-Z]+")) {
            //fname.setError("First name is invaild, can't contain digits");
            return false;
        }
        return true;
    }
    public static boolean ValidateUserLName (String last_name)
    {
        // check if last name contains only letters
        if (!last_name.matches("[a-zA-Z]+")) {
            //lname.setError("Last name is invaild, can't contain digits");
            return false;
        }
        return true;
    }
    public static boolean ValidateUserPhone (String phonenum)
    {
        // check if phone number contains only numbers
        if (!phonenum.matches("[0-9]+") || phonenum.length() != 10) {
            //phone.setError("Phone must contain only digits, and 10 digits");
            return false;
        }
        return true;
    }
    public static boolean ValidateIsraeliId(String id)
    {
        if (!id.matches("[0-9]+") || id.length() != 9)
            return false;
        return true;
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
        if (desc.length() < 3)
            return false;
        return true;
    }

    public static boolean ValidateLocation(String loc) {
        if (loc.length() < 3)
            return false;
        return true;
    }

    public static boolean ValidatePrice(String price)
    {
        if (!price.matches("[0-9]+") || price.length() > 4)
            return false;
        return true;
    }
}



