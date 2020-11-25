package com.example.easyjobs.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class SizeUtils {
    // Sets the Divider height (between recyclerView items)
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale/6);
    }
}
