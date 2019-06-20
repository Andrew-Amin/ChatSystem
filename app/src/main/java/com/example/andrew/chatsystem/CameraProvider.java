package com.example.andrew.chatsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("Registered")
public  class CameraProvider extends FileProvider {
    private Context context ;
    private Uri pictureUri;

    CameraProvider(Context context) {this.context = context ;}

    private String createImageName()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "IMG_"+timeStamp ;
    }

    public Intent dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File pictureDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String pictureName = createImageName();
        File imageFile = new File(pictureDirectory, pictureName);
        pictureUri = getUriForFile(context , "com.example.andrew.chatsystem",imageFile);
        takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, pictureUri);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return takePictureIntent ;
    }

    public Uri getPictureUri ()
    { return pictureUri ; }
}
