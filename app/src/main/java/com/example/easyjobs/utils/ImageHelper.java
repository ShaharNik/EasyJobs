package com.example.easyjobs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.easyjobs.Objects.Picture;
import com.example.easyjobs.adapters.viewPageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ImageHelper {
    public static void setMultipleImagesInImageView(Uri imageUri, ArrayList<Picture> localFile, viewPageAdapter vpa, Context c)
    {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver(), imageUri);
            File Image = File.createTempFile("Picture", ".jpg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(Image);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            Picture p = new Picture(Image, imageUri.getLastPathSegment());
            localFile.add(p);
            vpa.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setImagePreviewIfExists(ImageView image,ArrayList<Picture> localFile)
    {
        if (localFile.size() > 0) {
            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.get(0).getF().getAbsolutePath());
            image.setImageBitmap(myBitmap);
            image.setVisibility(View.VISIBLE);
            image.setEnabled(true);
        }
    }

    public static void setImagesFromGallery(int resultCode, Intent data,ArrayList<Uri> PicsUri,ImageView image,ArrayList<Picture> localFile,viewPageAdapter vpa, Context c)
    {
        if (resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    PicsUri.add(imageUri);
                    setMultipleImagesInImageView(imageUri, localFile,vpa,c);
                }
                setImagePreviewIfExists(image,localFile);
            }
        } else if (data != null && data.getData() != null) {
            PicsUri.add(data.getData());
            ImageHelper.setMultipleImagesInImageView(data.getData(), localFile,vpa,c);
        }
    }
    public static void pullImagesFromDBandInsertToArray(StorageReference sr,ImageView image,ArrayList<Picture> localFile,viewPageAdapter vpa)
    {
        File Image = null;
        try
        {
            Image = File.createTempFile(sr.getName(), ".jpg");
            File finalImage = Image;
            sr.getFile(finalImage).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task)
                {
                    if (finalImage.exists())
                    {
                        Bitmap myBitmap = BitmapFactory.decodeFile(finalImage.getAbsolutePath());
                        image.setImageBitmap(myBitmap);
                        Picture pic = new Picture(finalImage, sr.getName());
                        localFile.add(pic);
                        vpa.notifyDataSetChanged();
                        image.setEnabled(true);
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
