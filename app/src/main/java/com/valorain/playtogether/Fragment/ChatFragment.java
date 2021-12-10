package com.valorain.playtogether.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;
import com.valorain.playtogether.adapter.MesajlarAdapter;

import java.util.ArrayList;

public class ChatFragment extends Fragment {


    private View v;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFireStore;
    private Query mQuery;
    private ArrayList<dbUser> mArrayList;
    private dbUser mDbUser;
    private MesajlarAdapter mesajlarAdapter; //Messages Adapter
    private FirebaseUser mUser;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_chat, container, false);

        mFireStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mArrayList = new ArrayList<dbUser>();

        mRecyclerView = v.findViewById(R.id.chat_fragment_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(),LinearLayoutManager.VERTICAL,false));


        mQuery = mFireStore.collection("ChatRoom").document(mUser.getUid()).collection("Channels");
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null){

                    mArrayList.clear();


                    for (DocumentSnapshot snapshot : value.getDocuments()){
                        mDbUser = snapshot.toObject(dbUser.class);

                        if (mDbUser != null) {
                            mArrayList.add(mDbUser);


                        }

                    }

                    mesajlarAdapter = new MesajlarAdapter(mArrayList, v.getContext());
                    mRecyclerView.setAdapter(mesajlarAdapter);



                }

            }
        });







        return  v;
    }
}