package com.valorain.playtogether.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.onesignal.OneSignal;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Fragment.ChatFragment;
import com.valorain.playtogether.Fragment.HomeFragment;
import com.valorain.playtogether.Fragment.ProfileFragment;
import com.valorain.playtogether.GameActivity;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;
import com.valorain.playtogether.utility.NetworkChangeList;


import java.util.HashMap;

import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private HashMap<String, Object> mData;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawer;
    private NavigationView mNav;
    private Toolbar mToolbar;

    //CoutDownTimer
    private static final String IS_START_KEY = "IS_START";
    private static final String LAST_TIME_SAVE_KEY = "LAST_TIME_SAVE";
    private static final String TIME_REMAIN_KEY = "TIME_REMAIN";
    private static final long  TIME_LIMIT = 5*1000; // for the 24 hours// Hours*Minute*Second*Millisecond
    private boolean isStart;
    ///////////////////////////////////////////////////////

    private ActionBarDrawerToggle mToogle;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private ChatFragment chatFragment;
    private FirebaseUser mUser;
    private FirebaseFirestore mFireStore;
    private DocumentReference mRef;
    private dbUser user;  // Kullanıcı means user in turkish
    public View hView;
    public TextView userMail,status,userCoin,earnFreeCoin;
    public CircleImageView userPic;
    private Button buyPremium;
    private static final String ONESIGNAL_APP_ID = "4852c54f-44bb-481c-80f8-e0167adcde29";
    private String userC;
    NetworkChangeList networkChangeList = new NetworkChangeList();
    private HashMap<String ,Object> mCoin;

    CountdownView countdownView;

    //Ads
    private RewardedAd mRewardedAd;
    private AdRequest adRequest;



     private void init(){
         mAuth = FirebaseAuth.getInstance();
         mUser = FirebaseAuth.getInstance().getCurrentUser();


         adRequest = new AdRequest.Builder().build();
         loadAds();
         //Fragments
         homeFragment = new HomeFragment();
         profileFragment = new ProfileFragment();
         chatFragment = new ChatFragment();




    }


        public void kicker(){
            //If the user is not logged in, redirect to login page
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mUser == null){
                startActivity(new Intent(MainActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        kicker();


        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);





        //Select main fragment
        setHomeFragment(homeFragment);

        //menubar
        mDrawer = findViewById(R.id.main_activity_drawerlayout);
        mNav = findViewById(R.id.main_menu);
        mToolbar = findViewById(R.id.toolbar_custom);



        setSupportActionBar(mToolbar);
        mNav.setItemIconTintList(null);
        mToogle = new ActionBarDrawerToggle(MainActivity.this,mDrawer,mToolbar,R.string.nav_open,R.string.nav_close);
        mDrawer.addDrawerListener(mToogle);
        mToogle.syncState();
        //nav header updateder
            if (mUser != null){
                hView = mNav.getHeaderView(0);

                userMail = hView.findViewById(R.id.showUserMail);
                userMail.setText(mUser.getEmail());

                userPic = hView.findViewById(R.id.nav_header_userPic);
                buyPremium = hView.findViewById(R.id.nav_header_buyPremium);
                userCoin = hView.findViewById(R.id.nav_user_coin);

                earnFreeCoin = hView.findViewById(R.id.nav_earn_free_coin);



                //Coutdown Timer
                Paper.init(this);
                isStart = Paper.book().read(IS_START_KEY,false);
                //Check time
                if (isStart){
                    earnFreeCoin.setVisibility(View.GONE);
                    checktime();
                }else
                    earnFreeCoin.setVisibility(View.VISIBLE);
                countdownView = (CountdownView) hView.findViewById(R.id.nav_coin_timer);

                //coutdown
                setupView();

                mNav.setNavigationItemSelectedListener(null);
                //firestore
                status = hView.findViewById(R.id.showStatus);
                mFireStore = FirebaseFirestore.getInstance();
                mRef = mFireStore.collection("UserList").document(mUser.getUid());
                mRef.addSnapshotListener((value, error) -> {
                    if (error != null) {

                        return;
                    }
                    if (value != null && value.exists()) {

                        user = value.toObject(dbUser.class);

                        if (user != null) {
                            status.setText(user.getStatus());


                            userC = String.valueOf(user.getUserCoin());
                            userCoin.setText(userC);

                            //Add coin
                            mCoin = new HashMap<>();
                            mCoin.put("userCoin",user.getUserCoin() + 1000);



                            ////
                            if (user.getProfilePics().equals("default")) {
                                userPic.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                Picasso.get().load(user.getProfilePics()).into(userPic);
                            }

                            if (user.isPremium())
                                buyPremium.setVisibility(View.GONE);
                            else
                                buyPremium.setVisibility(View.VISIBLE);

                        }
                    }

                });
            }

            ////////////




        //menu seçenekler
        mNav.setNavigationItemSelectedListener(item -> {


            switch (item.getItemId()){
                case R.id.nav_menu_home:

                    setHomeFragment(homeFragment);
                    mToolbar.setTitle("PlayTogether");
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;

                case R.id.nav_menu_profile:
                    setHomeFragment(profileFragment);
                    mToolbar.setTitle("Profile");
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;

                    case R.id.nav_menu_chat:

                    setHomeFragment(chatFragment);
                    mToolbar.setTitle("Chats");
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;

                case R.id.nav_menu_settings:

                    startActivity(new Intent(this, GameActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;

                case R.id.nav_menu_exit:
                    mDrawer.closeDrawer(GravityCompat.START);
                    kullaniciSetOnline(false);
                    mAuth.signOut();
                    startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                   finish();

                    return true;
            }

            return false;
        });


    }

            //Fragment Metod
            private void setHomeFragment(Fragment fragment){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_activity_framelayout,fragment);
                transaction.commit();

            }

                //kullanici online mı

    private void kullaniciSetOnline(boolean b)
    {
            mData = new HashMap<>();
            mData.put("useronline",b);
            mFireStore.collection("UserList").document(mUser.getUid())
                    .update(mData);





    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeList,filter);
        super.onStart();
    }

    @Override
    protected void onResume() {
        kullaniciSetOnline(true);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeList,filter);
        super.onResume();



    }

    @Override
    protected void onStop() {
        kullaniciSetOnline(false);
         unregisterReceiver(networkChangeList);
        Paper.book().write(TIME_REMAIN_KEY,countdownView.getRemainTime());
        Paper.book().write(LAST_TIME_SAVE_KEY,System.currentTimeMillis());
         super.onStop();

    }

    private void reset() {

        earnFreeCoin.setVisibility(View.VISIBLE);
        Paper.book().delete(IS_START_KEY);
        Paper.book().delete(LAST_TIME_SAVE_KEY);
        Paper.book().delete(TIME_REMAIN_KEY);
    }



    private void checktime() {

        long currentTime = System.currentTimeMillis();
        long lastTimeSaved = Paper.book().read(LAST_TIME_SAVE_KEY);
        long timeRemain = Paper.book().read(TIME_REMAIN_KEY);
        long result = timeRemain + (lastTimeSaved - currentTime);
        if (result > 0){
            countdownView.start(result);
        }
        else
        {
            countdownView.stop();
            reset();

        }
    }
    private void setupView() {

        earnFreeCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStart){
                  countdownView.start(TIME_LIMIT);
                    Paper.book().write(IS_START_KEY,true);
                    earnFreeCoin.setVisibility(View.GONE);
                    mFireStore.collection("UserList").document(mUser.getUid()).update(mCoin);

                    //Show Ad
                  if (mRewardedAd != null){
                      loadAds();

                      Activity activityContext = MainActivity.this;
                      mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                          @Override
                          public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                              int rewardAmount = rewardItem.getAmount();
                              String rewardType = rewardItem.getType();

                          }
                      });

                  }else
                  {
                      System.out.println("Ödül Hazır Değil");
                  }


                }
            }
        });

        countdownView.setOnCountdownEndListener(cv -> {
            Toast.makeText(this,"Reward Available!",Toast.LENGTH_SHORT).show();
            reset();
        });

        countdownView.setOnCountdownIntervalListener(1000, (cv, remainTime) -> {
            Log.d("Timer",""+remainTime);
        });

    }

   private void loadAds(){
       //Add

       RewardedAd.load(MainActivity.this, getString(R.string.testAdsId), adRequest, new RewardedAdLoadCallback() {
           @Override
           public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

               mRewardedAd = rewardedAd;
               System.out.println("Reklam Yüklendi");

           }

           @Override
           public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

               System.out.println(loadAdError.getMessage());
               mRewardedAd = null;
               loadAds();

           }
       });

    }



}