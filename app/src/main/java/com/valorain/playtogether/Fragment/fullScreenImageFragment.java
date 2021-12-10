package com.valorain.playtogether.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.protobuf.StringValue;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.valorain.playtogether.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.AccessController;


public class fullScreenImageFragment extends DialogFragment {

    private static final int REQUEST_CODE = 100;
    private ImageView fullScreenImage,dismissDialog;
    private View  view;
    private Button btnSave;
    private Uri imgSrc;
    private OutputStream outputStream;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);


            btnSave = view.findViewById(R.id.dummy_button);
            dismissDialog = view.findViewById(R.id.close_image_dialog);
            fullScreenImage = view.findViewById(R.id.fullscreen_image);

            //incoming data
             imgSrc = Uri.parse(getArguments().getString("imgSrc"));

                //change imageview image
        Picasso.get().load(imgSrc).into(fullScreenImage);




         btnSave.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {


                 OpenInBrowser();



             }
         });




            //close dialog
        dismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

      return view;
    }

    private void OpenInBrowser() {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(String.valueOf(imgSrc)));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }


}







