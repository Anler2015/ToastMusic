package com.gejiahui.toastmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gejiahui.toastmusic.model.APPConstant;
import java.io.IOException;



/**
 * Created by gejiahui on 2015/11/18.
 */
public class MusicService extends Service {

    int progress = 0;
    ProgressIBinder myBind;
    private MediaPlayer musicPlayer ;
    String songURI;
    boolean isFirst = true;
    boolean isPlaying =false;
    int currentTime;

    //服务要发送的一些Action


    /**
     * 一个循环的handler 用来更新UI
     */

    private Handler timeHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            if (musicPlayer != null) {
                if(musicPlayer.isPlaying()){
                    currentTime = musicPlayer.getCurrentPosition();
                    Intent intent = new Intent();
                    intent.setAction(APPConstant.MUSIC_CURRENT);
                    intent.putExtra("currentTime", currentTime);
                    sendBroadcast(intent); // 发送广播
                }

                timeHandler.sendEmptyMessageDelayed(1, 1000);
            }


        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        myBind = new ProgressIBinder();
        timeHandler.sendEmptyMessage(1);
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

                Intent boardIntent = new Intent();
                boardIntent.setAction(APPConstant.MUSIC_DURATION);
                boardIntent.putExtra("duration", musicPlayer.getDuration());
                sendBroadcast(boardIntent);
                Log.v("gjh", "###########" + musicPlayer.getDuration());
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
        return myBind;
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
    private void
    stopMusic(){
        if (musicPlayer != null)
        {
            musicPlayer.stop();
        }
    }




    class ProgressIBinder extends Binder
    {

        public  int getProgress()
        {
            return progress;
        }

    }
}
