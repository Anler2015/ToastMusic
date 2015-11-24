package com.gejiahui.toastmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gejiahui.toastmusic.model.APPConstant;

import java.io.IOException;

/**
 * Created by gejiahui on 2015/11/18.
 */
public class MusicService extends Service {

    private MediaPlayer musicPlayer ;
    String songURI;
    boolean isFirst = true;
    boolean isPlaying =false;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirst = true;
        if(musicPlayer != null)
        {
            musicPlayer.stop();
            musicPlayer.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

         songURI = intent.getStringExtra("uri");
        int msg = intent.getIntExtra("msg",0);
        switch (msg)
        {
            case APPConstant.PLAY:
                if(isFirst)
                {
                    initMedia();
                    isFirst = false;
                }
                playMusic();
                break;
            case APPConstant.PAUSE:

                pauseMusic();

                break;
            case APPConstant.STOP:

                stopMusic();

                break;
            case APPConstant.RESUME:

                resumeMusic();
                break;
        }




        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void initMedia(){

        musicPlayer = new MediaPlayer();
     //   musicPlayer.setAudioStreamType();

    }


    /**
     * start to prepare and play music
     */
    private void playMusic(){

        musicPlayer.reset();
        try {
            musicPlayer.setDataSource(songURI);
            musicPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        musicPlayer.start();
    }


    private void resumeMusic(){
        if(musicPlayer != null && !musicPlayer.isPlaying())
        {
            musicPlayer.start();
        }
    }

    /**
     * pause music
     */
    private void pauseMusic(){
        if(musicPlayer != null && musicPlayer.isPlaying())
        {
            musicPlayer.pause();

        }
    }

    /**
     * stop music
     */
    private void stopMusic(){
        if (musicPlayer != null)
        {
            musicPlayer.stop();
        }
    }
}
