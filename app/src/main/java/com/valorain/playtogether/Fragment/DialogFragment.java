package com.valorain.playtogether.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.valorain.playtogether.R;

public class DialogFragment extends androidx.fragment.app.DialogFragment {



    private  ImageView imgClose;
    private  Button canceled;
    private FirebaseFirestore mStore;
    private FirebaseUser mUser;
    private TextView txtInfo;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);



        //FireBase
        mStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        //
        View v = inflater.inflate(R.layout.dialog_fragment,container,false);

        getDialog().setCancelable(false);


        txtInfo = v.findViewById(R.id.dialog_fragment_textV);
        imgClose = v.findViewById(R.id.close_dialog);
        imgClose.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                        mStore.collection("Eşleşme Odası").document(mUser.getUid())
                                .delete();


                            onStop();
                    }
        });

                canceled = v.findViewById(R.id.match_cancel);
                canceled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long l) {
                                    txtInfo.setText("İptal Ediliyor Lütfen Bekleyin...");
                            }

                            @Override
                            public void onFinish() {

                                mStore.collection("Eşleşme Odası").document(mUser.getUid())
                                        .delete();

                                onStop();
                            }
                        }.start();
                    }
                });



            return v;
    }
}
