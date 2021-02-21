package com.musicplayer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.musicplayer.MainActivity.musicFiles;


public class MusicFragment extends Fragment {

    RecyclerView recyclerView; //provide a limited window for a large data set (list for example)
    static MusicAdapter musicAdapter;

    public MusicFragment() {
        // Required empty public constructor
    }


    //create and return view using the original return value (copy what comes after return to View and return View)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        //test improved recyclerView performance
        /*improved after full view and load of the list, still not fully smooth*/
        recyclerView.setItemViewCacheSize(40);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //
        if(!(musicFiles.size()<1))
        //possible solution to lag problem, read and memorise all files so it doesn't load them every time
            // loading the entire list might not be a good ideea for long lists
        {
            musicAdapter = new MusicAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(musicAdapter); //set adapter to provide child views in demand
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }



}