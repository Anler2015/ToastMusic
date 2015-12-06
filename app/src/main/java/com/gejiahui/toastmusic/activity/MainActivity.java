package com.gejiahui.toastmusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.gejiahui.toastmusic.R;
import com.gejiahui.toastmusic.adapter.SongListAdapter;
import com.gejiahui.toastmusic.model.APPConstant;
import com.gejiahui.toastmusic.model.Mp3Info;
import com.gejiahui.toastmusic.service.MusicService;
import com.gejiahui.toastmusic.utensil.Helper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnAudioPlay;
    Button btnNextSong;
    Button btnPreSong;
    Button btnStop;
    SeekBar musicSeekbar;
    FloatingActionButton fab;
    ListView songsList;
    TextView txt_songDuration;
    TextView txt_currentTime;
    LinearLayout songTime;
    int currentPosition = 0;
    static boolean isPlaying = false;
    boolean isPause = false;
    List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
    Helper helper;
    MusicReceiver mReceiver;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper = new Helper(this);
        mp3Infos = helper.getMP3Info();
        initView();
        setListener();
        if (isPlaying) {
            showSongTime(true);
        }

        mReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(APPConstant.MUSIC_CURRENT);
        intentFilter.addAction(APPConstant.MUSIC_DURATION);
        registerReceiver(mReceiver, intentFilter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
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
     * init the widgets
     */
    private void initView() {
        btnAudioPlay = (Button) findViewById(R.id.btnAudioPlay);
        btnNextSong = (Button) findViewById(R.id.btnNextSong);
        btnPreSong = (Button) findViewById(R.id.btnPreSong);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        songsList = (ListView) findViewById(R.id.songsList);
        btnStop = (Button) findViewById(R.id.btnStop);
        txt_songDuration = (TextView) findViewById(R.id.songDuration);
        txt_currentTime = (TextView) findViewById(R.id.playtime);
        songTime = (LinearLayout) findViewById(R.id.song_time);
        musicSeekbar = (SeekBar) findViewById(R.id.musicSeekbar);

        SongListAdapter adapter = new SongListAdapter(this, mp3Infos);
        songsList.setAdapter(adapter);
    }

    /**
     * set on their listener
     */
    private void setListener() {
        btnAudioPlay.setOnClickListener(btnClickListener);
        btnNextSong.setOnClickListener(btnClickListener);
        btnStop.setOnClickListener(btnClickListener);
        btnPreSong.setOnClickListener(btnClickListener);
        songsList.setOnItemClickListener(itemClickListener);
        musicSeekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);
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
            Log.v("gjh", "isPlaying" + isPlaying);
            switch (v.getId()) {

                case R.id.btnAudioPlay:
                    if (isPlaying == false) {   //播放
                        if (isPause == false) {
                            intent.putExtra("msg", APPConstant.PLAY);
                            intent.putExtra("currentPosition", currentPosition);
                        } else {
                            intent.putExtra("msg", APPConstant.RESUME);
                        }

                        isPlaying = true;
                        isPause = false;
                        showSongTime(true);
                    } else {   //暂停
                        intent.putExtra("msg", APPConstant.PAUSE);

                        isPlaying = false;
                        isPause = true;
                    }
                    startService(intent);
                    break;
                case R.id.btnNextSong:
                    currentPosition++;
                    if(currentPosition >= mp3Infos.size()){
                        currentPosition = 0;
                    }

                    intent.putExtra("msg", APPConstant.PLAY);
                    intent.putExtra("currentPosition", currentPosition);
                    startService(intent);
                    showSongTime(true);
                    break;

                case R.id.btnPreSong:
                    currentPosition--;
                    if(currentPosition < 0){
                        currentPosition = mp3Infos.size()-1;
                    }
                    intent.putExtra("msg", APPConstant.PLAY);
                    intent.putExtra("currentPosition", currentPosition);
                    startService(intent);
                    showSongTime(true);
                    break;
                case R.id.btnStop:
                    isPlaying = false;
                    isPause = false;
                    intent.putExtra("msg", APPConstant.STOP);
                    startService(intent);
                    showSongTime(false);
                    break;
            }
        }
    };


    /**
     * listview item click listener
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            intent.putExtra("currentPosition", position);
            startService(intent);
            isPlaying = true;
            currentPosition = position;
            showSongTime(true);
        }
    };


    /**
     * seekbar listener
     */
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("seekPosition", musicSeekbar.getProgress());
                intent.putExtra("msg", APPConstant.SLIDEING);
                startService(intent);


        }
    };

    /**
     * show song duration and playing time or not
     */
    private void showSongTime(boolean ifShow) {
        if (ifShow) {
            songTime.setVisibility(View.VISIBLE);
     //       txt_songDuration.setText(msToMinutes(mp3Infos.get(playingPosition).getDuration()));
        } else {
            songTime.setVisibility(View.INVISIBLE);
        }

    }




    /**
     * @param time
     * @return
     */
    private String msToMinutes(long time) {
        String result = "";
        int minute = (int) time / 1000 / 60;
        int second = (int) time / 1000 % 60;
        DecimalFormat df = new DecimalFormat("00");
        result = minute + ":" + df.format(second);
        return result;
    }



    /**
     * 接收service返回的动作
     */
    class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
  //          Log.v("gjh", "action：  " + action);
            //更新音乐播放时间
            if (action.equals(APPConstant.MUSIC_CURRENT)) {
                int currentTime = intent.getIntExtra("currentTime", 0);
                txt_currentTime.setText(msToMinutes(currentTime));
                musicSeekbar.setProgress(currentTime);

            } else if (action.equals(APPConstant.MUSIC_DURATION)) {
                int duration = intent.getIntExtra("duration", 0);
                currentPosition = intent.getIntExtra("currentPosition", 0);
                musicSeekbar.setMax(duration);
                txt_songDuration.setText(msToMinutes(duration));
                Log.v("gjh", "歌曲更新");
                Log.v("gjh", "max:" + musicSeekbar.getMax());
            }

        }
    }


}
