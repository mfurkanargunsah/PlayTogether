package com.valorain.playtogether.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;


public class fullScreenImageFragment extends DialogFragment {

    private static int REQUEST_CODE = 100;
    private ImageView fullScreenImage;
    private View  view;
    private Button btnSave;
    File file;
    String dirPath, fileName;
    OutputStream outputStream;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);


            btnSave = view.findViewById(R.id.dummy_button);
            AndroidNetworking.initialize(view.getContext());
            dirPath = Environment.getExternalStorageDirectory()+"/Image";
            fileName = "image.jpeg";
            file = new File(dirPath,fileName);


            fullScreenImage = view.findViewById(R.id.fullscreen_image);
            String imgSrc = getArguments().getString("imgSrc");


        Picasso.get().load(imgSrc).into(fullScreenImage);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AndroidNetworking.download(imgSrc,dirPath,fileName)
                            .build()
                            .startDownload(new DownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Toast.makeText(view.getContext(), "DownLoad Complete", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(ANError anError) {

                                }
                            });

                }
            });


      return view;
    }

}