package cn.ixuehu.ultraplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cn.ixuehu.ultraplayer.R;
import cn.ixuehu.ultraplayer.bean.AudioItem;
import cn.ixuehu.ultraplayer.util.StringUtil;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.adapter
 * Created by daimaren on 2016/1/26.
 */
public class AudioListAdapter extends CursorAdapter {
    ViewHolder holder;
    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //
        holder = getViewHolder(view);
        holder.tv_artist.setText(AudioItem.fromCursor(cursor).getArtist());
        holder.tv_name.setText(StringUtil.formatAudioName(AudioItem.fromCursor(cursor).getTitle()));
    }
    private ViewHolder getViewHolder(View view)
    {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null)
        {
            //没有复用
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return viewHolder;
        }
        else
        {
            //有复用
            return viewHolder;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //View 初始化
        return View.inflate(context,R.layout.adapter_audio_layout,null);
    }
    class ViewHolder
    {
        private TextView tv_name,tv_artist;
        //初始化
        private ViewHolder(View view)
        {
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_artist = (TextView) view.findViewById(R.id.tv_artist);
        }

    }
}
