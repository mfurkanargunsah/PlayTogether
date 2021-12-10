 package com.valorain.playtogether.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.valorain.playtogether.R;


public class deleteChatDialog extends DialogFragment {

  private View view;
  private Button btnCancel,btnOnay;
  private FirebaseUser mUser;
  private FirebaseFirestore mStore;
  private FirebaseAuth mAuth;
  private String gelenKpos;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delete_chat_dialog, container, false);

        gelenKpos = getArguments().getString("targetID");
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStore = FirebaseFirestore.getInstance();

        btnCancel = view.findViewById(R.id.delete_chat_dialog_btnCancel);
        btnOnay = view.findViewById(R.id.delete_chat_dialog_btnDelete);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

        btnOnay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStore.collection("ChatRoom").document(mUser.getUid()).collection("Channels").document(gelenKpos).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Chat Deleted.",Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        });









        return view;
    }
}