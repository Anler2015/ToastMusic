package com.gejiahui.toastmusic.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gejiahui.toastmusic.R;
import com.gejiahui.toastmusic.adapter.SongListAdapter;
import com.gejiahui.toastmusic.model.APPConstant;
import com.gejiahui.toastmusic.model.Mp3Info;
import com.gejiahui.toastmusic.service.MusicService;
import com.gejiahui.toastmusic.utensil.Helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gejiahui on 2015/12/9.
 */
public class PlayListFragment extends Fragment {
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
    int duration = 0;
    static boolean isPlaying = false;
    boolean isPause = false;
    List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
    Helper helper;
    MusicReceiver mReceiver;
    public PlayListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        helper = new Helper( getActivity());
        mp3Infos = helper.getMP3Info();

        mReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(APPConstant.MUSIC_CURRENT);
        intentFilter.addAction(APPConstant.MUSIC_DURATION);
        getActivity().registerReceiver(mReceiver, intentFilter);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v("gjh", "!!!!!!!!!!!!!!!!!!!!!");
        outState.putInt("duration",duration);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if(savedInstanceState != null)
        {
            duration = savedInstanceState.getInt("duration");
        }
        view = inflater.inflate(R.layout.play_fragment,container,false);
        initView(view);
        setListener();
        if (isPlaying) {
            showSongTime(true);
        }
        return view;
    }

    /**
     * init the widgets
     */
    private void initView(View view) {

        btnAudioPlay = (Button)view.findViewById(R.id.btnAudioPlay);
        btnNextSong = (Button) view.findViewById(R.id.btnNextSong);
        btnPreSong = (Button) view.findViewById(R.id.btnPreSong);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        songsList = (ListView) view.findViewById(R.id.songsList);
        btnStop = (Button) view.findViewById(R.id.btnStop);
        txt_songDuration = (TextView) view.findViewById(R.id.songDuration);
        txt_currentTime = (TextView) view.findViewById(R.id.playtime);
        songTime = (LinearLayout) view.findViewById(R.id.song_time);
        musicSeekbar = (SeekBar) view.findViewById(R.id.musicSeekbar);
        SongListAdapter adapter = new SongListAdapter(getActivity(), mp3Infos);
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

    }
    /**
     * button click listener
     */
    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MusicService.class);
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
                    getActivity().startService(intent);
                    break;
                case R.id.btnNextSong:
                    currentPosition++;
                    if(currentPosition >= mp3Infos.size()){
                        currentPosition = 0;
                    }

                    intent.putExtra("msg", APPConstant.PLAY);
                    intent.putExtra("currentPosition", currentPosition);
                    getActivity().startService(intent);
                    showSongTime(true);
                    break;

                case R.id.btnPreSong:
                    currentPosition--;
                    if(currentPosition < 0){
                        currentPosition = mp3Infos.size()-1;
                    }
                    intent.putExtra("msg", APPConstant.PLAY);
                    intent.putExtra("currentPosition", currentPosition);
                    getActivity().startService(intent);
                    showSongTime(true);
                    break;
                case R.id.btnStop:
                    isPlaying = false;
                    isPause = false;
                    intent.putExtra("msg", APPConstant.STOP);
                    getActivity().startService(intent);
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
            Intent intent = new Intent(getActivity(), MusicService.class);
            intent.putExtra("currentPosition", position);
            getActivity().startService(intent);
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

            Intent intent = new Intent(getActivity(), MusicService.class);
            intent.putExtra("seekPosition", musicSeekbar.getProgress());
            intent.putExtra("msg", APPConstant.SLIDEING);
            getActivity().startService(intent);


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
                duration = intent.getIntExtra("duration", 0);
                Bundle b = getArguments();
                if(b != null){
                    b.putInt("duration", duration);  //更新参数
                }
                currentPosition = intent.getIntExtra("currentPosition", 0);
                musicSeekbar.setMax(duration);
                txt_songDuration.setText(msToMinutes(duration));
                Log.v("gjh", "歌曲更新");
                Log.v("gjh", "max:" + musicSeekbar.getMax());
            }

        }
    }
}
