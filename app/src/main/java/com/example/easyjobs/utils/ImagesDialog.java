package com.example.easyjobs.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.R;

import java.util.ArrayList;

public class ImagesDialog {
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
}
