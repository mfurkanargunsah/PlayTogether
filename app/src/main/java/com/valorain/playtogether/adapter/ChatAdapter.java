package com.valorain.playtogether.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.valorain.playtogether.Fragment.fullScreenImageFragment;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.R;


import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder>
{
    private  static final int MESSAGE_LEFT = 1;
    private  static final int MESSAGE_RIGHT = 0;

    private ArrayList<Chat> mChatList;
    private Context mContext;
    private String mUID;
    private View v;
    private Chat mChat;





    public ChatAdapter(ArrayList<Chat> mChatList, Context mContext, String mUID) {
        this.mChatList = mChatList;
        this.mContext = mContext;
        this.mUID = mUID;


    }

    public ChatAdapter() {
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_LEFT)
            v= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
        else if (viewType == MESSAGE_RIGHT)
            v= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, @SuppressLint("RecyclerView") int position) {
        mChat = mChatList.get(position);
        holder.txtMessage.setText(mChat.getUserMessage());

        if (mChat.getMessageType().equals("text")){
            holder.mProgress.setVisibility(View.GONE);
            holder.imgPics.setVisibility(View.GONE);
            holder.txtMessage.setText(mChat.getUserMessage());
        }
        else {
            holder.txtMessage.setVisibility(View.GONE);
            Picasso.get().load(mChat.getUserMessage()).into(holder.imgPics, new Callback() {
                @Override
                public void onSuccess() {
                    holder.mProgress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

            holder.imgPics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  Intent browse = new Intent(Intent.ACTION_VIEW);
                 //   browse.setData(Uri.parse(mChatList.get(position).getMesajIcerigi()));
                  //  mContext.startActivity(browse);
                    Bundle bundle = new Bundle();
                    bundle.putString("imgSrc", mChatList.get(position).getUserMessage());

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


         TextView txtMessage;
         ImageView imgPics;
         ProgressBar mProgress;






         public ChatHolder(@NonNull View itemView) {



             super(itemView);


             txtMessage = itemView.findViewById(R.id.chat_item_txtMesaj);
             imgPics = itemView.findViewById(R.id.chat_item_imgResim);
             mProgress = itemView.findViewById(R.id.chat_item_progress);


         }
     }


    @Override
    public int getItemViewType(int position) {
     if (mChatList.get(position).getReceiver().equals(mUID))

             return MESSAGE_LEFT;


     else
         return MESSAGE_RIGHT;
    }
}
