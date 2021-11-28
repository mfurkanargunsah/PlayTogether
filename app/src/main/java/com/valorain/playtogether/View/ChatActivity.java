package com.valorain.playtogether.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
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
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Fragment.ChatFragment;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.Model.Kullanici;
import com.valorain.playtogether.R;
import com.valorain.playtogether.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

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

    private LinearLayoutManager mManager;
    private Query chatQuery;
    private ArrayList<Chat> mChatList;
    private Chat mChat;
    private ChatAdapter chatAdapter;
    private String docId;
    private String mUID;
    private String kanalId;



    private void init()
    {
        mRecyclerView = findViewById(R.id.chat_activity_recycleView);
        editMesaj = findViewById(R.id.chat_activitiy_edtMessage);
        targetPhoto = findViewById(R.id.chat_activity_image_target_profile_pic);
        TargetName = findViewById(R.id.chat_activity_txt_target_name);

        mFireStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        gelenIntent = getIntent();
        hedefId = gelenIntent.getStringExtra("hedefId");
        mUID = mUser.getUid();



        mChatList = new ArrayList<>();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        hedefRef = mFireStore.collection("Kullanıcılar").document(hedefId);
        System.out.println(hedefId);
        hedefRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                            mFireStore.collection("Eşleşme Odası").document(hedefId).delete();

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
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);


        //Gönderen
        chatQuery = mFireStore.collection("Sohbet Kanalları").document(hedefId).collection("ChatList").document("Alıcı").collection(mUID)
                .orderBy("mesajTarihi",Query.Direction.ASCENDING);
        chatQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
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
               // mData.put("userName",hedefKullanici.getUserID());
                mData.put("imgProfil",hedefKullanici.getKullaniciProfil());


                mFireStore.collection("Sohbet Kanalları").document(hedefId).collection("ChatList").document("Alıcı").collection(mUID).document(docId)
                        .set(mData)
                        .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                    editMesaj.setText("");
                                else
                                    Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                mFireStore.collection("Sohbet Kanalları").document(mUID).collection("ChatList").document("Alıcı").collection(hedefId).document(docId)
                        .set(mData)
                        .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                    editMesaj.setText("");
                                else
                                    Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }else
                Toast.makeText(ChatActivity.this,"Boş Mesaj Gönderilemez",Toast.LENGTH_SHORT).show();


    }

    public void btnChatBack(View view){
        finish();

    }



}