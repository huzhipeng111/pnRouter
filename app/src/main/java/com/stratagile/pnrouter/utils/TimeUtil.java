package com.stratagile.pnrouter.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hu on 2017/5/5.
 */

public class TimeUtil {

    /**
     * 调此方法输入所要转换的时间输入例如（"2014-06-14 16:09:00"）返回时间戳
     *
     * @param time
     * @return
     */
    public static long timeStamp(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date;
//        KLog.i(time);
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
//            KLog.i(l);
            return l;
        } catch (Exception e) {
            e.printStackTrace();
            return new Long("123");
        }
    }

    /**
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdr.format(new Date(Calendar.getInstance().getTimeInMillis()));
    }
    public static String getCreateTime(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        return sdr.format(new Date(timeStamp * 1000));
    }
    public static String getFileListTime(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
        return sdr.format(new Date(timeStamp * 1000));
    }

    public static String getFileListTime1(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
        return sdr.format(new Date(timeStamp));
    }
    public static String getMMDDYY(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        return sdr.format(new Date(timeStamp));
    }
    public static String getHHMM(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return sdr.format(new Date(timeStamp));
    }
}
