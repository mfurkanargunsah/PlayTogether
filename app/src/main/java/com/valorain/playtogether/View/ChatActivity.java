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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.Model.Kullanici;
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
    private Intent galeriyeGit;
    private Uri imgUri;
    private String kayitYeri,indirmeLinki;
    private Bitmap imgBitmap;
    private ImageDecoder.Source imgSource;
    private ByteArrayOutputStream outputStream;
    private ProgressDialog mProgress;
    private byte[] imgByte;
    private HashMap<String,Object> mData;
    private RecyclerView mRecyclerView;
    private EditText editMesaj;
    private String txtMesaj;
    private CircleImageView targetPhoto;
    private TextView TargetName;
    private Intent gelenIntent;
    private String hedefId;

    private DocumentReference hedefRef;
    private Kullanici hedefKullanici;
    private FirebaseFirestore mFireStore;
    private String uidadapter;
    private FirebaseUser mUser;
    private StorageReference mStorageRef, yeniRef, sRef,downloadRef;

    private LinearLayoutManager mManager;
    private Query chatQuery;
    private ArrayList<Chat> mChatList;
    private Chat mChat;
    private ChatAdapter chatAdapter;
    private String docId;
    private String mUID;
    private String kanalId;
    private String selectGames;


    private void init()
    {
        mRecyclerView = findViewById(R.id.chat_activity_recycleView);
        editMesaj = findViewById(R.id.chat_activitiy_edtMessage);
        targetPhoto = findViewById(R.id.chat_activity_image_target_profile_pic);
        TargetName = findViewById(R.id.chat_activity_txt_target_name);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mFireStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        gelenIntent = getIntent();
        hedefId = gelenIntent.getStringExtra("hedefId");
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
        mProgress.setTitle("Gönderiliyor...");

        init();
        hedefRef = mFireStore.collection("Kullanıcılar").document(hedefId);
        System.out.println(hedefId);
        hedefRef.addSnapshotListener(this, (value, error) -> {

                if (selectGames != null)
            mFireStore.collection("Eşleşme Odası").document(selectGames).collection("Kullanıcılar").document(mUID).delete();

            if (error != null)
            {
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null && value.exists()) {

                hedefKullanici = value.toObject(Kullanici.class);

                if (hedefKullanici != null){
                    TargetName.setText(hedefKullanici.getKullaniciAdi());
                    if (hedefKullanici.getKullaniciProfil().equals("default"))
                        targetPhoto.setImageResource(R.mipmap.ic_launcher);
                    else
                        Picasso.get().load(hedefKullanici.getKullaniciProfil()).into(targetPhoto);

                }

            }
        });

        mRecyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);




        //Gönderen
        chatQuery = mFireStore.collection("Sohbet Kanalları").document(hedefId).collection("ChatList").document("Alıcı").collection(mUID)
                .orderBy("mesajTarihi",Query.Direction.ASCENDING);
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
                chatAdapter = new ChatAdapter(mChatList,ChatActivity.this,mUID,hedefId,docId);
                mRecyclerView.setAdapter(chatAdapter);


            }
        });
    }



    public void btnMesajGonder(View view){

        txtMesaj = editMesaj.getText().toString();
        if (!TextUtils.isEmpty(txtMesaj)){
            docId = UUID.randomUUID().toString();

            mData = new HashMap<>();
            mData.put("mesajIcerigi",txtMesaj);
            mData.put("gonderen",mUser.getUid());
            mData.put("alici",hedefId);
            mData.put("mesajTipi","text");
            mData.put("mesajTarihi", FieldValue.serverTimestamp());
            mData.put("docId",docId);
            mData.put("imgProfil",hedefKullanici.getKullaniciProfil());


            mFireStore.collection("Sohbet Kanalları").document(hedefId).collection("ChatList").document("Alıcı").collection(mUID).document(docId)
                    .set(mData)
                    .addOnCompleteListener(ChatActivity.this, task -> {

                        if (task.isSuccessful()) {
                            editMesaj.setText("");
                            progressAyar();
                        }
                        else
                            Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    });

            mFireStore.collection("Sohbet Kanalları").document(mUID).collection("ChatList").document("Alıcı").collection(hedefId).document(docId)
                    .set(mData)
                    .addOnCompleteListener(ChatActivity.this, task -> {

                        if (task.isSuccessful()) {

                            editMesaj.setText("");
                            progressAyar();
                        }
                        else
                            Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    });

        }else
            Toast.makeText(ChatActivity.this,"Boş Mesaj Gönderilemez",Toast.LENGTH_SHORT).show();


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

        galeriyeGit = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeriyeGit,IZIN_ALINDI_KODU);
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

                    kayitYeri = "ChatImages/" + mUID + "/" + hedefId + "/" + mUID + System.currentTimeMillis() + ".png";
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
                                                    mData.put("mesajIcerigi",indirmeLinki);
                                                    mData.put("gonderen",mUser.getUid());
                                                    mData.put("alici",hedefId);
                                                    mData.put("mesajTipi","resim");
                                                    mData.put("mesajTarihi", FieldValue.serverTimestamp());
                                                    mData.put("docId",docId);
                                                    // mData.put("userName",hedefKullanici.getUserID());
                                                    mData.put("imgProfil",hedefKullanici.getKullaniciProfil());


                                                    mFireStore.collection("Sohbet Kanalları").document(hedefId).collection("ChatList").document("Alıcı").collection(mUID).document(docId)
                                                            .set(mData)
                                                            .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {


                                                                        editMesaj.setText("");
                                                                        progressAyar();
                                                                    }
                                                                    else
                                                                        Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    mFireStore.collection("Sohbet Kanalları").document(mUID).collection("ChatList").document("Alıcı").collection(hedefId).document(docId)
                                                            .set(mData)
                                                            .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        editMesaj.setText("");
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