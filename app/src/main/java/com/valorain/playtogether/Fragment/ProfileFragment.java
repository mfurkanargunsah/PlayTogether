package com.valorain.playtogether.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class ProfileFragment extends Fragment {


    private static final int IZIN_KODU = 0;
    private static final int IZIN_ALINDI_KODU = 1;
    private EditText edt_userName, edt_Email;
    private ImageView imgProfil;
    private ImageView  newImg;
    private Button btn_edt, btn_cancel, btn_perm;
    private View v;
    private FirebaseUser mUser;
    private FirebaseFirestore mFireStore;
    private DocumentReference mRef;
    private dbUser user;
    private Intent galleryIntent;
    private Uri mUri;
    private Bitmap incomingImg;
    private ImageDecoder.Source imgSource;
    private ByteArrayOutputStream outputStream;
    private byte[] imgByte;
    private StorageReference mStorageRef, newRef,sRef;
    private String storage_place, downloadLink;
    private HashMap<String,Object> mData;
    private String txtUserName;

    private Query mQuery;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);


            edt_userName = v.findViewById(R.id.profil_fragment_edit_user_userName);
        txtUserName = edt_userName.getText().toString();
            edt_Email = v.findViewById(R.id.profil_fragment_edit_user_email);
            imgProfil = v.findViewById(R.id.profil_fragment_user_img_profil);
            newImg = v.findViewById(R.id.profil_fragment_set_newImg);
            btn_edt = v.findViewById(R.id.profil_fragment_edit_button);
            btn_cancel = v.findViewById(R.id.profil_fragment_cancel_button);
            btn_perm = v.findViewById(R.id.profil_fragment_onay_button);

            mUser = FirebaseAuth.getInstance().getCurrentUser();
            mFireStore = FirebaseFirestore.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference();

            mRef = mFireStore.collection("UserList").document(mUser.getUid());

            mRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null){
                                Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (value != null && value.exists()){
                                user = value.toObject(dbUser.class);

                                if (user != null){

                                    edt_userName.setText(user.getUserName());
                                    edt_Email.setText(user.getUserEmail());

                                    if (user.getProfilePics().equals("default"))
                                        imgProfil.setImageResource(R.mipmap.ic_launcher);
                                    else
                                        Picasso.get().load(user.getProfilePics()).into(imgProfil);

                                }
                            }

                }
            });



            newImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions((Activity)v.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},IZIN_KODU);
                    else
                        goToGallery();




                }
            });


            btn_edt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btn_edt.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_perm.setVisibility(View.VISIBLE);
                    newImg.setVisibility(View.VISIBLE);

                    edt_userName.setEnabled(true);


                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_edt.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.GONE);
                    btn_perm.setVisibility(View.GONE);
                    newImg.setVisibility(View.GONE);

                    edt_userName.setEnabled(false);
                    edt_userName.setText(user.getUserName());
                }
            });

            btn_perm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btn_edt.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.GONE);
                    btn_perm.setVisibility(View.GONE);
                    newImg.setVisibility(View.GONE);
                    edt_userName.setEnabled(false);





                    mFireStore.collection("UserList").document(mUser.getUid())
                            .update("userName",edt_userName.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                   nameUpdate(edt_userName);
                                    Toast.makeText(v.getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                }
                            });




                }
            });





        return v;
    }


    private void goToGallery(){

        galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,IZIN_ALINDI_KODU);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IZIN_KODU){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                goToGallery();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode == IZIN_ALINDI_KODU){
                if (resultCode == RESULT_OK && data != null && data.getData() != null){

                    mUri = data.getData();

                    try {
                        if (Build.VERSION.SDK_INT >= 28){
                            imgSource = ImageDecoder.createSource(v.getContext().getContentResolver(),mUri);
                            incomingImg = ImageDecoder.decodeBitmap(imgSource);
                        }
                        else{
                            incomingImg = MediaStore.Images.Media.getBitmap(v.getContext().getContentResolver(),mUri);
                        }

                        outputStream = new ByteArrayOutputStream();
                        incomingImg.compress(Bitmap.CompressFormat.PNG,75,outputStream);
                        imgByte = outputStream.toByteArray();

                        storage_place = "Users/" + user.getUserEmail() + "/profile.jpg";
                     sRef =   mStorageRef.child(storage_place);
                        sRef.putBytes(imgByte)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        newRef = FirebaseStorage.getInstance().getReference(storage_place);
                                        newRef.getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                    downloadLink = uri.toString();
                                                    mData = new HashMap<>();
                                                    mData.put("profilePics", downloadLink);
                                                    mFireStore.collection("UserList").document(mUser.getUid())
                                                            .update(mData)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                            profilGuncelle(downloadLink);
                                                                    }
                                                                    else
                                                                        Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void profilGuncelle(final String link){

        mQuery = mFireStore.collection("ChatRoom").document(mUser.getUid()).collection("Channels");
        mQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.getDocuments().size() > 0){

                            for (DocumentSnapshot snp : queryDocumentSnapshots.getDocuments()){

                                mData = new HashMap<>();
                                mData.put("profilePics",link);
                                mFireStore.collection("ChatRoom").document(snp.getData().get("userID").toString()).collection("Channels").document(mUser.getUid())
                                        .update(mData);
                            }
                            Toast.makeText(v.getContext(), "Updated!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void nameUpdate(final EditText nameUp){
        mQuery = mFireStore.collection("ChatRoom").document(mUser.getUid()).collection("Channels");
        mQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.getDocuments().size() > 0){

                            for (DocumentSnapshot snp : queryDocumentSnapshots.getDocuments()){

                                mData = new HashMap<>();
                                mData.put("userName",edt_userName.getText().toString());

                                mFireStore.collection("ChatRoom").document(snp.getData().get("userID").toString()).collection("Channels").document(mUser.getUid())
                                        .update(mData);
                            }
                            Toast.makeText(v.getContext(), "Updated!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}