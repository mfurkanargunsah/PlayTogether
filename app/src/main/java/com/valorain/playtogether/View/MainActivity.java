package com.valorain.playtogether.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Fragment.ChatFragment;
import com.valorain.playtogether.Fragment.FriendsFragment;
import com.valorain.playtogether.Fragment.HomeFragment;
import com.valorain.playtogether.Fragment.ProfileFragment;
import com.valorain.playtogether.Fragment.SettingsFragment;
import com.valorain.playtogether.Model.Kullanici;
import com.valorain.playtogether.R;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private HashMap<String, Object> mData;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawer;
    private NavigationView mNav;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle mToogle;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private FriendsFragment friendsFragment;
    private ChatFragment chatFragment;
    private SettingsFragment settingsFragment;
    private FirebaseUser mUser;
    private FirebaseFirestore mFireStore;
    private DocumentReference mRef;
    private Kullanici user;
    public View hView;
    public TextView userMail,status;
    public CircleImageView userPic;
    private static final String ONESIGNAL_APP_ID = "4852c54f-44bb-481c-80f8-e0167adcde29";





     private void init(){
         mAuth = FirebaseAuth.getInstance();
         mUser = FirebaseAuth.getInstance().getCurrentUser();


         //fragmentler
         homeFragment = new HomeFragment();
         profileFragment = new ProfileFragment();
         friendsFragment = new FriendsFragment();
         chatFragment = new ChatFragment();
         settingsFragment = new SettingsFragment();





    }


        public void kicker(){
            // giriş yapmadıysa kick
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


       //


        //fragment aktarım
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


                mNav.setNavigationItemSelectedListener(null);
                //firestore
                status = hView.findViewById(R.id.showStatus);
                mFireStore = FirebaseFirestore.getInstance();
                mRef = mFireStore.collection("Kullanıcılar").document(mUser.getUid());
                mRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {

                            return;
                        }
                        if (value != null && value.exists()) {

                            user = value.toObject(Kullanici.class);

                            if (user != null) {
                                status.setText(user.getStatus());
                            }
                        }
                    }
                });
            }

            ////////////





        //menu seçenekler
        mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()){
                    case R.id.nav_menu_home:

                        setHomeFragment(homeFragment);
                        mToolbar.setTitle("PlayTogether");
                        mDrawer.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.nav_menu_profile:
                        setHomeFragment(profileFragment);
                        mToolbar.setTitle("Profil");
                        mDrawer.closeDrawer(GravityCompat.START);
                        return true;

                        case R.id.nav_menu_chat:

                        setHomeFragment(chatFragment);
                        mToolbar.setTitle("Sohbetler");
                        mDrawer.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.nav_menu_settings:

                        setHomeFragment(settingsFragment);
                        mToolbar.setTitle("Ayarlar");
                        mDrawer.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.nav_menu_exit:
                        mDrawer.closeDrawer(GravityCompat.START);
                        kullaniciSetOnline(false);
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                        return true;
                }

                return false;
            }
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
            mFireStore.collection("Kullanıcılar").document(mUser.getUid())
                    .update(mData);





    }
    @Override
    protected void onResume() {
        super.onResume();
        kullaniciSetOnline(true);


    }

    @Override
    protected void onStop() {
        super.onStop();
        kullaniciSetOnline(false);

    }



    ///////////

  ///  @Override
 //   public void onBackPressed() {
    //    if(mDrawer.isDrawerOpen(GravityCompat.START))
       //     mDrawer.closeDrawer(GravityCompat.START);

  //   }


}