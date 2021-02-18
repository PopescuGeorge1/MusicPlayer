package com.musicplayer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.musicplayer.MainActivity.musicFiles;

public class PlaylistFragment extends Fragment {
        RecyclerView recyclerView; //provide a limited window for a large data set (list for example)
        PlaylistAdapter playlistAdapter;

        public PlaylistFragment(){
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        if(!(musicFiles.size()<1))
        {
            playlistAdapter = new PlaylistAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(playlistAdapter); //set adapter to provide child views in demand
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}