package cn.ixuehu.ultraplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.adapter
 * Created by daimaren on 2016/1/20.
 */
public class MainPagerAdapter extends FragmentPagerAdapter{
    private ArrayList<Fragment> framents;
    public MainPagerAdapter(FragmentManager fm, ArrayList<Fragment> framents) {
        super(fm);
        this.framents = framents;
    }

    @Override
    public Fragment getItem(int position) {
        return framents.get(position);
    }

    @Override
    public int getCount() {
        return framents.size();
    }
}
