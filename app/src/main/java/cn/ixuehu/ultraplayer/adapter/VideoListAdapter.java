package cn.ixuehu.ultraplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.bean.VideoItem;
import cn.ixuehu.ultraplayer.util.StringUtil;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.adapter
 * Created by daimaren on 2016/1/22.
 */
public class VideoListAdapter extends CursorAdapter{
    private ViewHolder viewHolder;
    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return View.inflate(context, R.layout.adapter_video_list,null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        viewHolder = getViewHolder(view);
        //设置数据
        VideoItem videoItem = VideoItem.fromCursor(cursor);

        viewHolder.tv_title.setText(videoItem.getTitle());
        viewHolder.tv_duration.setText(videoItem.getDuration() + "");
        viewHolder.tv_size.setText(StringUtil.formatVideoDuration(videoItem.getSize()));
    }
    private ViewHolder getViewHolder(View view)
    {
        //检查是否有复用
        viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null)
        {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }
    public class ViewHolder
    {
        TextView tv_title,tv_duration,tv_size;
        public ViewHolder(View view)
        {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
        }
    }
}
