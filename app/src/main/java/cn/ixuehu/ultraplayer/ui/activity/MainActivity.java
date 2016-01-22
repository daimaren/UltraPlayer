package cn.ixuehu.ultraplayer.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.adapter.MainPagerAdapter;
import cn.ixuehu.ultraplayer.ui.Fragment.AudioListFragment;
import cn.ixuehu.ultraplayer.ui.Fragment.VideoListFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private TextView tab_video,tab_audio;
    private View indicate_line;
    private ViewPager viewPager;
    private MainPagerAdapter adapter;
    ArrayList<Fragment> framents = new ArrayList<Fragment>();
    private int mIndicateWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        initView();
        initListener();
        initData();
    }
    private void initView()
    {
        setContentView(R.layout.activity_main);
        tab_video = (TextView) findViewById(R.id.tab_video);
        tab_audio = (TextView) findViewById(R.id.tab_audio);
        indicate_line = findViewById(R.id.indicate_line);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }
    private void initListener()
    {
        //点击事件
        tab_video.setOnClickListener(this);
        tab_audio.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int targetPosition = position * mIndicateWidth + positionOffsetPixels / framents.size();
                //设置指示线移动
                ViewPropertyAnimator.animate(indicate_line).translationX(targetPosition).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                lightAndScaleTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void initData() {
        framents.add(new VideoListFragment());
        framents.add(new AudioListFragment());
        caculateIndicateLineWith();

        adapter = new MainPagerAdapter(getSupportFragmentManager(),framents);
        viewPager.setAdapter(adapter);
        //高亮并缩放标题
        lightAndScaleTitle();
    }
    /**
     * 高亮并缩放标题
     */
    private void lightAndScaleTitle()
    {
        int currentItem = viewPager.getCurrentItem();
        //字体颜色切换
        tab_video.setTextColor(currentItem == 0 ? getResources().getColor(R.color.indicate_line)
                :getResources().getColor(R.color.gray_white));
        tab_video.setTextColor(currentItem == 1 ? getResources().getColor(R.color.indicate_line)
                : getResources().getColor(R.color.gray_white));
        //字体缩放切换
        ViewPropertyAnimator.animate(tab_video).scaleX(currentItem == 0 ? 1.2f : 1.0f).setDuration(200);
        ViewPropertyAnimator.animate(tab_video).scaleY(currentItem == 0 ? 1.2f : 1.0f).setDuration(200);

        ViewPropertyAnimator.animate(tab_audio).scaleX(currentItem == 1 ? 1.2f : 1.0f).setDuration(200);
        ViewPropertyAnimator.animate(tab_audio).scaleY(currentItem == 1 ? 1.2f : 1.0f).setDuration(200);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tab_video:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_audio:
                viewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    /**
     * 计算指示线的宽度
     */
    private void caculateIndicateLineWith()
    {
        //得到屏幕宽度
        int width = getWindowManager().getDefaultDisplay().getWidth();
        //单个选项卡宽度
        mIndicateWidth = width / framents.size();
        //设置线宽
        indicate_line.getLayoutParams().width = mIndicateWidth;
        indicate_line.requestLayout();
    }
}
