package com.valorain.playtogether.Fragment;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.valorain.playtogether.Model.Kullanici;
import com.valorain.playtogether.R;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {

   private RecyclerView mRecylerView;
   private View v;

   private FirebaseUser mUser;

   private ArrayList<Kullanici> mKullaniciList;
   private Kullanici mKullanici;

   private Query mQuery;
   private FirebaseFirestore mFireStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_friends, container, false);




        return v;
    }

}