package cn.ixuehu.ultraplayer.util;

import android.util.Log;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.util
 * Created by daimaren on 2016/1/22.
 */
public class LogUtil {
    private static boolean isDebug = true;
    public static void i(String tag,String msg)
    {
        if (isDebug)
        {
            Log.i(tag,msg);
        }
    }
    public static void i(Object object,String msg)
    {
        if (isDebug)
        {
            Log.i(object.getClass().getSimpleName(),msg);
        }
    }
    public static void e(String tag,String msg)
    {
        if (isDebug)
        {
            Log.e(tag,msg);
        }
    }
    public static void e(Object object,String msg)
    {
        if (isDebug)
        {
            Log.e(object.getClass().getSimpleName(),msg);
        }
    }
}
