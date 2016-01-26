package cn.ixuehu.ultraplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.bean
 * Created by daimaren on 2016/1/26.
 */
public class AudioItem implements Serializable{
    private String title;
    private long duration;
    private String path;
    private String artist;
    //AudioItem
    public static AudioItem fromCursor(Cursor cursor)
    {
        AudioItem audioItem = new AudioItem();
        audioItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
        audioItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        audioItem.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        audioItem.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        return audioItem;
    }
    @Override
    public String toString() {
        return "AudioItem [artist=" + artist + ", title=" + title + ", duration="
                + duration + ", path=" + path + "]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
