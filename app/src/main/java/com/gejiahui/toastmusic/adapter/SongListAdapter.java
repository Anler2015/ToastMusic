package com.gejiahui.toastmusic.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gejiahui.toastmusic.R;
import com.gejiahui.toastmusic.model.Mp3Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gejiahui on 2015/11/23.
 */
public class SongListAdapter extends BaseAdapter {

    List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
    Context context;


    public SongListAdapter(Context context ,List<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null ;
        if(convertView == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.song_list_item,null);
        }
        else
        {
            view = convertView;
        }

        TextView songName = (TextView)view.findViewById(R.id.song_name);
        songName.setText(mp3Infos.get(position).getTitle());

        return view;
    }
}
