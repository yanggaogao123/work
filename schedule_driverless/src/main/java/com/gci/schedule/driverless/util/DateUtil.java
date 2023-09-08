package com.gci.schedule.driverless.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static final String yyyyMMdd = "yyyyMMdd";

    public static final String yyyyMMddHHmm = "yyyyMMddHHmm";

    public static final String yyyyMMdd_HHmmss = "yyyyMMdd HHmmss";

    public static final String hhmm = "HHmm";

    public static final String date_sdf = "yyyy-MM-dd";

    public static final String time_sdf = "yyyy-MM-dd HH:mm:ss";

    public static final String yyyy = "yyyy";

    public static long hhmm2Ts(String hhmm) {
        String[] hhmmArray = new String[2];
        if (hhmm.contains(":")) {
            hhmmArray = hhmm.split(":");
        } else if (hhmm.contains(";")) {
            hhmmArray = hhmm.split(";");
        } else {
            hhmmArray[0] = hhmm.substring(0, 2);
            hhmmArray[1] = hhmm.substring(hhmm.length() - 2);
        }
        return (Long.parseLong(hhmmArray[0]) * 3600 * 1000)
                + (Long.parseLong(hhmmArray[1]) * 60 * 1000);
    }

    private static Calendar getDayCalendar(Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTimeInMillis(dateTime.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private static Calendar getHourCalendar(Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTimeInMillis(dateTime.getTime());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Date getHourStartTime(Date dateTime) {
        Calendar calendar = getHourCalendar(dateTime);
        return calendar.getTime();
    }

    public static Long getDailyStartTime(Date dateTime) {
        Calendar calendar = getDayCalendar(dateTime);
        return calendar.getTimeInMillis();
    }

    public static Date getDailyStartDate(Date dateTime) {
        Calendar calendar = getDayCalendar(dateTime);
        return calendar.getTime();
    }

    public static Date getTodayHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 去掉秒分时间
     *
     * @param ts
     * @return
     */
    public static Date subSeconds(Long ts) {
        String dateStr = date2Str(new Date(ts), time_sdf);
        dateStr = dateStr.substring(0, dateStr.length() - 2) + "00";
        return str2Date(dateStr, time_sdf);
    }

    public static Long str2Timestamp(String dateStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static Date str2Date(String dateStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String date2Str(Date date, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    public static String timestamp2Str(Long timestamp, String format) {
        return date2Str(new Date(timestamp), format);
    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date getDateAdd(Date date, int field, int amount) {
        Calendar calendar = getCalendar(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static Date getDateAddDay(Date date, int amount) {
        return getDateAdd(date, Calendar.DAY_OF_MONTH, amount);
    }

    public static Date getDateAddMinute(Date date, int amount) {
        return getDateAdd(date, Calendar.MINUTE, amount);
    }

    public static Date getDate(Date date, int amount, Integer hourOfDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateAddDay(date, amount));
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /***
     * @description: 获取某个时间的前/后多少分钟
     * @param: stationTime
     * @Param: time
     * @return: {@link Date}
     * @author fan
     * @date: 2022/7/4 9:50
     */
    public static Date getMinuteTime(Long stationTime, Integer time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(stationTime));
        calendar.add(Calendar.MINUTE, time);// 20分钟后 time=20，前20分钟time=-20
        Date after20Time = calendar.getTime();
        return after20Time;
    }

    /***
     * @description: 获取当前时间的前/后几个月时间
     * @param: time
     * @return: {@link Date}
     * @author fan
     * @date: 2022/7/4 10:05
     */
    public static Date getMonthTime(Integer time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, time);// 如：6个月后 time=6，前6个月time=-6
        Date monthTime = calendar.getTime();
        return monthTime;
    }

    /**
     * 格式化日期，格式：yyyy
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(yyyy);
        return df.format(date);
    }

    /**
     * 获取前后时间
     *
     * @param date
     * @param num
     * @return
     */
    public static Date plusDate(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }

}
