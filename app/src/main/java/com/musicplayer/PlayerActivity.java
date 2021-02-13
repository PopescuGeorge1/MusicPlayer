package com.musicplayer;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.musicplayer.SongListController.musicFiles;

public class PlayerActivity extends AppCompatActivity {




    TextView time_played;
    TextView time_total;
    TextView song_name;
    TextView artist_name;
    ImageView nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn, cover_art;
    FloatingActionButton playPauseBtn;
    SeekBar positionBar;

    int position = -1;
    static ArrayList<MusicFiles>listSongs = new ArrayList<>();
    static Uri uri; //uniform resource identifier
    static MediaPlayer mp; //used to control playback of audio/video files and streams
    private Handler handler = new Handler(); //send and process message and runnable objects associated with a thread's messageQueue
    private Thread playThread, prevThread, nextThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //hide actionbar
        setContentView(R.layout.player_activity);
        //initialize views (buttons, labels, etc.)
        initViews();
        getIntentMethod();
        time_total.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
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
        }); //follow audio/video file progress

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mp!=null){
                    int mCurrentPosition = mp.getCurrentPosition()/1000;
                    positionBar.setProgress(mCurrentPosition);
                    time_played.setText(formattedTimer(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
    }

    private String formattedTimer(int mCurrentPosition) {
        String totalout = "";
        String totalnew = "";
        String seconds = String.valueOf(mCurrentPosition%60);
        String minutes = String.valueOf(mCurrentPosition/60);
        totalout=minutes+":"+seconds;
        totalnew = minutes+":"+0+seconds;
        if(seconds.length()==1)
            return totalnew;
        else
            return totalout;
    }


    private void getIntentMethod(){
        position = getIntent().getIntExtra("position", -1);
        listSongs = musicFiles;
        if(listSongs !=null){
            playPauseBtn.setImageResource(R.drawable.pause_music);
            uri = Uri.parse(listSongs.get(position).getPath());

        }
        //starting new song (stop and release resources used for previous song)
        if(mp!=null){
            mp.stop();
            mp.release();
            mp = MediaPlayer.create(getApplicationContext(),uri);
            mp.start();
        }else{
            //starting a song
            mp = MediaPlayer.create(getApplicationContext(),uri);
            mp.start();
        }
        positionBar.setMax(mp.getDuration()/1000);
        metaData(uri);
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
        cover_art = findViewById(R.id.cover_art);
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void prevThreadBtn() {
        prevThread = new Thread(){
            @Override
            public  void run(){
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();;
    }

    private void prevBtnClicked() {
        if(mp.isPlaying()){
            mp.stop();
            mp.release();
            position=(position-1)<0?(listSongs.size()-1):(position-1);
            uri = Uri.parse(listSongs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            positionBar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null){
                        int mCurrentPosition = mp.getCurrentPosition()/1000;
                        positionBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.pause_music);
            mp.start();
        }else{
            mp.stop();
            mp.release();
            position=(position+1)%listSongs.size();
            uri = Uri.parse(listSongs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            positionBar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null){
                        int mCurrentPosition = mp.getCurrentPosition()/1000;
                        positionBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.play_btn);
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public  void run(){
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();;
    }

    private void nextBtnClicked() {
        if(mp.isPlaying()){
            mp.stop();
            mp.release();
            position=(position+1)%listSongs.size();
            uri = Uri.parse(listSongs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            positionBar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null){
                        int mCurrentPosition = mp.getCurrentPosition()/1000;
                        positionBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.pause_music);
            mp.start();
        }else{
            mp.stop();
            mp.release();
            position=(position+1)%listSongs.size();
            uri = Uri.parse(listSongs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            positionBar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null){
                        int mCurrentPosition = mp.getCurrentPosition()/1000;
                        positionBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.play_btn);
        }
    }

    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public  void run(){
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();;
    }

    private void playPauseBtnClicked() {
        if(mp.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.play_btn);
            mp.pause();
            positionBar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null){
                        int mCurrentPosition = mp.getCurrentPosition()/1000;
                        positionBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }else {
            playPauseBtn.setImageResource(R.drawable.pause_music);
            mp.start();
            positionBar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null){
                        int mCurrentPosition = mp.getCurrentPosition()/1000;
                        positionBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration())/1000;
        time_total.setText(formattedTimer(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        if(art!=null){
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
        }
        else
        {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.music_art)
                    .into(cover_art);
        }
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