package com.valorain.playtogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.valorain.playtogether.Fragment.ChatFragment;
import com.valorain.playtogether.Model.Chat;
import com.valorain.playtogether.Model.Kullanici;
import com.valorain.playtogether.R;
import com.valorain.playtogether.View.ChatActivity;

import java.util.ArrayList;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajlarAdapter extends RecyclerView.Adapter<MesajlarAdapter.MesajlarHolder> {

    private ArrayList<Kullanici> mArrayList;
    private Context mContext;
    private Kullanici mKullanici;
    private View v;
    private int kPos;
    private FirebaseFirestore mStore;
    private FirebaseUser mUser;
    private Intent chatIntent;


    public MesajlarAdapter(ArrayList<Kullanici> mArrayList, Context mContext) {
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
        mKullanici = mArrayList.get(position);

        if (!mUser.getUid().equals(mKullanici.getUserID()))
        holder.kullaniciIsmi.setText(mKullanici.getKullaniciAdi());

        if (mKullanici.getKullaniciProfil().equals("default"))
            holder.kullaniciProfil.setImageResource(R.mipmap.ic_launcher);

        else
            Picasso.get().load(mKullanici.getKullaniciProfil()).resize(60,60).into(holder.kullaniciProfil);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                kPos = holder.getAdapterPosition();

                if (kPos != RecyclerView.NO_POSITION){



                    chatIntent = new Intent(mContext, ChatActivity.class);

                    chatIntent.putExtra("hedefId",mArrayList.get(kPos).getUserID());
                    chatIntent.putExtra("alici",mArrayList.get(kPos).getKullaniciAdi());
                    chatIntent.putExtra("imgProfil",mArrayList.get(kPos).getKullaniciProfil());


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

       TextView kullaniciIsmi,sonMesaj;
       CircleImageView kullaniciProfil;


       public MesajlarHolder(@NonNull View itemView) {
           super(itemView);

           kullaniciIsmi = itemView.findViewById(R.id.chat_fragment_item_txtKullaniciIsim);
           kullaniciProfil = itemView.findViewById(R.id.chat_fragment_item_imgKullaniciProfil);
           sonMesaj = itemView.findViewById(R.id.chat_fragment_item_txtLastMessage);
       }
   }

}
