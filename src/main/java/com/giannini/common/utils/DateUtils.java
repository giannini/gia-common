package com.giannini.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * date日期格式处理
 *
 * @author giannini
 */
public final class DateUtils {

    /**
     * 默认时区
     */
    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone
            .getTimeZone("GMT+8:00");

    /**
     * 线程本地变量
     */
    private static ThreadLocal<DateFormat> dfLocal = new ThreadLocal<DateFormat>();

    /** 构造函数 */
    private DateUtils() {};

    /**
     * Long转Date
     * 
     * @param milliSecond
     *            毫秒
     * @return
     */
    public static Date getDate(Long milliSecond) {
        if (milliSecond == null || milliSecond <= 0) {
            return null;
        } else {
            return new Date(milliSecond);
        }
    }

    /**
     * Date转Long(毫秒)
     * 
     * @param date
     * @return
     */
    public static Long getMiliSecond(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    /**
     * 解析简单格式('yyyy-MM-dd')的字符串到日期对象(线程安全)
     * 
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date parseSimpleDateString(String str) throws ParseException {
        if (str == null) {
            return null;
        } else {
            DateFormat df = dfLocal.get();
            if (df == null) {
                df = new SimpleDateFormat("yyyy-MM-dd");
                dfLocal.set(df);
            }
            return df.parse(str);
        }
    }

    /**
     * 按照指定的格式解析字符串到Date类型
     * <p>
     * 参考jdk文档：http://docs.oracle.com/javase/7/docs/api/java/text/
     * SimpleDateFormat.html
     * 
     * @param timeStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parseDateString(String timeStr, String format)
            throws ParseException {
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(timeStr);
    }

    /**
     * 自动将指定的时间参数值(字符串类型)转换到毫秒单位时间值(long类型)
     * <p>
     * 允许以下参数值中包含以下时间单位后缀(单个字符串只包含一个单位)
     * <ul>
     * <li>'d' 或 'D': 天
     * <li>'h' 或 'H': 小时
     * <li>'m' 或 'M': 分
     * <li>'s' 或 'S': 秒
     * <li>'ms' 或 无单位后缀 : 毫秒
     * </ul>
     * <p>
     * 例如 15m 转换后 900000L
     * 
     * @param timeStr
     *            时间参数值(字符串类型)
     * @return 毫秒单位时间值(long类型), 异常返回-1
     */
    public static long strToMilliseconds(String timeStr) {
        long ms = -1L;
        if (timeStr == null || timeStr.trim().length() == 0) {
            return ms;
        }

        // 获取单位，单位最长为2个字符(ms)，也可能没有
        int unitIndex = timeStr.length() - 1;
        String unit;
        while (!Character.isDigit(timeStr.charAt(unitIndex))) {
            unitIndex--;
        }
        if (unitIndex == (timeStr.length() - 1)) {
            unit = "ms";
        } else {
            unit = timeStr.substring(unitIndex + 1).trim();
        }

        // 获取数值
        Long value = Long.parseLong(timeStr.substring(0, unitIndex + 1).trim());
        if (unit.equalsIgnoreCase("ms")) {
            return value;
        }

        // 转换成毫秒数量级
        switch (unit.charAt(0)) {
            case 'd':
            case 'D':
                return TimeUnit.DAYS.toMillis(value);
            case 'h':
            case 'H':
                return TimeUnit.HOURS.toMillis(value);
            case 'm':
            case 'M':
                return TimeUnit.MINUTES.toMillis(value);
            case 's':
            case 'S':
                return TimeUnit.MINUTES.toMillis(value);
            default:
                break;
        }
        return -1L;
    }

    /**
     * 将指定的毫秒单位时间值(long类型)转换到带时间参数单位值(字符串类型)
     * <p>
     * 根据指定时间指将自动添加以下时间单位后缀:
     * <ul>
     * <li>'d': 天
     * <li>'h': 小时
     * <li>'m': 分
     * <li>'s': 秒
     * <li>'ms' : 毫秒
     * </ul>
     * 
     * @param millis
     *            毫秒单位时间值(long类型)
     * @return 带时间参数单位值(字符串类型)
     */
    public static String milliSecondToStr(long millis) {
        if (millis < 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        
        // day
        if (millis >= 24 * 60 * 60 * 1000) {
            long days = TimeUnit.MILLISECONDS.toDays(millis);
            sb.append(days).append("d");
            millis -= days * 24 * 60 * 60 * 1000;
        }

        // hour
        if (millis >= 60 * 60 * 1000) {
            long hour = TimeUnit.MILLISECONDS.toHours(millis);
            sb.append(hour).append("h");
            millis -= hour * 60 * 60 * 1000;
        }

        // minutes
        if (millis >= 60 * 1000) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            sb.append(minutes).append("m");
            millis -= minutes * 60 * 1000;
        }

        // second
        if (millis > 1000) {
            long second = TimeUnit.MILLISECONDS.toSeconds(millis);
            sb.append(second).append("s");
            millis -= second * 1000;
        }

        // ms
        if (millis > 0) {
            sb.append(millis).append("ms");
        }
        return sb.toString();
    }

    /**
     * 时区解析
     * 
     * @param id
     * @return
     */
    public static final TimeZone tryParseTimeZone(String id) {
        return null;
    }
}
