package com.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;

import java.util.ArrayList;

/*
App seems instalable but it CANNOT be installed on another device
by simply downloading it
 */


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
//    Button goToMain; //test button to shift between panels
    /*    public void toMain(View v){
        Intent intent = new Intent(v.getContext(), PlayerActivity.class);
        startActivity(intent);
    }*/

    public static final int REQUEST_CODE=1; //requires further detailing(why=1?)
    static ArrayList<MusicFiles>musicFiles;
    static boolean shuffleBoolean=false, repeatBoolean = false;
    static ArrayList <MusicFiles> playList = new ArrayList<>();
    private String my_sort_pref = "SortOrder";
//    FrameLayout frag_bottom_player;
    public static final String MUSIC_FILE_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static boolean SHOW_MINI_PLAYER = false;
    public static String PATH_TO_FRAG = null;
    public static String ARTIST_TO_FRAG = null;
    public static String SONG_NAME_TO_FRAG = null;
    public static final String SONG_NAME = "SONG NAME";
    public static final String ARTIST_NAME = "ARTIST NAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide(); //hide action bar
        setContentView(R.layout.song_list);
        permission();


    }
    //first go to manifest and add uses-permission
    //method used to ask for permission to access storage
    private void permission(){
        //if permission not granted (usually first time when started or if permission gets disabled
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }else{
            //starts every time when accessing application
//            Toast.makeText(this,"Permission Granted!", Toast.LENGTH_SHORT).show();
            musicFiles= getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            //if permission granted
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                //do whatever you want permission related
                musicFiles = getAllAudio(this);
                initViewPager();
            }
            else
            {
                //ask again for permission
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }
    /*
    initialize bar which will be filled with fragments
    */
    public void initViewPager(){
        ViewPager vp = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter vpa = new ViewPagerAdapter(getSupportFragmentManager());
        //add fragments
        vpa.addFragments(new MusicFragment(), "Songs");
        vpa.addFragments(new PlaylistFragment(), "Playlists");

        vp.setAdapter(vpa);
        tabLayout.setupWithViewPager(vp);
    }

    /*
    fragments (like an option bar)
    */
    public static class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public ArrayList<MusicFiles> getAllAudio(Context context){
        /*for sort*/
        SharedPreferences preferences = getSharedPreferences(my_sort_pref, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");
        String order = null;
        switch (sortOrder){
            case "sortByName":
                order=MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
                break;
            case "sortByDate":
                order=MediaStore.MediaColumns.DATE_ADDED+" ASC";
                break;
            case "sortBySize":
                order=MediaStore.MediaColumns.SIZE+" DESC";
                break;
        }
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };
        /*order added to cursor for sorting feature*/
        Cursor cursor = context.getContentResolver().query(
                uri, projection,null,null,order);
        if(cursor!=null){
            while(cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                MusicFiles musicFiles = new MusicFiles(path,title,artist,album,duration,id);
                //take log.e to check
                Log.e("Path : "+ path, "Album : "+album);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search); /*give id to our search option*/
        /*create an instance of search view*/
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for (MusicFiles song : musicFiles){
            if(song.getTitle().toLowerCase().contains(userInput))
            {
                myFiles.add(song);
            }
        }
        /*method in MusicAdapter/updateList */
        MusicFragment.musicAdapter.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(my_sort_pref, MODE_PRIVATE).edit();
        switch (item.getItemId()){
            case R.id.sort_name:
                editor.putString("sorting", "sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.sort_date:
                editor.putString("sorting", "sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.sort_size:
                editor.putString("sorting", "sortBySize");
                editor.apply();
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE);
        String path = preferences.getString(MUSIC_FILE, null);
        String artist = preferences.getString(ARTIST_NAME, null);
        String song_name = preferences.getString(SONG_NAME, null);
        if(path!=null){
            SHOW_MINI_PLAYER=true;
            PATH_TO_FRAG=path;
            ARTIST_TO_FRAG=artist;
            SONG_NAME_TO_FRAG=song_name;
        }
        else{
            SHOW_MINI_PLAYER=false;
            PATH_TO_FRAG=null;
            ARTIST_TO_FRAG=null;
            SONG_NAME_TO_FRAG=null;
        }
    }
}
