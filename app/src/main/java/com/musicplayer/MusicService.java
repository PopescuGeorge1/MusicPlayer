package com.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import static com.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.musicplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.musicplayer.ApplicationClass.CHANNEL_ID_2;
import static com.musicplayer.PlayerActivity.listSongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    public static final String MUSIC_FILE_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String SONG_NAME = "SONG NAME";
    public static final String ARTIST_NAME = "ARTIST NAME";


    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if(myPosition!=-1)
            playMedia(myPosition);
/*        return super.onStartCommand(intent, flags, startId);*/

        /*Notification bar commands*/
        if(actionName!=null)
            switch (actionName){
                case "playPause":
                    Toast.makeText(this,"PlayPause", Toast.LENGTH_SHORT).show();
                    if(actionPlaying!=null){
                        Log.e("Inside", "Action");
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case "next":
                    Toast.makeText(this,"Next", Toast.LENGTH_SHORT).show();
                    if(actionPlaying!=null){
                        Log.e("Inside", "Action");
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "previous":
                    Toast.makeText(this,"Previous", Toast.LENGTH_SHORT).show();
                    if(actionPlaying!=null){
                        Log.e("Inside", "Action");
                        actionPlaying.prevBtnClicked();
                    }
                    break;
            }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        musicFiles = listSongs;
        position = startPosition;
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles!=null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else{
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start(){
        if(mediaPlayer!=null)
            mediaPlayer.start();
    }
    boolean isPlaying(){
        if(mediaPlayer!=null)
            return mediaPlayer.isPlaying();
        return false;
    }
    void stop(){
        if(mediaPlayer!=null)
           mediaPlayer.stop();
    }
    void release(){
        if(mediaPlayer!=null)
            mediaPlayer.release();
    }
    int getDuration(){
        //attempt to resolve test error
        if(mediaPlayer!=null)
            return mediaPlayer.getDuration();
        else
            return -1;
        //attempt to resolve test error; originally there was no if here
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(int positionInner){
        position=positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_FILE_LAST_PLAYED, MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST_NAME, musicFiles.get(position).getArtist());
        editor.putString(SONG_NAME, musicFiles.get(position).getTitle());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }
    int getCurrentPosition(){
        //attempt to resolve test error; originally there was no if here
        if(mediaPlayer!=null)
            return mediaPlayer.getCurrentPosition();
        else
            return 0;
    }
    void pause(){
        mediaPlayer.pause();
    }

    @Override
    public void onCompletion(MediaPlayer mp){
        if(actionPlaying!=null) {
            actionPlaying.nextBtnClicked();
            if(mediaPlayer!=null){
                createMediaPlayer(position);
                mediaPlayer.start();
                OnCompleted();
            }
        }


    }
    void OnCompleted(){
        if(mediaPlayer!=null)
            mediaPlayer.setOnCompletionListener(this);
    }
    void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying=actionPlaying;
    }
    void showNotification (int playPauseBtn){
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, intent, 0);
        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(
                this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(
                this, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(
                this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;
        //attempt to resolve test error
        if(position>0)
            picture = getAlbumArt(musicFiles.get(position).getPath());
        //attempt to resolve test error
        Bitmap thumb = null;
        if(picture!=null){
            thumb = BitmapFactory.decodeByteArray(picture,0,picture.length);
        }
        else
        {
            thumb = BitmapFactory.decodeResource(getResources(),R.drawable.music_art);
        }
        Notification notification=null;
        if(musicFiles.size()>0){
            notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.skip_previous, "Previous", prevPending)
                .addAction(playPauseBtn, "Play/Pause", pausePending)
                .addAction(R.drawable.skip_next, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
       startForeground(2, notification);}
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[]art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
