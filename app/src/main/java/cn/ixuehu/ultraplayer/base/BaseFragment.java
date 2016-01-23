package cn.ixuehu.ultraplayer.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.base
 * Created by daimaren on 2016/1/20.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater, container, savedInstanceState);
        return view;
    }
    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void initListener();
    protected abstract void initData();
    protected abstract void processClick(View view);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        initData();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 不带数据
     * @param targetActivity
     */
    protected void enterActivity(Class<?> targetActivity)
    {
        //Activity跳转
        Intent intent = new Intent(getActivity(),targetActivity);
        startActivity(intent);
    }

    /**
     * 带数据
     * @param targetActivity
     * @param bundle
     */
    protected void enterActivity(Class<?> targetActivity,Bundle bundle)
    {
        //Activity跳转
        Intent intent = new Intent(getActivity(),targetActivity);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
