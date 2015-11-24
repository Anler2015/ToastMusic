package com.gejiahui.toastmusic.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.gejiahui.toastmusic.R;
import com.gejiahui.toastmusic.adapter.SongListAdapter;
import com.gejiahui.toastmusic.model.APPConstant;
import com.gejiahui.toastmusic.model.Mp3Info;
import com.gejiahui.toastmusic.service.MusicService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnAudioPlay ;
    Button btnNextSong ;
    Button btnPreSong ;
    Button btnStop;
    FloatingActionButton fab;
    ListView songsList;
    TextView txt_songDuration;
    TextView txt_playTime;
    LinearLayout songTime;
    int playingPosition = 0;
    boolean isPlaying = false;
    boolean isPause = false;
    List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        setListener();
        getMP3Info();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * get MP3 info from SD card
     */
    private void getMP3Info()
    {
        Cursor mp3Curosr =  getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //if mp3Curosr == null , return
        if (null == mp3Curosr) {
            return;
        }
        mp3Curosr.moveToFirst();

        for(int i = 0; i < mp3Curosr.getCount();i++)
        {
            Mp3Info mp3Info = new Mp3Info();
            mp3Info.setId(mp3Curosr.getLong(mp3Curosr.getColumnIndex(MediaStore.Audio.Media._ID))); //id
            mp3Info.setAlbum(mp3Curosr.getString(mp3Curosr.getColumnIndex(MediaStore.Audio.Media.ALBUM))); //album
            mp3Info.setDuration(mp3Curosr.getLong(mp3Curosr.getColumnIndex(MediaStore.Audio.Media.DURATION))); // the song duration
            mp3Info.setSize(mp3Curosr.getLong(mp3Curosr.getColumnIndex(MediaStore.Audio.Media.SIZE)));   //file size
            mp3Info.setUrl(mp3Curosr.getString(mp3Curosr.getColumnIndex(MediaStore.Audio.Media.DATA)));   //the file url
            mp3Info.setTitle(mp3Curosr.getString(mp3Curosr.getColumnIndex(MediaStore.Audio.Media.TITLE)));   //song name
            mp3Info.setSinger(mp3Curosr.getString(mp3Curosr.getColumnIndex(MediaStore.Audio.Media.ARTIST)));  //singer
            mp3Infos.add(mp3Info);
            mp3Curosr.moveToNext();
        }
        SongListAdapter adapter = new SongListAdapter(this,mp3Infos);
        songsList.setAdapter(adapter);
    }

    /**
     * init the widgets
     */
    private void initView()
    {
        btnAudioPlay = (Button) findViewById(R.id.btnAudioPlay);
        btnNextSong = (Button) findViewById(R.id.btnNextSong);
        btnPreSong = (Button) findViewById(R.id.btnPreSong);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        songsList = (ListView)findViewById(R.id.songsList);
        btnStop = (Button) findViewById(R.id.btnStop);
        txt_songDuration = (TextView)findViewById(R.id.songDuration);
        txt_playTime = (TextView)findViewById(R.id.playtime);
       songTime = (LinearLayout)findViewById(R.id.song_time);
    }

    /**
     * set on their listener
     */
    private void setListener()
    {
        btnAudioPlay.setOnClickListener(btnClickListener);
        btnNextSong.setOnClickListener(btnClickListener);
        btnStop.setOnClickListener(btnClickListener);
        songsList.setOnItemClickListener( itemClickListener);

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * button click listener
     */
    OnClickListener btnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            Log.v("gjh","isPlaying"+isPlaying);
            switch(v.getId())
            {

                case R.id.btnAudioPlay:
                    if(isPlaying == false)
                    {
                        if(isPause ==false)
                        {
                            intent.putExtra("msg",APPConstant.PLAY);
                            intent.putExtra("uri",mp3Infos.get(playingPosition).getUrl());
                        }
                        else
                        {
                            intent.putExtra("msg",APPConstant.RESUME);
                        }

                        isPlaying = true;
                        isPause = false;
                        showSongTime();
                    }
                    else
                    {
                        intent.putExtra("msg",APPConstant.PAUSE);
                        isPlaying = false;
                        isPause = true ;
                    }
                    startService(intent);
                    break;
                case R.id.btnNextSong:
                    playingPosition++;
                    intent.putExtra("msg",APPConstant.PLAY);
                    intent.putExtra("uri",mp3Infos.get(playingPosition).getUrl());
                    startService(intent);
                    break;

                case R.id.btnPreSong:
      ;
                    break;
                case R.id.btnStop:
                    intent.putExtra("msg",APPConstant.STOP);
                    startService(intent);
                    break;
            }
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            intent.putExtra("uri",mp3Infos.get(position).getUrl());
            startService(intent);
            isPlaying = true;
            playingPosition = position;
            showSongTime();
        }
    };

    /**
     * show song duration and playing time
     */
    private void showSongTime(){
        songTime.setVisibility(View.VISIBLE);
        txt_songDuration.setText(msToMinutes(mp3Infos.get(playingPosition).getDuration()));
    }

    private String msToMinutes(long time){
        String result = "";
        int minute = (int)time/1000/60;
        int second = (int)time/1000%60;
        DecimalFormat df=new DecimalFormat("00");
        result = minute + ":"+df.format(second);
        return result;
    }

}
