package com.valorain.playtogether.Fragment;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.valorain.playtogether.Model.Kullanici;
import com.valorain.playtogether.R;

import java.util.HashMap;
import java.util.Objects;

public class DialogFragment extends androidx.fragment.app.DialogFragment {


    private View v;
    private  ImageView imgClose;
    private  Button canceled;
    private FirebaseFirestore mStore;
    private FirebaseUser mUser;
    private Kullanici userc;
    private DocumentReference mRef;
    private TextView txtInfo;
    private HashMap<String, Object> coinData;
    private LinearLayout mLinear;
    private String gelenVeri,gelenCins;
    private  int sayac = 5, oyunsayac = 0;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //
        v = inflater.inflate(R.layout.dialog_fragment,container,false);

        assert getArguments() != null;
        gelenVeri = getArguments().getString("SelectGame");

        //FireBase
        mStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mStore.collection("Kullanıcılar").document(mUser.getUid());


        Objects.requireNonNull(getDialog()).setCancelable(false);

        mLinear = v.findViewById(R.id.dialog_fragment_background);
        txtInfo = v.findViewById(R.id.dialog_fragment_textV);
        imgClose = v.findViewById(R.id.close_dialog);
        gameSelect();

        ///
        mRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value != null && value.exists()) {
                    userc = value.toObject(Kullanici.class);

                    if (userc != null) {



                    }}}
        });


        imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        mStore.collection("Eşleşme Odası").document(gelenVeri).collection("Kullanıcılar").document(mUser.getUid())
                                .delete();

                        imgClose.setVisibility(View.GONE);
                            onStop();
                    }
        });

                canceled = v.findViewById(R.id.match_cancel);
                canceled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        canceled.setVisibility(View.GONE);


                            //updateCoin
                        coinData = new HashMap<>();
                        coinData.put("userCoin",userc.getUserCoin() + 100);
                        mStore.collection("Kullanıcılar").document(mUser.getUid())
                                .update(coinData);


                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long l) {
                               sayac = sayac-1;
                                    txtInfo.setText("İptal Ediliyor Lütfen Bekleyin..."+" " +sayac);
                            }

                            @Override
                            public void onFinish() {

                              //   mStore.collection("Eşleşme Odası").document(mUser.getUid())
                                mStore.collection("Eşleşme Odası").document(gelenVeri).collection("Kullanıcılar").document(mUser.getUid())
                                        .delete();
                                canceled.setVisibility(View.GONE);

                                onStop();
                            }
                        }.start();
                    }
                });



            return v;
    }



    private void gameSelect(){

            if(gelenVeri.equals("LeaugeOfLegends")){

                mLinear.setBackgroundResource(R.drawable.dialog_lol);
            }else
                if (gelenVeri.equals("Valorant")){

                    mLinear.setBackgroundResource(R.drawable.dialog_valorant);
                }else
                    if (gelenVeri.equals("CSGO")){
                        mLinear.setBackgroundResource(R.drawable.dialog_csgo);
                    }else
                        if (gelenVeri.equals("Pubg")){
                            mLinear.setBackgroundResource(R.drawable.dialog_pubg);
                        }else
                            if (gelenVeri.equals("Dota2")){
                                mLinear.setBackgroundResource(R.drawable.dialog_dota2);
                            }

    }



}
