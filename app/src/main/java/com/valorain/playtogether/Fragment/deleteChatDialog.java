 package com.valorain.playtogether.Fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.valorain.playtogether.R;


public class deleteChatDialog extends DialogFragment {

  private View view;
  private Button btnCancel,btnOnay;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delete_chat_dialog, container, false);


        btnCancel = view.findViewById(R.id.delete_chat_dialog_btnCancel);
        btnOnay = view.findViewById(R.id.delete_chat_dialog_btnDelete);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });









        return view;
    }
}