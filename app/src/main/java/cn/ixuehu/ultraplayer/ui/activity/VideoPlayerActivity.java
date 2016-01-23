package cn.ixuehu.ultraplayer.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.base.BaseActivity;
import cn.ixuehu.ultraplayer.bean.VideoItem;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.ui.activity
 * Created by daimaren on 2016/1/22.
 */
public class VideoPlayerActivity extends BaseActivity{
    private VideoView videoView;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_video_player);
        videoView = (VideoView) findViewById(R.id.videoView);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        VideoItem videoItem = (VideoItem) getIntent().getExtras().getSerializable("VideoItem");
        //播放视频
        videoView.setVideoURI(Uri.parse(videoItem.getPath()));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    @Override
    protected void processClick(View view) {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }
}
