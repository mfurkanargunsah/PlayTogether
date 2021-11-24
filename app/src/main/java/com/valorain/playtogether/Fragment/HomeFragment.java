package com.valorain.playtogether.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.QuerySnapshot;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.Model.Kullanici;
import com.valorain.playtogether.R;
import com.valorain.playtogether.View.ChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class HomeFragment extends Fragment {

    private Kullanici user;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mStore;
    private DocumentReference mRef;
    private Kullanici mKullanici;
    private Button btnStart;
    private ArrayList<String> mKullaniciList;
    private String mUID;

    private HashMap<String,Object> mData;
    private Intent chatIntent;
    private String channelId;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //dialog tanım
        btnStart = view.findViewById(R.id.startMatch);


        //firebase tanım
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
        mRef = mStore.collection("Kullanıcılar").document(mUser.getUid());


        //Game Select Menu
        Spinner gameSelect = view.findViewById(R.id.spinner_game_list);
        ArrayAdapter<CharSequence> adapterGame;
        adapterGame = ArrayAdapter.createFromResource(getContext(), R.array.game_list, R.layout.color_spinner_layout);
        adapterGame.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameSelect.setAdapter(adapterGame);

        gameSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                if (i == 0) {

                }
                if (i == 1) {

                }
                if (i == 2) {

                }
                if (i == 3) {

                }
                if (i == 4) {

                }
                if (i == 5) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        //Gender Select Menu
        Spinner genderSelect = view.findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapterGender;
        adapterGender = ArrayAdapter.createFromResource(getContext(), R.array.gender_list, R.layout.color_spinner_layout);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSelect.setAdapter(adapterGender);

        genderSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterGender, View view, int i, long l) {


                if (i == 0) {

                }
                if (i == 1) {

                }
                if (i == 2) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        //nonpremium spinner
        Spinner nonPre = view.findViewById(R.id.spinner_no_premium);
        ArrayAdapter<CharSequence> adapterNP;
        adapterNP = ArrayAdapter.createFromResource(getContext(), R.array.non_premium, R.layout.color_spinner_layout);
        nonPre.setAdapter(adapterNP);

        ///
        mRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    return;
                }

                if (value != null && value.exists()) {
                    user = value.toObject(Kullanici.class);

                    if (user != null) {
                        System.out.println(user.getStatus());
                        System.out.println(user.isPremium());
                        TextView showError = view.findViewById(R.id.genderPremiumError);
                        if (user.isPremium() == false) {


                            showError.setVisibility(View.VISIBLE);
                            mRef.update("status", "Free");
                            nonPre.setVisibility(View.VISIBLE);
                            genderSelect.setVisibility(View.GONE);
                        } else if (user.isPremium() == true) {
                            showError.setVisibility(View.GONE);
                            mRef.update("status", "Premium");
                            nonPre.setVisibility(View.GONE);
                            genderSelect.setVisibility(View.VISIBLE);


                        }


                    }
                }


            }
        });


        mKullaniciList = new ArrayList<String>();
        //kullanıcı listesi çekme


        if (!(mUser == null)) {
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    //dialog penceresini aç
                    DialogFragment mDialogFragment = new DialogFragment();
                    mDialogFragment.show(getChildFragmentManager(), "mDialogFragment");



                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {




                            mData = new HashMap<>();
                            mData.put("cins",user.getCins());
                            mData.put("kullaniciAdi",user.getKullaniciAdi());
                            mData.put("kullaniciProfil",user.getKullaniciProfil());
                            mData.put("userID",user.getUserID());
                            mStore.collection("Eşleşme Odası").document(mUser.getUid()).set(mData);




                    //veri getir
                    mStore.collection("Eşleşme Odası")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {



                                    if (value != null) {

                                        mKullaniciList.clear();
                                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                                            mKullanici = snapshot.toObject(Kullanici.class);
                                            if (mKullanici != null) {

                                                mKullaniciList.add(mKullanici.getUserID().toString());

                                            }
                                        }
                                        if (mKullaniciList.size() < 2){
                                            return;
                                        }
                                        if (mUser.getUid().equals(mKullaniciList.get(0))) {
                                            mStore.collection("ChatRoom").document(mKullaniciList.get(1)).collection("Kanallar").document(mUser.getUid()).set(mData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            chatIntent = new Intent(view.getContext(), ChatActivity.class);

                                                            channelId = UUID.randomUUID().toString();
                                                            chatIntent.putExtra("channelId",channelId);
                                                            chatIntent.putExtra("hedefId", mKullaniciList.get(1));


                                                            mDialogFragment.dismiss();



                                                            chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(chatIntent);

                                                        }
                                                    });
                                        }
                                         if(mUser.getUid().equals(mKullaniciList.get(1))){
                                            mStore.collection("ChatRoom").document(mKullaniciList.get(0)).collection("Kanallar").document(mUser.getUid()).set(mData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            chatIntent = new Intent(view.getContext(), ChatActivity.class);
                                                            channelId = UUID.randomUUID().toString();
                                                            chatIntent.putExtra("channelId",channelId);
                                                            chatIntent.putExtra("hedefId", mKullaniciList.get(0));
                                                            mDialogFragment.dismiss();


                                                            chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(chatIntent);

                                                        }
                                                    });

                                        }





                                    }
                                }
                            });
                        }
                    }.start();
                }
            });
        }


        return view;


    }

}
