package com.valorain.playtogether.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;
import com.valorain.playtogether.adapter.ChatAdapter;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int IZIN_KODU = 0;
    private static final int IZIN_ALINDI_KODU = 1;
    private Intent goToGallery;
    private Uri imgUri;
    private String kayitYeri,indirmeLinki;
    private Bitmap imgBitmap;
    private ImageDecoder.Source imgSource;
    private ByteArrayOutputStream outputStream;
    private ProgressDialog mProgress;
    private byte[] imgByte;
    private HashMap<String,Object> mData;
    private RecyclerView mRecyclerView;
    private EditText editMessage;
    private String txtMessage;
    private CircleImageView targetPhoto;
    private TextView TargetName;
    private Intent gelenIntent;
    private String targetID;

    private DocumentReference targetRef;
    private dbUser targetDbUser;
    private FirebaseFirestore mFireStore;
    private FirebaseUser mUser;
    private StorageReference mStorageRef, yeniRef, sRef,downloadRef;

    private LinearLayoutManager mManager;
    private Query chatQuery;
    private ArrayList<Chat> mChatList;
    private Chat mChat;
    private ChatAdapter chatAdapter;
    private String docId;
    private String mUID;
    private String selectGames;


    private void init()
    {
        mRecyclerView = findViewById(R.id.chat_activity_recycleView);
        editMessage = findViewById(R.id.chat_activitiy_edtMessage);
        targetPhoto = findViewById(R.id.chat_activity_image_target_profile_pic);
        TargetName = findViewById(R.id.chat_activity_txt_target_name);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mFireStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        gelenIntent = getIntent();
        targetID = gelenIntent.getStringExtra("targetID");
        selectGames = gelenIntent.getStringExtra("selectGame");
        System.out.println(selectGames);
        mUID = mUser.getUid();





        mChatList = new ArrayList<>();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mProgress = new ProgressDialog(ChatActivity.this);
        mProgress.setTitle("Sending..");

        init();
        targetRef = mFireStore.collection("UserList").document(targetID);
        System.out.println(targetID);
        targetRef.addSnapshotListener(this, (value, error) -> {

                if (selectGames != null)
            mFireStore.collection("Matching Room").document(selectGames).collection("Users").document(mUID).delete();

            if (error != null)
            {
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null && value.exists()) {

                targetDbUser = value.toObject(dbUser.class);

                if (targetDbUser != null){
                    TargetName.setText(targetDbUser.getUserName());
                    if (targetDbUser.getProfilePics().equals("default"))
                        targetPhoto.setImageResource(R.mipmap.ic_launcher);
                    else
                        Picasso.get().load(targetDbUser.getProfilePics()).into(targetPhoto);

                }

            }
        });

        mRecyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);




        //Gönderen
        chatQuery = mFireStore.collection("Message Room").document(targetID).collection("ChatList").document("Receiver").collection(mUID)
                .orderBy("messageDate",Query.Direction.ASCENDING);
        chatQuery.addSnapshotListener(this, (value, error) -> {
            if (error != null){

                Toast.makeText(ChatActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            if(value != null){
                mChatList.clear();

                for (DocumentSnapshot snapshot : value.getDocuments()){
                    mChat = snapshot.toObject(Chat.class);
                    assert mChat != null;
                    mChatList.add(mChat);

                }
                chatAdapter = new ChatAdapter(mChatList,ChatActivity.this,mUID);
                mRecyclerView.setAdapter(chatAdapter);


            }
        });
    }



    public void btnMesajGonder(View view){

        txtMessage = editMessage.getText().toString();
        if (!TextUtils.isEmpty(txtMessage)){
            docId = UUID.randomUUID().toString();

            mData = new HashMap<>();
            mData.put("userMessage", txtMessage);
            mData.put("sender",mUser.getUid());
            mData.put("receiver", targetID);
            mData.put("messageType","text");
            mData.put("messageDate", FieldValue.serverTimestamp());
            mData.put("docId",docId);
            mData.put("imgProfil", targetDbUser.getProfilePics());


            mFireStore.collection("Message Room").document(targetID).collection("ChatList").document("Receiver").collection(mUID).document(docId)
                    .set(mData)
                    .addOnCompleteListener(ChatActivity.this, task -> {

                        if (task.isSuccessful()) {
                            editMessage.setText("");
                            progressAyar();
                        }
                        else
                            Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    });

            mFireStore.collection("Message Room").document(mUID).collection("ChatList").document("Receiver").collection(targetID).document(docId)
                    .set(mData)
                    .addOnCompleteListener(ChatActivity.this, task -> {

                        if (task.isSuccessful()) {

                            editMessage.setText("");
                            progressAyar();
                        }
                        else
                            Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    });

        }else
            Toast.makeText(ChatActivity.this,"Empty Message Cannot Be Sent",Toast.LENGTH_SHORT).show();


    }

    public void btnChatBack(View view){
        finish();



    }



    public void btnGaleridenResimGonder(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},IZIN_KODU);
        }else
            galeriIntent();
    }

    private void galeriIntent(){

        goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(goToGallery,IZIN_ALINDI_KODU);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IZIN_KODU){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                galeriIntent();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IZIN_ALINDI_KODU){
            if (resultCode == RESULT_OK && data != null && data.getData() != null){
                imgUri = data.getData();

                try {
                    if(Build.VERSION.SDK_INT >= 28){
                        imgSource = ImageDecoder.createSource(this.getContentResolver(),imgUri);
                        imgBitmap = ImageDecoder.decodeBitmap(imgSource);
                    }else{
                        imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgUri);
                    }
                    outputStream = new ByteArrayOutputStream();
                    imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    imgByte = outputStream.toByteArray();

                    kayitYeri = "ChatImages/" + mUID + "/" + targetID + "/" + mUID + System.currentTimeMillis() + ".png";
                    sRef = mStorageRef.child(kayitYeri);
                    sRef.putBytes(imgByte)
                            .addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    mProgress.show();
                                    yeniRef = FirebaseStorage.getInstance().getReference(kayitYeri);
                                    yeniRef.getDownloadUrl()
                                            .addOnSuccessListener(ChatActivity.this, new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    indirmeLinki = uri.toString();
                                                    docId = UUID.randomUUID().toString();

                                                    mData = new HashMap<>();
                                                    mData.put("userMessage",indirmeLinki);
                                                    mData.put("sender",mUser.getUid());
                                                    mData.put("receiver", targetID);
                                                    mData.put("messageType","Image");
                                                    mData.put("messageDate", FieldValue.serverTimestamp());
                                                    mData.put("docId",docId);
                                                    // mData.put("userName",hedefDbUser.getUserID());
                                                    mData.put("imgProfil", targetDbUser.getProfilePics());


                                                    mFireStore.collection("Message Room").document(targetID).collection("ChatList").document("Receiver").collection(mUID).document(docId)
                                                            .set(mData)
                                                            .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {


                                                                        editMessage.setText("");
                                                                        progressAyar();
                                                                    }
                                                                    else
                                                                        Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    mFireStore.collection("Message Room").document(mUID).collection("ChatList").document("Receiver").collection(targetID).document(docId)
                                                            .set(mData)
                                                            .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        editMessage.setText("");
                                                                        progressAyar();
                                                                    }
                                                                    else
                                                                        Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                }
                                            }).addOnFailureListener(ChatActivity.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                            progressAyar();
                                        }
                                    });


                                }
                            }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            progressAyar();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void progressAyar(){

        if (mProgress.isShowing())
            mProgress.dismiss();
    }




}