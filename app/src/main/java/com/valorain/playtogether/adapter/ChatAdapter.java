package com.valorain.playtogether.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.R;
import com.valorain.playtogether.View.ChatActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        mChat = mChatList.get(position);
        holder.txtMesaj.setText(mChat.getMesajIcerigi());



    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{


         TextView txtMesaj;


         public ChatHolder(@NonNull View itemView) {



             super(itemView);


             txtMesaj = itemView.findViewById(R.id.chat_item_txtMesaj);
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
