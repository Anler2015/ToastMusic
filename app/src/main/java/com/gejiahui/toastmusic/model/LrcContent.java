package com.gejiahui.toastmusic.model;

/**
 * Created by gejiahui on 2015/12/4.
 */
public class LrcContent {
    String lrcStr;  //歌词
    int lrcTime;        //播放的时间

    public String getLrcStr() {
        return lrcStr;
    }

    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }

    public int getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }
}
