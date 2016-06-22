package com.giannini.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
     * 允许以下参数值中包含以下时间单位后缀
     * <ul>
     * <li>'d' 或 'D': 天
     * <li>'h' 或 'H': 小时
     * <li>'m' 或 'M': 分
     * <li>'s' 或 'S': 秒
     * <li>'ms' 或 无单位后缀 : 毫秒
     * </ul>
     * 
     * @param timeStr
     *            时间参数值(字符串类型)
     * @return 毫秒单位时间值(long类型)
     * @throws ClassCastException
     *             非法的时间单位
     */

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

    /**
     * 只支持以下格式的时区ID:
     * <p>
     * <ul>
     * <li>"Sign Hours", 例如: "+8"
     * <li>"GMT Sign Hours : Minutes", 例如: "GMT+08:00"
     * <li>"GMT Sign Hours Minutes", 例如: "GMT+0800"
     * <li>"GMT Sign Hours", 例如: "GMT+08"
     * </ul>
     * 
     * @param id
     *            a string of the <a href="#CustomID">custom ID form</a>.
     * @return 有效时区对象(null=无法识别的时区ID)
     */

}
