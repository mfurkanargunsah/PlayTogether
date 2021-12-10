package com.valorain.playtogether.Fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;


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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;
import com.valorain.playtogether.View.ChatActivity;
import com.valorain.playtogether.utility.NetworkChangeList;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.UUID;


public class HomeFragment extends Fragment {
    private View view;
    private dbUser user;
    private FirebaseUser mUser;
    private FirebaseFirestore mStore;
    private DocumentReference mRef;
    private dbUser mDbUser;
    private ArrayList<String> mUserList;
    private HashMap<String, Object> mData,coinData;
    private Intent chatIntent;
    private String channelId, selectedGame = null, selectGender = "Random";
    private final Bundle bundle = new Bundle();
    private TextView showError;
    private DialogFragment mDialogFragment;

    //spinners
    private Spinner gameSelect;
    private Spinner genderSelect;
    private Spinner nonPre;

    //demo
    NetworkChangeList networkChangeList = new NetworkChangeList();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        //dialog tanım
        Button btnStart = view.findViewById(R.id.startMatch);
        genderSelect = view.findViewById(R.id.spinner_gender);
        gameSelect = view.findViewById(R.id.spinner_game_list);
        showError = view.findViewById(R.id.genderPremiumError);
        //firebase tanım
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
        mRef = mStore.collection("UserList").document(mUser.getUid());





        //Game Select Menu
        gameSelectMethod();


        //Gender Select Menu
        genderSelectMethod();


        //Free / Premium Kontrolü
        isFreeOrPremium();


        //kullanıcı listesi çekme
        mUserList = new ArrayList<>();
        if (!(mUser == null)) {
            btnStart.setOnClickListener(view -> {

                //Coin Update
               coinData = new HashMap<>();
                coinData.put("userCoin",user.getUserCoin()-100);
                mStore.collection("UserList").document(mUser.getUid())
                        .update(coinData);

                //dialog penceresini aç
                openDialogWindow();


                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {

                        //Eşleşme Odası Oluştur
                        createMatchingRoom();


                        //veri getir
                        mStore.collection("Matching Room").document(selectedGame).collection("Users")
                                .addSnapshotListener((value, error) -> {


                                    if (value != null) {

                                        mUserList.clear();
                                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                                            mDbUser = snapshot.toObject(dbUser.class);
                                            if (mDbUser != null) {
                                                mDbUser.setUserCoin(100);
                                                mUserList.add(mDbUser.getUserID());


                                            }
                                        }
                                        if (mUserList.size() < 2) {
                                            return;
                                        }


                                            //Random Eşleşme İçin
                                            //Kullanıcı 0 için Sohbet Odası Oluştur
                                            if (mUser.getUid().equals(mUserList.get(0))) {

                                                createChatRoom0();
                                                mDialogFragment.dismiss();

                                            }

                                            //Kullanıcı 1 için Sohbet Odası Oluştur
                                            if (mUser.getUid().equals(mUserList.get(1))) {

                                                createChatRoom1();
                                                mDialogFragment.dismiss();

                                            }


                                    }


                                });
                    }
                }.start();
            });
        }




        return view;


    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(networkChangeList,filter);
        super.onStart();
    }

    @Override
    public void onStop() {
        requireActivity().unregisterReceiver(networkChangeList);
        super.onStop();
    }

    private void createChatRoom1() {
        mStore.collection("ChatRoom").document(mUserList.get(0)).collection("Channels").document(mUser.getUid()).set(mData)
                .addOnCompleteListener(task -> {
                    chatIntent = new Intent(view.getContext(), ChatActivity.class);
                    channelId = UUID.randomUUID().toString();
                    chatIntent.putExtra("channelID", channelId);
                    chatIntent.putExtra("targetID", mUserList.get(0));
                    chatIntent.putExtra("selectGame", selectedGame);
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(chatIntent);

                });
    }

    private void createChatRoom0() {

        mStore.collection("ChatRoom").document(mUserList.get(1)).collection("Channels").document(mUser.getUid()).set(mData)
                .addOnCompleteListener(task -> {
                    chatIntent = new Intent(view.getContext(), ChatActivity.class);

                    channelId = UUID.randomUUID().toString();
                    chatIntent.putExtra("channelID", channelId);
                    chatIntent.putExtra("targetID", mUserList.get(1));
                    chatIntent.putExtra("selectGame", selectedGame);
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(chatIntent);

                });

    }

    private void createMatchingRoom() {
        mData = new HashMap<>();
        mData.put("gender", user.getGender());
        mData.put("userName", user.getUserName());
        mData.put("profilePics", user.getProfilePics());
        mData.put("userID", user.getUserID());
        mStore.collection("Matching Room").document(selectedGame).collection("Users").document(mUser.getUid()).set(mData);

    }

    private void openDialogWindow() {
        bundle.putString("SelectGame", selectedGame);
        mDialogFragment = new DialogFragment();
        mDialogFragment.setArguments(bundle);
        mDialogFragment.show(getChildFragmentManager(), "mDialogFragment");
    }

    private void isFreeOrPremium() {

        nonPre = view.findViewById(R.id.spinner_no_premium);
        ArrayAdapter<CharSequence> adapterNP = ArrayAdapter.createFromResource(getContext(), R.array.non_premium, R.layout.color_spinner_layout);
        nonPre.setAdapter(adapterNP);
        nonPre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectGender = "random";

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectGender =  "random";

            }
        });
        ///devamı
        mRef.addSnapshotListener((value, error) -> {

            if (error != null) {
                return;
            }

            if (value != null && value.exists()) {
                user = value.toObject(dbUser.class);

                if (user != null) {

                    if (!user.isPremium()) {
                        showError.setVisibility(View.VISIBLE);
                        mRef.update("status", "Free");
                        nonPre.setVisibility(View.VISIBLE);
                        genderSelect.setVisibility(View.GONE);
                    } else if (user.isPremium()) {
                        showError.setVisibility(View.GONE);
                        mRef.update("status", "Premium");
                        nonPre.setVisibility(View.GONE);
                        genderSelect.setVisibility(View.VISIBLE);

                    }


                }
            }


        });
    }

    private void genderSelectMethod() {
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(getContext(), R.array.gender_list, R.layout.color_spinner_layout);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSelect.setAdapter(adapterGender);
        genderSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterGender, View view, int i, long l) {


                if (i == 0) {
                    selectGender = "random";

                }
                if (i == 1) {

                    selectGender = "male";

                }
                if (i == 2) {
                    selectGender = "female";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectGender = "random";


            }
        });
    }

    private void gameSelectMethod() {
        //adapters
        ArrayAdapter<CharSequence> adapterGame = ArrayAdapter.createFromResource(getContext(), R.array.game_list, R.layout.color_spinner_layout);
        adapterGame.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameSelect.setAdapter(adapterGame);
        gameSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                if (i == 0) {
                    selectedGame = "Other";
                }
                if (i == 1) {
                    selectedGame = "LeaugeOfLegends";
                }
                if (i == 2) {
                    selectedGame = "Valorant";
                }
                if (i == 3) {
                    selectedGame = "CSGO";
                }
                if (i == 4) {
                    selectedGame = "Pubg";
                }
                if (i == 5) {
                    selectedGame = "Dota2";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                selectedGame = "Other";

            }
        });
    }

}
