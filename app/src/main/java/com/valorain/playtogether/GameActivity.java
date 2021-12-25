package com.valorain.playtogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.valorain.playtogether.View.LoginActivity;
import com.valorain.playtogether.View.MainActivity;
import com.valorain.playtogether.adapter.SliderAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity {



    private ViewPager2 viewPager2;
    private ArrayList<dbUser> mArrayList;
    private FirebaseFirestore mFireStore;
    private Query mQuery;
    private dbUser mDbUser;
    private FirebaseUser mUser;
    private Button btnIgnore;
    private String ignoredUser,approvedUser,ItemName,targetUserUID;
    private int pagerPos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


       btnIgnore = findViewById(R.id.settingsFragment_ignore_button);
       viewPager2 = findViewById(R.id.viewPagerImageSlider);
        mFireStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        //kicker
        if (mUser == null){
            startActivity(new Intent(GameActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }



        mArrayList = new ArrayList<dbUser>();

        mQuery = mFireStore.collection("UserList");
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(GameActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null){

                    mArrayList.clear();


                    for (DocumentSnapshot snapshot : value.getDocuments()){
                        mDbUser = snapshot.toObject(dbUser.class);

                        if (mDbUser != null) {

                            if (!mDbUser.getUserID().equals(mUser.getUid()))
                                mArrayList.add(mDbUser);


                        }

                    }
                    Collections.shuffle(mArrayList,new Random(System.nanoTime()));
                    viewPager2.setAdapter(new SliderAdapter(mArrayList,viewPager2));
                    viewPager2.setClipToPadding(false);
                    viewPager2.setClipChildren(false);
                    viewPager2.setOffscreenPageLimit(5);
                    viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                    compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                        @Override
                        public void transformPage(@NonNull View page, float position) {
                            float r = 1 - Math.abs(position);
                            page.setScaleY(0.85f + r  * 0.15f);
                        }
                    });
                    viewPager2.setPageTransformer(compositePageTransformer);

                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            targetUserUID = mArrayList.get(position).getUserID();
                            pagerPos = position;

                            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        }

                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                            super.onPageScrollStateChanged(state);
                        }
                    });

                    btnIgnore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivity(new Intent(GameActivity.this, MainActivity.class));



                        }
                    });



                }

            }
        });


    }

}