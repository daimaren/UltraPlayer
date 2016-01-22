package cn.ixuehu.ultraplayer.util;

import android.database.Cursor;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.util
 * Created by daimaren on 2016/1/22.
 */
public class CursorUtil {
    public static void printCursor(Cursor cursor)
    {
        //是否非空
        if (cursor == null)
            return;
        LogUtil.e("CursorUtil","共" + cursor.getCount() + "条数据");
        while(cursor.moveToNext())
        {
            //打印一个
            for (int i=0;i < cursor.getColumnCount();i++)
            {
                String columnName = cursor.getColumnName(i);
                String columnValue = cursor.getString(i);
                LogUtil.e("CursorUtil",columnName + ":" + columnValue);
            }
            LogUtil.e("CursorUtil", " =========================== ");
        }
    }
}
