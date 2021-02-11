package com.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.musicplayer.SongListController.musicFiles;

public class PlayerActivity extends AppCompatActivity {



//    SeekBar volumeBar;
    TextView time_played;
    TextView time_total;
    TextView song_name;
    TextView artist_name;
    ImageView nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar positionBar;
    int position = -1;
    static ArrayList<MusicFiles>listSongs = new ArrayList<>();
    static Uri uri;


    static MediaPlayer mp;
    int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.player_activity);
        initViews();
        getIntentMethod();
        //seekbar position
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mp!=null && fromUser){
                    mp.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void getIntentMethod(){
        position = getIntent().getIntExtra("position", -1);
        listSongs = musicFiles;
        if(listSongs !=null){
            playPauseBtn.setImageResource(R.drawable.pause_music);
            uri = Uri.parse(listSongs.get(position).getPath());

        }
        if(mp!=null){
            mp.stop();
            mp.release();
            mp = MediaPlayer.create(getApplicationContext(),uri);
            mp.start();
        }else{
            mp = MediaPlayer.create(getApplicationContext(),uri);
            mp.start();
        }
        positionBar.setMax(mp.getDuration()/1000);
    }

//initialize buttons, views, ....
    private void initViews(){
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        time_played = findViewById(R.id.time_played);
        time_total = findViewById(R.id.time_total);
        nextBtn = findViewById(R.id.skip_next);
        prevBtn = findViewById(R.id.skip_previous);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.shuffle);
        repeatBtn = findViewById(R.id.cycle);
        playPauseBtn = findViewById(R.id.play_pause);
        positionBar = findViewById(R.id.seekBar);
    }


//        playBtn = (Button) findViewById(R.id.play_pause);
//        elapsedTimeLabel = (TextView) findViewById(R.id.time_played);
//        remainingTimeLabel = (TextView) findViewById(R.id.time_total);
//
//        //media player
//        mp = MediaPlayer.create(this, R.raw.music);
//        mp.setLooping(true);
//        mp.seekTo(0);
//        mp.setVolume(0.5f,0.5f);
//        totalTime=mp.getDuration();
//
//        //position bar
//        positionBar = (SeekBar) findViewById(R.id.positionBar);
//        positionBar.setMax(totalTime);
//        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(fromUser){
//                    mp.seekTo(progress);
//                    positionBar.setProgress(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        //volume bar
////        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
//        //
//        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                float volumeNum = progress / 100f;
//                mp.setVolume(volumeNum, volumeNum);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        //thread (update positionBar & timeLabel
//    new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while(mp!=null){
//                try{
//                    Message msg = new Message();
//                    msg.what = mp.getCurrentPosition();
//                    handler.sendMessage(msg);
//                    Thread.sleep(1000);
//
//                }catch(InterruptedException e){}
//            }
//        }
//    }).start();
//    }
//
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            int currentPosition = msg.what;
//            //update positionBar
//            positionBar.setProgress(currentPosition);
//            //update labels
//            String elapsedTime = createTimeLabel(currentPosition);
//            elapsedTimeLabel.setText(elapsedTime);
//
//            String remainingTime = createTimeLabel(totalTime-currentPosition);
//            remainingTimeLabel.setText("- "+remainingTime);
//        }
//    };
//
//    public String createTimeLabel(int time){
//        String timeLabel = "";
//        int min = time /1000 /60;
//        int sec = time / 1000 % 60;
//
//        timeLabel = min+":";
//        if(sec<10) timeLabel+="0";
//        timeLabel+=sec;
//
//        return timeLabel;
//    }
//    public void playBtnClick(View view){
//        if(!mp.isPlaying()) {
//            //stopping
//            mp.start();
//            playBtn.setBackgroundResource(R.drawable.pause_music);
//        }
//        else{
//            //playing
//            mp.pause();
//            playBtn.setBackgroundResource(R.drawable.play_btn );
//        }

}