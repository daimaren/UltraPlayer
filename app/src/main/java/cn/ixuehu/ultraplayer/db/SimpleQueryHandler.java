package cn.ixuehu.ultraplayer.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

import cn.ixuehu.ultraplayer.util.CursorUtil;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.db
 * Created by daimaren on 2016/1/22.
 */
public class SimpleQueryHandler extends AsyncQueryHandler{
    public SimpleQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        CursorUtil.printCursor(cursor);
        //更新代码
        if (cookie != null && cookie instanceof CursorAdapter)
        {
            //LogUtil.e("CursorUtil","akfakgk");
            CursorAdapter adapter = (CursorAdapter) cookie;
            adapter.changeCursor(cursor);//  相当于notifyDatesetChange
        }
    }
}
