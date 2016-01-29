package cn.ixuehu.ultraplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import cn.ixuehu.ultraplayer.bean.AudioItem;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.service
 * Created by daimaren on 2016/1/28.
 */
public class AudioPlayService extends Service{
    public static final String ACTION_MEDIA_PREPARED = "ACTION_MEDIA_PREPARED";
    public static final String ACION_MEDIA_COMPLETION = "ACION_MEDIA_COMPLETION";
    public static final String ACION_MEDIA_FIRST = "ACION_MEDIA_FIRST";
    public static final String ACION_MEDIA_LAST = "ACION_MEDIA_LAST";
    //播放模式
    public static final int MODE_ORDER = 0;
    public static final int MODE_SINGLE_REPEAT = 1;
    public static final int MODE_ALL_REPEAT = 2;
    private int currentPlayMode = MODE_ORDER;

    private AudioServiceBinder audioServiceBinder;
    private ArrayList<AudioItem> audioItemArrayList;
    private int currentPosition = 0;
    private MediaPlayer mediaPlayer;
    @Override
    public void onCreate() {
        super.onCreate();
        audioServiceBinder = new AudioServiceBinder();
        audioItemArrayList = new ArrayList<AudioItem>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        audioItemArrayList = (ArrayList<AudioItem>) intent.getExtras().getSerializable("audioList");
        currentPosition = intent.getIntExtra("currentPosition",0);
        Log.d("AudioPlayService",audioItemArrayList.get(currentPosition).getPath());

        audioServiceBinder.openAudio();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return audioServiceBinder;
    }
    public class AudioServiceBinder extends Binder
    {
        /**
         * 播放一首音乐
         */
        public void openAudio()
        {
            if (mediaPlayer != null)
            {
                mediaPlayer.release();
                mediaPlayer =null;
            }
            mediaPlayer = new MediaPlayer();
            try {
                //设置音乐地址
                mediaPlayer.setDataSource(audioItemArrayList.get(currentPosition).getPath());
                mediaPlayer.prepareAsync();
                Log.d("AudioPlayService", "prepareAsync");
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        //准备完成后开始播放
                        mediaPlayer.start();
                        notifyPrepared();
                        Log.d("AudioPlayService", "notifyPrepared");
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        notifyCompletion();
                        autoPlayByMode();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public boolean isPlaying()
        {
            return mediaPlayer != null ? mediaPlayer.isPlaying():false;
        }
        public void pause()
        {
            if (mediaPlayer != null)
                mediaPlayer.pause();
        }
        public void start()
        {
            if (mediaPlayer != null)
                mediaPlayer.start();;
        }
        public long getCurrentPosition()
        {
            if (mediaPlayer != null)
                return mediaPlayer.getCurrentPosition();
            else
                return 0;
        }
        public long getDuration()
        {
            return mediaPlayer != null ? mediaPlayer.getDuration():0;
        }
        public void seekTo(int position)
        {
            if (mediaPlayer != null)
                mediaPlayer.seekTo(position);
        }
        public void playNext(boolean isShowTips){
            if(currentPosition<(audioItemArrayList.size()-1)){
                currentPosition++;
                openAudio();
            }else {
                if(isShowTips){
                    notifyFirstAndLast(ACION_MEDIA_LAST);
                }
            }
        }

        public void playPre(boolean isShowTips){
            if(currentPosition>0){
                currentPosition--;
                openAudio();
            }else {
                if(isShowTips){
                    notifyFirstAndLast(ACION_MEDIA_FIRST);
                }
            }
        }
        public void switchMode()
        {
            if (currentPlayMode == MODE_ORDER)
                currentPlayMode = MODE_SINGLE_REPEAT;
            else if (currentPlayMode == MODE_SINGLE_REPEAT)
                currentPlayMode = MODE_ALL_REPEAT;
            else if (currentPlayMode == MODE_ALL_REPEAT)
                currentPlayMode = MODE_ORDER;
        }
        public int getCurrentPlayMode()
        {
            return currentPlayMode;
        }
    }
    /**
     * 通知准备完成
     */
    private void notifyPrepared()
    {
        Intent intent = new Intent(ACTION_MEDIA_PREPARED);
        Bundle bundle = new Bundle();
        AudioItem audioItem = audioItemArrayList.get(currentPosition);
        bundle.putSerializable("audioItem",audioItem);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 通知播放完成
     */
    private void notifyCompletion(){
        Intent intent = new Intent(ACION_MEDIA_COMPLETION);
        Bundle bundle = new Bundle();
        bundle.putSerializable("audioItem", audioItemArrayList.get(currentPosition));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * 通知是否是第一个和最后一个
     */
    private void notifyFirstAndLast(String action)
    {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 一首歌曲完成后自动根据模式继续播放
     */
    private void autoPlayByMode()
    {
        if (currentPlayMode == MODE_ORDER)
            audioServiceBinder.playNext(false);
        else if (currentPlayMode == MODE_SINGLE_REPEAT)
            mediaPlayer.start();
        else if (currentPlayMode == MODE_ALL_REPEAT)
        {
            //全部循环播放
            if (currentPosition == (audioItemArrayList.size() - 1) )
            {
                currentPosition = 0;
                audioServiceBinder.openAudio();
            }
            else
                audioServiceBinder.playNext(false);
        }
    }
}
