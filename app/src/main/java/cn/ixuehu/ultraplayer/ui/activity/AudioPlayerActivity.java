package cn.ixuehu.ultraplayer.ui.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.base.BaseActivity;
import cn.ixuehu.ultraplayer.bean.AudioItem;
import cn.ixuehu.ultraplayer.service.AudioPlayService;
import cn.ixuehu.ultraplayer.util.StringUtil;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.ui.activity
 * Created by daimaren on 2016/1/26.
 */
public class AudioPlayerActivity extends BaseActivity{
    private ImageView iv_anim,btn_back;
    private ImageView btn_play,btn_paly_mode,btn_pre,btn_next;
    private TextView tv_title,tv_artist,tv_time;
    private SeekBar seekbar;

    private ArrayList<AudioItem> audioItemArrayList;
    private int currentPosition = 0;

    private AudioPlayConnection audioPlayConnection;
    private AudioPlayService.AudioServiceBinder audioServiceBinder;
    private AudioServiceBroadcastReceiver audioServiceBroadcastReceiver;
    private static final int MESSAGE_UPDATE_PROGRESS = 0;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what)
            {
                case MESSAGE_UPDATE_PROGRESS:
                    updatePlayProgress();
                    break;
            }
            return false;
        }
    });
    @Override
    protected void initView() {
        setContentView(R.layout.activity_audio_player);
        btn_play = (ImageView) findViewById(R.id.btn_play);
        btn_paly_mode = (ImageView) findViewById(R.id.btn_paly_mode);
        btn_pre = (ImageView) findViewById(R.id.btn_play_pre);
        btn_next = (ImageView) findViewById(R.id.btn_next);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        seekbar = (SeekBar) findViewById(R.id.seekbar);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        iv_anim = (ImageView) findViewById(R.id.iv_anim);

        //开始播放动画
        AnimationDrawable drawable = (AnimationDrawable) iv_anim.getBackground();
        drawable.start();
    }

    @Override
    protected void initListener() {
        btn_back.setOnClickListener(this);
        btn_paly_mode.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //人为拖动，改变显示时间
                    tv_time.setText(StringUtil.formatVideoDuration(i)+":/" +
                            StringUtil.formatVideoDuration(audioServiceBinder.getDuration()));
                    //改变播放时间
                    audioServiceBinder.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void initData() {
        registerReceiver();
        //Intent获取传递的数据
        audioItemArrayList = (ArrayList<AudioItem>) getIntent().getExtras().getSerializable("audioList");
        currentPosition = getIntent().getIntExtra("currentPosition",0);
        Log.d("AudioPlayerActivity", audioItemArrayList.get(currentPosition).getPath());
        //绑定AudioPlayService
        Intent intent = new Intent(this, AudioPlayService.class);
        //给Service传递音乐列表数据
        Bundle bundle = new Bundle();
        bundle.putSerializable("audioList", audioItemArrayList);
        bundle.putInt("currentPosition", currentPosition);
        intent.putExtras(bundle);
        startService(intent);
        audioPlayConnection = new AudioPlayConnection();
        bindService(intent, audioPlayConnection, Service.BIND_AUTO_CREATE);
    }
    private void registerReceiver()
    {
        audioServiceBroadcastReceiver = new AudioServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(AudioPlayService.ACTION_MEDIA_PREPARED);
        intentFilter.addAction(AudioPlayService.ACION_MEDIA_COMPLETION);
        intentFilter.addAction(AudioPlayService.ACION_MEDIA_FIRST);
        intentFilter.addAction(AudioPlayService.ACION_MEDIA_LAST);
        registerReceiver(audioServiceBroadcastReceiver, intentFilter);
    }
    class AudioServiceBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioPlayService.ACTION_MEDIA_PREPARED.equals(intent.getAction()))
            {
                //解析Service发来的准备完成数据
                AudioItem audioItem = (AudioItem) intent.getExtras().getSerializable("audioItem");
                seekbar.setMax((int) audioItem.getDuration());
                tv_time.setText("00:00/" + StringUtil.formatVideoDuration(audioItem.getDuration()));
                //设置音乐标题、Artist
                tv_title.setText(StringUtil.formatAudioName(audioItem.getTitle()));
                tv_artist.setText(audioItem.getArtist());
                btn_play.setBackgroundResource(R.drawable.selector_btn_audio_pause);
                //准备完成后开始更新
                updatePlayProgress();
            }
            else if (AudioPlayService.ACION_MEDIA_COMPLETION.equals(intent.getAction()))
            {
                AudioItem audioItem = (AudioItem) intent.getExtras().getSerializable("audioItem");

                seekbar.setProgress((int) audioItem.getDuration());
                tv_time.setText(StringUtil.formatVideoDuration(audioItem.getDuration())+"/"
                        +StringUtil.formatVideoDuration(audioItem.getDuration()));
                btn_play.setBackgroundResource(R.drawable.selector_btn_audio_play);
            }
            else if (AudioPlayService.ACION_MEDIA_FIRST.equals(intent.getAction()))
            {
                Toast.makeText(AudioPlayerActivity.this, "当前是第一首", 0).show();
            }
            else if (AudioPlayService.ACION_MEDIA_LAST.equals(intent.getAction()))
            {
                Toast.makeText(AudioPlayerActivity.this, "当前是最后一首", 0).show();
            }
        }
    }
    class AudioPlayConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            audioServiceBinder = (AudioPlayService.AudioServiceBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    @Override
    protected void processClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_paly_mode:
                audioServiceBinder.switchMode();
                updatePlayModeBg();
                break;
            case R.id.btn_play:
                if (audioServiceBinder.isPlaying())
                {
                    //pause
                    audioServiceBinder.pause();
                }
                else
                    audioServiceBinder.start();
                updatePlayBtnBg();
                break;
            case R.id.btn_pre:
                audioServiceBinder.playPre(true);
                break;
            case R.id.btn_next:
                audioServiceBinder.playNext(true);
                break;
            default:
                break;
        }

    }
    private void updatePlayBtnBg()
    {
        btn_play.setBackgroundResource(audioServiceBinder.isPlaying()?R.drawable.selector_btn_audio_pause
                :R.drawable.selector_btn_audio_play);
    }
    private void updatePlayModeBg()
    {
        int currentPlayMode = audioServiceBinder.getCurrentPlayMode();
        switch (currentPlayMode)
        {
            case AudioPlayService.MODE_ORDER:
                btn_paly_mode.setBackgroundResource(R.drawable.selector_audio_mode_normal);
                break;
            case AudioPlayService.MODE_SINGLE_REPEAT:
                btn_paly_mode.setBackgroundResource(R.drawable.selector_audio_mode_single_repeat);
                break;
            case AudioPlayService.MODE_ALL_REPEAT:
                btn_paly_mode.setBackgroundResource(R.drawable.selector_audio_mode_all_repeat);
                break;
            default:
                break;
        }
    }
    private void updatePlayProgress()
    {
        //更新时间
        String currentTime = StringUtil.formatVideoDuration(audioServiceBinder.getCurrentPosition());
        tv_time.setText(currentTime + ":/" + StringUtil.formatVideoDuration(audioServiceBinder.getDuration()));
        //更新seekbar
        seekbar.setProgress((int) audioServiceBinder.getCurrentPosition());
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS,1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
