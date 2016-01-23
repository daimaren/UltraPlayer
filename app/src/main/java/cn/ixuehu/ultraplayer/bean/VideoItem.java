package cn.ixuehu.ultraplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.bean
 * Created by daimaren on 2016/1/22.
 */
public class VideoItem implements Serializable {
    private long size;
    private String title;
    private long duration;
    private String path;

    //Curcor转换为VideoItem
    public static VideoItem fromCursor(Cursor cursor)
    {
        VideoItem videoItem= new VideoItem();
        videoItem.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        videoItem.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
        videoItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        videoItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        return videoItem;
    }
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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

    @Override
    public String toString() {
        return "VideoItem [size=" + size + ", title=" + title + ", duration="
                + duration + ", path=" + path + "]";
    }
}
