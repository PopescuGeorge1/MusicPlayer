package com.musicplayer;

import android.media.Image;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerActivityTest {

    @Rule
    public ActivityTestRule<PlayerActivity> pActivityR = new ActivityTestRule<PlayerActivity>(PlayerActivity.class);
    private PlayerActivity pActivity=null;

    @Before
    public void setUp(){
        pActivity = pActivityR.getActivity();
    }

    @Test
    public void testViews(){
        /*java.lang.NullPointerException: Attempt to invoke virtual method'int android.media.MediaPlayer.getDuration()' on a null object reference*/
        //errors solved: mostly nullPointerExceptions

        View v = pActivity.findViewById(R.id.layout_topBtn);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.mContainer);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.back_btn);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.menu_btn);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.card);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.cover_art);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.imageViewGradient);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.song_name);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.song_artist);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.relative_layout_for_bottom);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.shuffle);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.skip_previous);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.play_pause);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.skip_next);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.cycle);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.seek_bar_layout);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.seekBar);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.time_total);
        assertNotNull(v);
        v = pActivity.findViewById(R.id.time_played);
        assertNotNull(v);
    }

    @Test
    public void testImageViews(){
        ImageView imageView = pActivity.findViewById(R.id.play_pause);
        /*imageView.performClick();//doesn't work*/
        imageView.callOnClick();
        imageView = pActivity.findViewById(R.id.skip_next);
        imageView.callOnClick();
        imageView = pActivity.findViewById(R.id.skip_previous);
        imageView.callOnClick();
        imageView = pActivity.findViewById(R.id.cycle);
        imageView.callOnClick();
        imageView = pActivity.findViewById(R.id.shuffle);
        imageView.callOnClick();
    }

    @Test
    public void testSeekBar(){
        SeekBar seekBar = pActivity.findViewById(R.id.seekBar);
        seekBar.callOnClick();
    }

    @After
    public void tearDown(){
        pActivity=null;
    }

}