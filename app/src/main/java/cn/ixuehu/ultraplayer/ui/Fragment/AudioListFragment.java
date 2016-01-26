package cn.ixuehu.ultraplayer.ui.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.adapter.AudioListAdapter;
import cn.ixuehu.ultraplayer.base.BaseFragment;
import cn.ixuehu.ultraplayer.bean.AudioItem;
import cn.ixuehu.ultraplayer.db.SimpleQueryHandler;
import cn.ixuehu.ultraplayer.ui.activity.AudioPlayerActivity;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.ui.Fragment
 * Created by daimaren on 2016/1/21.
 */
public class AudioListFragment extends BaseFragment {
    private ListView listView;
    private SimpleQueryHandler queryHandler;
    private AudioListAdapter adapter;
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_list, null);
        listView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    protected void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //跳转到播放音乐界面
                Cursor cursor = (Cursor) adapter.getItem(i);
                //VideoItem videoItem = VideoItem.fromCursor(cursor);
                ArrayList<AudioItem> arrayList = cursorToList(cursor);

                Bundle bundle = new Bundle();
                bundle.putInt("currentPosition",i);
                bundle.putSerializable("audioList", arrayList);
                enterActivity(AudioPlayerActivity.class,bundle);
            }
        });
    }

    @Override
    protected void initData() {
        //查询本地歌曲
        adapter = new AudioListAdapter(getActivity(),null);
        listView.setAdapter(adapter);
        queryHandler = new SimpleQueryHandler(getActivity().getContentResolver());
        String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION};
        queryHandler.startQuery(0, adapter, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
    }

    @Override
    protected void processClick(View view) {

    }
    /**
     * 将Cursor数据解析出并放入集合
     * @param cursor
     * @return
     */
    private ArrayList<AudioItem> cursorToList(Cursor cursor)
    {
        ArrayList<AudioItem> arrayList = new ArrayList<AudioItem>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext())
        {
            arrayList.add(AudioItem.fromCursor(cursor));
        }
        return arrayList;
    }
}
