package com.valorain.playtogether.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import com.valorain.playtogether.Fragment.deleteChatDialog;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SliderAdapter  extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{


    private ArrayList<dbUser> mArrayList;
     private ViewPager2 viewPager2;
    private FirebaseFirestore mStore;
    private dbUser mDbUser;
    private FirebaseUser mUser;



     public SliderAdapter( ArrayList<dbUser> mArrayList, ViewPager2 viewPager2) {
        this.mArrayList = mArrayList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_item_test,
                        parent,
                        false
                )

        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDbUser = mArrayList.get(position);








        if (!mUser.getUid().equals(mDbUser.getUserID())) {
            holder.txtUsername.setText(mDbUser.getUserName() + " | " + mDbUser.getUserAge() + " | " + mDbUser.getGender());
            holder.txtUserDescription.setText(mDbUser.getUserDescription());

            if (mDbUser.getProfilePics().equals("default")) {
                holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.get().load(mDbUser.getProfilePics()).into(holder.circleImageView);
            }

            if (mDbUser.getUserBackground().equals("default"))
                holder.imageView.setImageResource(R.drawable.dialog_other);
            else
                Picasso.get().load(mDbUser.getUserBackground()).into(holder.imageView);

        }
    }


    @Override
    public int getItemCount() {

        return mArrayList.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageView;
        private CircleImageView circleImageView;
        private TextView txtUsername,txtUserDescription;


        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlideBackground);
            circleImageView = itemView.findViewById(R.id.slide_item_userProfilePics);
            txtUsername = itemView.findViewById(R.id.slide_item_username);
            txtUserDescription = itemView.findViewById(R.id.slide_item_userDescription);

        }

    }
}
