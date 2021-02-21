package com.musicplayer;

import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.musicplayer.MainActivity.ARTIST_TO_FRAG;
import static com.musicplayer.MainActivity.PATH_TO_FRAG;
import static com.musicplayer.MainActivity.SHOW_MINI_PLAYER;
import static com.musicplayer.MainActivity.SONG_NAME;
import static com.musicplayer.MainActivity.SONG_NAME_TO_FRAG;
import static com.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.musicplayer.ApplicationClass.ACTION_PLAY;
public class NowPlayingFragment extends Fragment {

    ImageView albumArt, skipNext;
    TextView artist, song;
    FloatingActionButton playPauseBtn;
    View view;
    PlayerActivity playerActivity;

    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        init();
        skipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerActivity.nextBtnClicked();
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerActivity.playPauseBtnClicked();

            }
        });
        return view;
    }

    public void init(){
        albumArt = view.findViewById(R.id.bottom_album_art);
        skipNext = view.findViewById(R.id.bottom_card_skip_next_btn);
        artist = view.findViewById(R.id.bottom_card_artist);
        song = view.findViewById(R.id.bottom_card_song_name);
        playPauseBtn = view.findViewById(R.id.bottom_card_playpause_btn);
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[]art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SHOW_MINI_PLAYER){
            if(PATH_TO_FRAG!=null){
                byte[] art = getAlbumArt(PATH_TO_FRAG);
                if(art!=null){
                    Glide.with(getContext()).load(art).into(albumArt);
                }else{
                    Glide.with(getContext()).load(R.drawable.music_art).into(albumArt);
                }

                song.setText(SONG_NAME_TO_FRAG);
                artist.setText(ARTIST_TO_FRAG);
            }
        }
    }
}