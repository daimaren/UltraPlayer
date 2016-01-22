package cn.ixuehu.ultraplayer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;

import cn.ixuehu.ultraplayer.R;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.ui.activity
 * Created by daimaren on 2016/1/20.
 */
public class SplashActivity extends Activity{
    private boolean bHaveEnterMain = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        delayEnterMainActivity(true);
    }

    private void delayEnterMainActivity(boolean isDelay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //延时2秒进入主界面
                if (!bHaveEnterMain)
                {
                    bHaveEnterMain = true;
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
            }
        },isDelay ? 2000:0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
                delayEnterMainActivity(false);
                break;
            default:
                break;

        }
        return super.onTouchEvent(event);
    }
}
