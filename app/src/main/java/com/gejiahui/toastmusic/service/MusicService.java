package com.gejiahui.toastmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gejiahui.toastmusic.model.APPConstant;
import com.gejiahui.toastmusic.model.Mp3Info;
import com.gejiahui.toastmusic.utensil.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by gejiahui on 2015/11/18.
 */
public class MusicService extends Service {

    private MediaPlayer musicPlayer ;
    String songURI;
    boolean isFirst = true;
    int currentTime;
    Helper helper;
    List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
    int currentPosition = 0;



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
        helper = new Helper(this);
        mp3Infos = helper.getMP3Info();
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
        currentPosition = intent.getIntExtra("currentPosition",0);
        songURI = mp3Infos.get(currentPosition).getUrl();
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
                sendMusicDurationBroad();
                Log.v("gjh", "currentPosition：" + currentPosition);
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

            case APPConstant.SLIDEING:
                int seekPosition = intent.getIntExtra("seekPosition",0);
                seekToMusic(seekPosition);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /////////////////media 状态改变///////////////////////////
    private void initMedia(){

        musicPlayer = new MediaPlayer();
        musicPlayer.setOnCompletionListener(onCompletionListener);
//        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.v("gjh","currentPosition  1："+currentPosition);
//                currentPosition++;
//                Log.v("gjh","currentPosition  2："+currentPosition);
//                if (currentPosition >= mp3Infos.size()) {
//                    currentPosition = 0;
//                }
//                Log.v("gjh","currentPosition  3："+currentPosition);
//                songURI = mp3Infos.get(currentPosition).getUrl();
//                playMusic();
//                sendMusicDurationBroad();
//            }
//        });
        //   musicPlayer.setAudioStreamType();

    }

     MediaPlayer.OnCompletionListener onCompletionListener = new  MediaPlayer.OnCompletionListener(){
         @Override
         public void onCompletion(MediaPlayer mp) {
             Log.v("gjh","currentPosition  1："+currentPosition);
             currentPosition++;
             Log.v("gjh","currentPosition  2："+currentPosition);
             if (currentPosition >= mp3Infos.size()) {
                 currentPosition = 0;
             }
             Log.v("gjh","currentPosition  3："+currentPosition);
             songURI = mp3Infos.get(currentPosition).getUrl();
             playMusic();
             sendMusicDurationBroad();
         }
     };

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
    private void pauseMusic() {
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

    private void seekToMusic(int position){

        if(musicPlayer != null ){
            musicPlayer.seekTo(position);
        }

    }


///////////////发送广播i///////////////////////////////////////
    private void sendMusicDurationBroad(){
        Intent Intent = new Intent();
        Intent.setAction(APPConstant.MUSIC_DURATION);
        Intent.putExtra("duration", musicPlayer.getDuration());
        Intent.putExtra("currentPosition", currentPosition);
        sendBroadcast(Intent);
        Log.v("gjh", "###########" + musicPlayer.getDuration());
    }

}
