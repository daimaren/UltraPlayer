package cn.ixuehu.ultraplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.base.BaseActivity;
import cn.ixuehu.ultraplayer.bean.VideoItem;
import cn.ixuehu.ultraplayer.util.LogUtil;
import cn.ixuehu.ultraplayer.util.StringUtil;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.ui.activity
 * Created by daimaren on 2016/1/22.
 */
public class VideoPlayerActivity extends BaseActivity{
    private VideoView videoView;
    private ImageView btn_exit,btn_pre,btn_play,btn_next,btn_screen;
    private TextView tv_name,tv_time;
    private ImageView iv_battery;
    private ImageView iv_volume;
    private SeekBar volumn_seekbar;
    //底部控制面板控件
    private TextView tv_current_position,tv_total_time;
    private SeekBar play_seekbar;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    private final int MESSAGE_UPDATE_TIME = 0;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what)
            {
                case MESSAGE_UPDATE_TIME:
                    updateSymTime();
                    break;
            }
            return false;
        }
    });
    private AudioManager audioManager;
    private int streamMaxVolume;
    private int currentVolume;
    private boolean isMute = false;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_video_player);
        videoView = (VideoView) findViewById(R.id.videoView);

        btn_exit = (ImageView) findViewById(R.id.btn_exit);
        btn_pre = (ImageView) findViewById(R.id.btn_pre);
        btn_play = (ImageView) findViewById(R.id.btn_play);
        btn_next = (ImageView) findViewById(R.id.btn_next);
        btn_screen = (ImageView) findViewById(R.id.btn_screen);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        iv_battery = (ImageView) findViewById(R.id.iv_battery);
        iv_volume = (ImageView) findViewById(R.id.iv_volume);
        volumn_seekbar = (SeekBar) findViewById(R.id.volumn_seekbar);
        tv_current_position = (TextView) findViewById(R.id.tv_current_position);
        tv_total_time = (TextView) findViewById(R.id.tv_total_time);
        play_seekbar = (SeekBar) findViewById(R.id.play_seekbar);
    }

    @Override
    protected void initListener() {
        iv_volume.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_screen.setOnClickListener(this);
        volumn_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                {
                    isMute =false;
                    //是认为修改
                    seekBar.setProgress(i);
                    currentVolume = i;
                    updateVolume();

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
        VideoItem videoItem = (VideoItem) getIntent().getExtras().getSerializable("VideoItem");
        registerBatteryBroadcastReceiver();
        //设置标题
        tv_name.setText(videoItem.getTitle());
        updateSymTime();
        initVolume();
        //播放视频
        videoView.setVideoURI(Uri.parse(videoItem.getPath()));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        //videoView.setMediaController(new MediaController());
    }

    /**
     * 获取最大音量、当前音量初始化SeekBar
     */
    private void initVolume()
    {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumn_seekbar.setMax(streamMaxVolume);
        volumn_seekbar.setProgress(currentVolume);
    }
    private void registerBatteryBroadcastReceiver()
    {
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra("level", 0);
                LogUtil.e(this,"level"+level);
                updateBatteryStatus(level);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

    }
    private void updateSymTime()
    {
        tv_time.setText(StringUtil.formatSystemTime());
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIME, 1000);
    }
    private void updateVolume()
    {
        if (isMute)
        {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            volumn_seekbar.setProgress(0);
        }
        else
        {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,0);
            volumn_seekbar.setProgress(currentVolume);
        }

    }
    @Override
    protected void processClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_exit:
                break;
            case R.id.btn_play:
                if (videoView.isPlaying())
                {
                    videoView.pause();
                }
                else
                {
                    videoView.start();
                }
                updatePlayBtnBg();
                break;
            case R.id.iv_volume:
                isMute = ! isMute;
                updateVolume();
                break;
        }
    }

    /**
     * 更新播放按钮背景
     */
    private void updatePlayBtnBg()
    {
        btn_play.setBackgroundResource(videoView.isPlaying()?R.drawable.selector_btn_pause:R.drawable.selector_btn_play);
    }
    private void updateBatteryStatus(int level)
    {
        if(level<=0){
            iv_battery.setBackgroundResource(R.drawable.ic_battery_0);
        }else if (level>0 && level<=10) {
            iv_battery.setBackgroundResource(R.drawable.ic_battery_10);
        }else if (level>10 && level<20) {
            iv_battery.setBackgroundResource(R.drawable.ic_battery_20);
        }else if (level>20 && level<=40) {
            iv_battery.setBackgroundResource(R.drawable.ic_battery_40);
        }else if (level>40 && level<=60) {
            iv_battery.setBackgroundResource(R.drawable.ic_battery_60);
        }else if (level>60 && level<=80) {
            iv_battery.setBackgroundResource(R.drawable.ic_battery_80);
        }else  {
            iv_battery.setBackgroundResource(R.drawable.ic_battery_100);
        }
    }

    }
}
