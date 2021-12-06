package com.valorain.playtogether.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.valorain.playtogether.R;

import java.util.Objects;

public class DialogFragment extends androidx.fragment.app.DialogFragment {



    private  ImageView imgClose;
    private  Button canceled;
    private FirebaseFirestore mStore;
    private FirebaseUser mUser;
    private TextView txtInfo;
    private LinearLayout mLinear;
    private String gelenVeri,gelenCins;
    private  int sayac = 5, oyunsayac = 0;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        assert getArguments() != null;
        gelenVeri = getArguments().getString("SelectGame");

        //FireBase
        mStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        //
        View v = inflater.inflate(R.layout.dialog_fragment,container,false);

        Objects.requireNonNull(getDialog()).setCancelable(false);

        mLinear = v.findViewById(R.id.dialog_fragment_background);
        txtInfo = v.findViewById(R.id.dialog_fragment_textV);
        imgClose = v.findViewById(R.id.close_dialog);
        gameSelect();


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
