package com.example.easyjobs;

import android.content.Context;

public class SizeUtils {
    // Sets the Divider height (between recyclerView items)
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale/3);
    }
}
