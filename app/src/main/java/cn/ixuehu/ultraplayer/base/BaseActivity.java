package cn.ixuehu.ultraplayer.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.base
 * Created by daimaren on 2016/1/22.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    protected abstract void initView();
    protected abstract void initListener();
    protected abstract void initData();
    protected abstract void processClick(View view);
    @Override
    public void onClick(View view) {
        processClick(view);
    }
}
