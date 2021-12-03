package com.valorain.playtogether.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Fragment.deleteChatDialog;
import com.valorain.playtogether.Fragment.fullScreenImageFragment;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.R;
import com.valorain.playtogether.View.FullScreenActivity;


import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder>
{
    private  static final int MESAJ_SOL = 1;
    private  static final int MESAJ_SAG = 0;

    private ArrayList<Chat> mChatList;
    private Context mContext;
    private String mUID;
    private View v;
    private Chat mChat;
    private String hedefUID;
    private String docUID;




    public ChatAdapter(ArrayList<Chat> mChatList, Context mContext, String mUID,String hedefUID,String docUID) {
        this.mChatList = mChatList;
        this.mContext = mContext;
        this.mUID = mUID;
        this.hedefUID = hedefUID;
        this.docUID = docUID;


    }

    public ChatAdapter() {
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESAJ_SOL)
            v= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
        else if (viewType == MESAJ_SAG)
            v= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, @SuppressLint("RecyclerView") int position) {
        mChat = mChatList.get(position);
        holder.txtMesaj.setText(mChat.getMesajIcerigi());

        if (mChat.getMesajTipi().equals("text")){
            holder.mProgress.setVisibility(View.GONE);
            holder.imgResim.setVisibility(View.GONE);
            holder.txtMesaj.setText(mChat.getMesajIcerigi());
        }
        else {
            holder.txtMesaj.setVisibility(View.GONE);
            Picasso.get().load(mChat.getMesajIcerigi()).into(holder.imgResim, new Callback() {
                @Override
                public void onSuccess() {
                    holder.mProgress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

            holder.imgResim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  Intent browse = new Intent(Intent.ACTION_VIEW);
                 //   browse.setData(Uri.parse(mChatList.get(position).getMesajIcerigi()));
                  //  mContext.startActivity(browse);
                    Bundle bundle = new Bundle();
                    bundle.putString("imgSrc", mChatList.get(position).getMesajIcerigi());

                    FragmentActivity activity = (FragmentActivity)(mContext);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    fullScreenImageFragment fullImage = new fullScreenImageFragment();
                    fullImage.setArguments(bundle);
                    fullImage.show(fm,"fullSc");



                }

            });


        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{


         TextView txtMesaj;
         ImageView  imgResim;
         ProgressBar mProgress;






         public ChatHolder(@NonNull View itemView) {



             super(itemView);


             txtMesaj = itemView.findViewById(R.id.chat_item_txtMesaj);
             imgResim = itemView.findViewById(R.id.chat_item_imgResim);
             mProgress = itemView.findViewById(R.id.chat_item_progress);


         }
     }


    @Override
    public int getItemViewType(int position) {
     if (mChatList.get(position).getAlici().equals(mUID))

             return MESAJ_SOL;


     else
         return MESAJ_SAG;
    }
}
