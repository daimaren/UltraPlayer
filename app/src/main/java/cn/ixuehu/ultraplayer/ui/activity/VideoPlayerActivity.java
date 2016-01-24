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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.base.BaseActivity;
import cn.ixuehu.ultraplayer.bean.VideoItem;
import cn.ixuehu.ultraplayer.ui.view.VideoView;
import cn.ixuehu.ultraplayer.util.StringUtil;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.ui.activity
 * Created by daimaren on 2016/1/22.
 */
public class VideoPlayerActivity extends BaseActivity{
    private VideoView videoView;
    //顶部控制控件
    private ImageView btn_exit,btn_pre,btn_play,btn_next,btn_screen;
    private TextView tv_name,tv_time;
    private ImageView iv_battery;
    private ImageView iv_volume;
    private SeekBar volumn_seekbar;
    private LinearLayout ll_top_control;
    //底部控制面板控件
    private TextView tv_current_position,tv_total_time;
    private SeekBar play_seekbar;
    private LinearLayout ll_bottom_control;
    //广播
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    private final int MESSAGE_UPDATE_TIME = 0;
    private final int MESSAGE_UPDATE_VIDEO = 1;
    private final int MESSAGE_HIDE_CONTROL = 2;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what)
            {
                case MESSAGE_UPDATE_TIME:
                    updateSymTime();
                    break;
                case MESSAGE_UPDATE_VIDEO:
                    updateVideoProgress();
                    break;
                case MESSAGE_HIDE_CONTROL:
                    hideControl();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private AudioManager audioManager;
    private int streamMaxVolume;
    private int currentVolume;
    private boolean isMute = false;
    private int downY;
    private int screenHeight;
    private int screenWidth;
    private ArrayList<VideoItem> arrayList;
    private int currentPosition;
    //手势检测
    private GestureDetector gestureDetector;
    private boolean isShowControl = false;
    private boolean isScreen = false;
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
        //线性布局
        ll_top_control = (LinearLayout) findViewById(R.id.ll_top_control);
        ll_bottom_control = (LinearLayout) findViewById(R.id.ll_bottom_control);
    }

    @Override
    protected void initListener() {
        iv_volume.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_screen.setOnClickListener(this);
        //View和自己的子View全部完成时回调此方法
        ll_top_control.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //及时移除监听器，是因为只要某个子view的宽高改变或者layout改变都会引起该方法回调
                ll_top_control.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //隐藏顶部控制界面(使用属性动画)
                ViewPropertyAnimator.animate(ll_top_control).translationY(-ll_top_control.getHeight()).setDuration(0);
            }
        });
        //View和自己的子View全部完成时回调此方法
        ll_bottom_control.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //及时移除监听器，是因为只要某个子view的宽高改变或者layout改变都会引起该方法回调
                ll_bottom_control.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //隐藏底部控制界面
                ViewPropertyAnimator.animate(ll_bottom_control).translationY(ll_bottom_control.getHeight()).setDuration(0);
            }
        });

        volumn_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    isMute = false;
                    //是认为修改
                    seekBar.setProgress(i);
                    currentVolume = i;
                    updateVolume();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(MESSAGE_HIDE_CONTROL);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CONTROL,5000)

            }
        });
        play_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                {
                    //改变视频进度条
                    videoView.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //播放完成
                btn_play.setBackgroundResource(R.drawable.selector_btn_play);
                //停止进度条更新
                play_seekbar.setProgress(videoView.getDuration());
                handler.removeMessages(MESSAGE_UPDATE_VIDEO);
            }
        });
    }

    @Override
    protected void initData() {
        gestureDetector = new GestureDetector(this,new MyGetsureListener());
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //final VideoItem videoItem = (VideoItem) getIntent().getExtras().getSerializable("VideoItem");
        arrayList = (ArrayList<VideoItem>) getIntent().getExtras().getSerializable("videoList");
        currentPosition = getIntent().getIntExtra("currentPosition",1);

        registerBatteryBroadcastReceiver();

        updateSymTime();
        initVolume();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
                //播放按钮变暂停
                btn_play.setBackgroundResource(R.drawable.selector_btn_pause);
                //初始化播放进度条
                play_seekbar.setMax(videoView.getDuration());
                tv_current_position.setText("00:00");
                tv_total_time.setText(StringUtil.formatVideoDuration(videoView.getDuration()));
                updateVideoProgress();
            }
        });
        //videoView.setMediaController(new MediaController());
    }
    class MyGetsureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //单击,隐藏显示控制界面
            if (isShowControl)
            {
                hideControl();
            }
            else
            {
                showControl();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //长按播放
            processClick(btn_play);
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //双击全屏
            processClick(btn_screen);
            return super.onDoubleTap(e);
        }
    }

    /**
     * 隐藏控制界面
     */
    private void hideControl()
    {
        ViewPropertyAnimator.animate(ll_top_control).translationY(0).setDuration(200);
        ViewPropertyAnimator.animate(ll_top_control).translationY(0).setDuration(200);
        isShowControl = false;
    }

    /**
     * 显示控制界面
     */
    private void showControl()
    {
        ViewPropertyAnimator.animate(ll_top_control).translationY(-ll_top_control.getHeight()).setDuration(200);
        ViewPropertyAnimator.animate(ll_top_control).translationY(ll_bottom_control.getHeight()).setDuration(200);
        isShowControl = true;

        //延时隐藏
        handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CONTROL,5000);
    }
    /**
     * 播放指定序号的视频
     * @param position
     */
    private void playVideo(int position)
    {
        //判断是否为空
        if (arrayList == null || arrayList.size() == 0)
        {
            //退出界面
            finish();
            return;
        }
        VideoItem videoItem = arrayList.get(position);
        //设置标题
        tv_name.setText(videoItem.getTitle());
        //播放视频
        videoView.setVideoURI(Uri.parse(videoItem.getPath()));

        //使能上一个、下一个
        /*btn_next.setVisibility(View.VISIBLE);
        selector_btn_defaultscreen.setVisibility(View.VISIBLE);*/
        btn_pre.setEnabled(position != 0);
        btn_next.setEnabled(position != arrayList.size() - 1);
    }
    private void updateVideoProgress()
    {
        play_seekbar.setProgress(videoView.getCurrentPosition());
        tv_current_position.setText(StringUtil.formatVideoDuration(videoView.getCurrentPosition()));
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_VIDEO, 1000);
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

    /**
     * 注册广播接收
     */
    private void registerBatteryBroadcastReceiver()
    {
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra("level", 0);
                //LogUtil.e("level","level"+level);
                updateBatteryStatus(level);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

    }

    /**
     * 更新显示时间
     */
    private void updateSymTime()
    {
        tv_time.setText(StringUtil.formatSystemTime());
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIME, 1000);
    }

    /**
     * 更新音量
     */
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

    /**
     * 处理按钮点击事件
     * @param view
     */
    @Override
    protected void processClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_play:
                if (videoView.isPlaying())
                {
                    videoView.pause();
                    handler.removeMessages(MESSAGE_UPDATE_VIDEO);
                }
                else
                {
                    videoView.start();
                    handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_VIDEO,1000);
                }
                updatePlayBtnBg();
                break;
            case R.id.iv_volume:
                isMute = ! isMute;
                updateVolume();
                break;
            case R.id.btn_screen:
                isScreen = ! isScreen;
                videoView.switchScreen();
                //改变背景图片
                btn_screen.setBackgroundResource(isScreen?R.drawable.selector_btn_defaultscreen:
                        R.drawable.selector_btn_fullscreen);
                break;
            default:
                break;

        }
    }

    /**
     * 播放上一个
     */
    private void playPre()
    {

    }
    /**
     * 播放下一个
     */
    private void playNext()
    {
        if (currentPosition == (arrayList.size() - 1))
            return;
        currentPosition++;
        playVideo(currentPosition);
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

    /**
     * 滑动调节音量
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                downY = (int) (event.getY() + 0.5f);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) (event.getY() + 0.5f);
                int diffY = moveY - downY;
                if (Math.abs(diffY) < 15)
                    break;
                //找到最小的
                int totalDistance = Math.min(screenHeight, screenWidth);
                float movePercent = Math.abs(diffY)/totalDistance;
                float moveVolume = movePercent * streamMaxVolume;//是特别小的一个值
                if (moveVolume < 0)
                {
                    currentVolume += 1;
                }
                else
                {
                    currentVolume -= 1;
                }
                isMute = false;
                updateVolume();
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(broadcastReceiver);
    }
}
