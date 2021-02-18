package com.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//
//in work
//

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyHolder>{
    private Context mContext;
    private ArrayList <MusicFiles> pFiles;
    View view;

    PlaylistAdapter (Context mContext, ArrayList<MusicFiles> pFiles){
        this.mContext=mContext;
        this.pFiles=pFiles;
    }


    public class MyHolder extends RecyclerView.ViewHolder{
        public MyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.playlist_items,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return pFiles.size();
    }


}
