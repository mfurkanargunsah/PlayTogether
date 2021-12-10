package com.valorain.playtogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Fragment.deleteChatDialog;
import com.valorain.playtogether.Model.dbUser;
import com.valorain.playtogether.R;
import com.valorain.playtogether.View.ChatActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajlarAdapter extends RecyclerView.Adapter<MesajlarAdapter.MesajlarHolder> {

    private ArrayList<dbUser> mArrayList;
    private Context mContext;
    private dbUser mDbUser;
    private View v;
    private int kPos;
    private FirebaseFirestore mStore;
    private FirebaseUser mUser;
    private Intent chatIntent;
    private String targetID;



    public MesajlarAdapter(ArrayList<dbUser> mArrayList, Context mContext) {
        this.mArrayList = mArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MesajlarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        v = LayoutInflater.from(mContext).inflate(R.layout.chat_fragment_item,parent,false);

        return new MesajlarHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajlarHolder holder, int position) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDbUser = mArrayList.get(position);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               kPos = holder.getAdapterPosition();

                if (kPos != RecyclerView.NO_POSITION){

                    FragmentActivity activity = (FragmentActivity)(mContext);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    deleteChatDialog deleteChat;
                    deleteChat = new deleteChatDialog();
                    deleteChat.show(fm,"delete");

                    Bundle bundle = new Bundle();
                    bundle.putString("targetID",mArrayList.get(kPos).getUserID());
                    deleteChat.setArguments(bundle);

                }


            }
        });


        if (!mUser.getUid().equals(mDbUser.getUserID()))
        holder.userName.setText(mDbUser.getUserName());

        if (mDbUser.getProfilePics().equals("default"))
            holder.userProfilePics.setImageResource(R.mipmap.ic_launcher);

        else
            Picasso.get().load(mDbUser.getProfilePics()).into(holder.userProfilePics);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                kPos = holder.getAdapterPosition();


                if (kPos != RecyclerView.NO_POSITION){



                    chatIntent = new Intent(mContext, ChatActivity.class);

                    chatIntent.putExtra("targetID",mArrayList.get(kPos).getUserID());
                    chatIntent.putExtra("receiver",mArrayList.get(kPos).getUserName());
                    chatIntent.putExtra("imgProfil",mArrayList.get(kPos).getProfilePics());
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(chatIntent);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MesajlarHolder extends RecyclerView.ViewHolder{

       TextView userName, lastMessage;
       CircleImageView userProfilePics;
       ImageView imgDelete;




       public MesajlarHolder(@NonNull View itemView) {
           super(itemView);

           userName = itemView.findViewById(R.id.chat_fragment_item_txtKullaniciIsim);
           userProfilePics = itemView.findViewById(R.id.chat_fragment_item_imgKullaniciProfil);
           lastMessage = itemView.findViewById(R.id.chat_fragment_item_txtLastMessage);
           imgDelete = itemView.findViewById(R.id.chat_fragment_item_imgDelete);
       }
   }

}
