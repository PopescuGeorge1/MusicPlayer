package com.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> implements  {

    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext,ArrayList<MusicFiles>mFiles){
        this.mFiles=mFiles;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyViewHolder(view);

    }

    /*
    lag possible solution
    create a loop that goes loads all files into an array at the start of the application
    the array will be cleaned when the app gets closed
    */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());

        if(image!=null){
            Glide.with(mContext).asBitmap().load(image).into(myViewHolder.album_art);
        }else{
            Glide.with(mContext).load(R.drawable.music_art).into(myViewHolder.album_art);
        }

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);

                mContext.startActivity(intent);
            }
        });
        //popup menu
        myViewHolder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener( (item) -> {
                    switch (item.getItemId()){
                        case R.id.delete:
                            Toast.makeText(mContext,"Delete Clicked", Toast.LENGTH_SHORT).show();
                            deleteFile(position, v);
                            break;
                    }
                    return true;
                });
            }
        });
    }

    /*
    *delete file
    */
    private void deleteFile(int position, View v){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));
        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete(); //delete your file

        /*
        file removed from recycler view
        */
        if(deleted){
            mContext.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(v, "File Deleted", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(v, "Can't be Deleted", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    /*song accessible label: art, name, menu*/
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView file_name;
        ImageView album_art, menuMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name=itemView.findViewById(R.id.music_file_name);
            album_art=itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.menu_more);
        }

    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[]art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    /*update list depending on the items searched*/
    void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFiles= new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
