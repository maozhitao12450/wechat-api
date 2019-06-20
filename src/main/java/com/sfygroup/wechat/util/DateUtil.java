package com.sfygroup.wechat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DateUtil {
    public static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 获取月的天数
     *
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 把指定时间向前推移一天
     *
     * @param date 当前时间
     */
    public static Date getYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        ;
        //+1今天的时间加一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 把指定时间向后推移一天
     *
     * @param date 当前时间
     */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        ;
        //+1今天的时间加一天
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 把指定时间向后推移day天
     *
     * @param date 当前时间
     */
    public static Date getNDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        ;
        //时间 date 加 day天
        calendar.add(Calendar.DAY_OF_MONTH, day);
        date = calendar.getTime();
        return date;
    }

    /**
     * 把指定时间向后推移second秒
     *
     * @param date 当前时间
     */
    public static Date getNSecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        ;
        //+1今天的时间加second秒
        calendar.add(Calendar.SECOND, second);
        date = calendar.getTime();
        return date;
    }


    /**
     * 把当前时间向后推移n月
     *
     * @param currentDate 当前时间
     * @param n
     * @return
     */
    public static Date getNMonth(Date currentDate, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        //今天的时间加n月
        calendar.add(Calendar.MONTH, n);
        currentDate = calendar.getTime();
        return currentDate;
    }

    //获取当天的开始时间
    public static Date getDayBegin() {
        Calendar todayBegin = Calendar.getInstance();
        todayBegin.set(Calendar.HOUR_OF_DAY, 0);
        todayBegin.set(Calendar.MINUTE, 0);
        todayBegin.set(Calendar.SECOND, 0);
        todayBegin.set(Calendar.MILLISECOND, 0);
        return todayBegin.getTime();
    }

    //获取当天的结束时间
    public static Date getDayEnd() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    //获取本周的开始时间
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        Calendar weekBegin = Calendar.getInstance();
        weekBegin.setTime(date);
        int dayOfWeek = weekBegin.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            dayOfWeek += 7;
        }
        weekBegin.add(Calendar.DATE, 2 - dayOfWeek);
        return getDayStartTime(weekBegin.getTime());
    }

    //获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar weekEnd = Calendar.getInstance();
        weekEnd.setTime(getBeginDayOfWeek());
        weekEnd.add(Calendar.DAY_OF_WEEK, 6);
        return getDayEndTime(weekEnd.getTime());
    }

    //获取某年某月月的开头
    public static Date getBeginDayOfMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            return getBeginDayOfMonth();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    //获取本月的开始时间
    public static Date getBeginDayOfMonth() {
        return getBeginDayOfMonth(getNowYear(), getNowMonth());
    }

    //获取本月的结束时间
    public static Date getEndDayOfMonth() {
        return getEndDayOfMonth(getNowYear(), getNowMonth());
    }

    //获取本月的结束时间
    public static Date getEndDayOfMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            return getEndDayOfMonth();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(year, month - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    //获取本年的开始时间
    public static Date getBeginDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getNowYear());
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        return getDayStartTime(calendar.getTime());
    }

    //获取本年的结束时间
    public static Date getEndDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getNowYear());
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 31);
        return getDayEndTime(calendar.getTime());
    }

    //获取某天的开始时间
    public static Date getDayStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        ;
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //获取某天的结束时间
    public static Date getDayEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        ;
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    //获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTime(date);
        return Integer.valueOf(calendar.get(Calendar.YEAR));
    }

    //获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONDAY) + 1;
    }


    //获取某天所在月的第一天
    public static Date getFirstDayOfMonth(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    //获取某天所在月的最后一天
    public static Date getLastDayOfMonth(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 日期格式化返回字符串
     *
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStrDate(Date date) {
        return getStrDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getStrDate(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String str = simpleDateFormat.format(date);
        return str;
    }

    public static String getStrDate(Long milliseconds, String pattern) {
        Date date = new Date(milliseconds);
        return getStrDate(date, pattern);
    }

    /**
     * 获取某年某月某日的strScheduleTime时间
     *
     * @param hourAssigned
     * @param minuteAssigned
     * @return
     */
    public static Date getScheduleTime(int hourAssigned, int minuteAssigned) {

        Calendar currentDateAssignTime = Calendar.getInstance();
        currentDateAssignTime.set(Calendar.HOUR_OF_DAY, hourAssigned);
        currentDateAssignTime.set(Calendar.MINUTE, minuteAssigned);
        currentDateAssignTime.set(Calendar.SECOND, 00);
        currentDateAssignTime.set(Calendar.MILLISECOND, 000);
        Date date = currentDateAssignTime.getTime();
        return date;
    }

    /**
     * 将某个Date转换为date8
     **/
    public static int getDate8(Date time) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyyMMdd");
        String date8Str = formatter.format(time);
        int date8 = Integer.parseInt(date8Str);
        return date8;
    }

    /**
     * 取得今天的date8
     */
    public static int getTodayDate8() {
        Date today_date = new Date();
        return getDate8(today_date);
    }

    public static long getRemainSecondsOneDay(Date currentDate) {
        Calendar midnight = Calendar.getInstance();
        midnight.setTime(currentDate);
        midnight.add(Calendar.DAY_OF_MONTH, 1);
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        long seconds = ((midnight.getTime().getTime() - currentDate.getTime()) / 1000);
        return seconds;
    }

    public static List<Date> getBetweenDates(Date begin, Date end) {
        List<Date> result = new ArrayList<>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(begin);
        while (begin.getTime() <= end.getTime()) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            begin = tempStart.getTime();
        }
        return result;
    }

    //将Date转成LocalDate(日期)
    public static LocalDate getLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }
}
