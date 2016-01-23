package cn.ixuehu.ultraplayer.ui.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.adapter.VideoListAdapter;
import cn.ixuehu.ultraplayer.base.BaseFragment;
import cn.ixuehu.ultraplayer.bean.VideoItem;
import cn.ixuehu.ultraplayer.db.SimpleQueryHandler;
import cn.ixuehu.ultraplayer.ui.activity.VideoPlayerActivity;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.ui.Fragment
 * Created by daimaren on 2016/1/21.
 */
public class VideoListFragment extends BaseFragment {
    private ListView listView;
    private SimpleQueryHandler queryHandler;
    private VideoListAdapter adapter;
    @Override
    protected void processClick(View view) {

    }

    @Override
    protected void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //点击事件,界面跳转，传递视频数据
                Cursor cursor = (Cursor) adapter.getItem(i);
                VideoItem videoItem = VideoItem.fromCursor(cursor);

                Bundle bundle = new Bundle();
                bundle.putSerializable("VideoItem", videoItem);
                enterActivity(VideoPlayerActivity.class,bundle);
            }
        });
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //返回View
        View view = inflater.inflate(R.layout.fragment_video_list,null);
        listView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    protected void initData() {
        adapter = new VideoListAdapter(getActivity(), null);
        listView.setAdapter(adapter);

        queryHandler = new SimpleQueryHandler(getContext().getContentResolver());
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA};
        //Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                //projection, null, null, null);
        //CursorUtil.printCursor(cursor);
        queryHandler.startQuery(0,adapter, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
    }
}

