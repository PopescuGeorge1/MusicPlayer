package com.musicplayer;

import android.Manifest;
import android.content.Context;
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

import java.util.ArrayList;

/*
App seems instalable but it CANNOT be installed on another device
by simply downloading it
 */


public class SongListController extends AppCompatActivity {
//    Button goToMain; //test button to shift between panels
    /*    public void toMain(View v){
        Intent intent = new Intent(v.getContext(), PlayerActivity.class);
        startActivity(intent);
    }*/

    public static final int REQUEST_CODE=1; //requires further detailing(why=1?)
    static ArrayList<MusicFiles>musicFiles;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.song_list);
        permission();


    }
    //first go to manifest and add uses-permission
    //method used to ask for permission to access storage
    private void permission(){
        //if permission not granted (usually first time when started or if permission gets disabled
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SongListController.this,
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
                        SongListController.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }
    /*
    * initialize bar which will be filled with fragments
    */
    public void initViewPager(){
        ViewPager vp = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter vpa = new ViewPagerAdapter(getSupportFragmentManager());
        //add fragments
        vpa.addFragments(new SongsFragment(), "Songs");
        vpa.addFragments(new AlbumsFragment(), "Albums");

        vp.setAdapter(vpa);
        tabLayout.setupWithViewPager(vp);
    }

    //fragments (like an option bar)
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

    public static ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST};
        Cursor cursor = context.getContentResolver().query(
                uri, projection,null,null,null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(path,title,artist,album,duration);
                //take log.e to check
                Log.e("Path : "+ path, "Album : "+album);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }
}
