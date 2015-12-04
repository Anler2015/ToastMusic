package com.gejiahui.toastmusic.utensil;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.gejiahui.toastmusic.adapter.SongListAdapter;
import com.gejiahui.toastmusic.model.Mp3Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gejiahui on 2015/12/4.
 */
public class Helper {
    Context context;
    public Helper(Context context) {
        this.context = context;
    }

    public   List<Mp3Info> getMP3Info() {
        List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        Cursor mp3Curosr = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //if mp3Curosr == null , return
        if (null == mp3Curosr) {
            Log.v("gjh","#############");
            return null;
        }
        mp3Curosr.moveToFirst();

        for (int i = 0; i < mp3Curosr.getCount(); i++) {
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

        return mp3Infos;

    }

}
