package cn.ixuehu.ultraplayer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 项目名：UltraPlayer
 * 包名：cn.ixuehu.ultraplayer.util
 * Created by daimaren on 2016/1/22.
 */
public class StringUtil {
    /**
     * 转换时间
     * @param duration
     * @return
     */
    public static String formatVideoDuration(long duration)
    {
        int HOUR = 1000*60*60;//1小时
        int MINUTE = 1000*60;//1分钟
        int SECOND = 1000;//1秒

        int remainTime = 0;
        //1.算出多少小时
        int hour = (int) (duration/HOUR);//算出多少小时
        remainTime = (int) (duration%HOUR);//得到算完小时后剩余的时间

        //2.拿算完小时后剩余的时间，再算分钟
        int minute = remainTime/MINUTE;//算分钟
        remainTime = remainTime%MINUTE;//得到算完分钟后剩余的时间

        //3.拿算完分钟后剩余的时间，再算秒
        int second = remainTime/SECOND;//算秒

        if(hour==0){
            //显示22:33
            return String.format("%02d:%02d", minute,second);
        }else {
            //显示01:22:33
            return String.format("%02d:%02d:%02d",hour, minute,second);
        }
    }
    /**
     * 获得格式化之后的系统时间
     * @return
     */
    public static String formatSystemTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 去掉name的.mp3
     * @param name
     * @return
     */
    public static String formatAudioName(String name)
    {
        return name.substring(0,name.lastIndexOf("."));
    }
}
