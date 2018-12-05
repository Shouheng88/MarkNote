package me.shouheng.commons.utils;

import android.content.Context;
import android.text.format.DateUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.shouheng.commons.BaseApplication;

/**
 * Created by WngShhng on 2017/3/13.
 */
public class TimeUtils {

    private static int daysOfMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public enum DateFormat {
        YYYY_MMM_dd_E_hh_mm_a("yyyy MMM dd E hh:mm a"),
        YYYYMMdd_hh_mm_a("yyyy/MM/dd hh:mm a"),
        YYYYMMdd("yyyy/MM/dd");

        final String format;

        DateFormat(String format) {
            this.format = format;
        }
    }


    // region 获取格式化的日期字符串
    public static String formatDate(long millis, DateFormat dateFormat){
        Date d = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.format, Locale.getDefault());
        return sdf.format(d);
    }

    public static String formatDate(Date date, DateFormat dateFormat) {
        return date == null ? "null" : new SimpleDateFormat(dateFormat.format, Locale.getDefault()).format(date);
    }
    // endregion


    // region 获取指定时间格式的时间和日期字符串

    /**
     * 获取长的日期，比如2017年10月10日（后面有日）
     *
     * @param context 上下文
     * @param date 日期
     * @return 日期字符串 */
    public static String getLongDate(Context context, Date date){
        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        return DateUtils.formatDateTime(context, date.getTime(), flags);
    }

    public static String getLongDateWithWeekday(Context context, Date date){
        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        return DateUtils.formatDateTime(context, date.getTime(), flags);
    }

    public static String getLongDateTime(Context context, Date date) {
        return DateUtils.formatDateTime(context, date.getTime(),
                DateUtils.FORMAT_NUMERIC_DATE
                        | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_YEAR);
    }


    public static String getShortDateWithWeekday(Context context, Calendar calendar) {
        if (calendar == null) return "";
        Calendar now = Calendar.getInstance();
        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        if (calendar.get(Calendar.YEAR) != now.get(Calendar.YEAR)){
            flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        }
        return DateUtils.formatDateTime(context, calendar.getTime().getTime(), flags)
                + new SimpleDateFormat(" | E ", Locale.getDefault()).format(calendar.getTime());
    }

    /**
     * 获取短的日期，如果指定的日期是今年的，就不用显示年份信息
     *
     * @param context 上下文
     * @param calendar 日期
     * @return 日期字符串
     */
    public static String getShortDate(Context context, Calendar calendar){
        if (calendar == null) return "";
        Calendar now = Calendar.getInstance();
        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        if (calendar.get(Calendar.YEAR) != now.get(Calendar.YEAR)){
            flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        }
        return DateUtils.formatDateTime(context, calendar.getTime().getTime(), flags);
    }

    public static String getShortDate(Context context, Date date){
        if (date == null) return "";
        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        if (c.get(Calendar.YEAR) != now.get(Calendar.YEAR)){
            flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        }
        return DateUtils.formatDateTime(context, date.getTime(), flags);
    }

    public static String getNoMonthDay(Context context, Date date) {
        if (date == null) return "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        flags |= DateUtils.FORMAT_NO_MONTH_DAY;
        flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        return DateUtils.formatDateTime(context, date.getTime(), flags);
    }

    /**
     * 获取短的日期，日期规则同上，这里再加上时间
     *
     * @param mContext 上下文
     * @param date 日期
     * @return 日期字符串
     */
    public static String getDateTimeShort(Context mContext, Date date) {
        if (date == null) return "";
        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        if (c.get(Calendar.YEAR) != now.get(Calendar.YEAR)){
            flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        }
        return DateUtils.formatDateTime(mContext, date.getTime(), flags) + " "
                + DateUtils.formatDateTime(mContext, date.getTime(), DateUtils.FORMAT_SHOW_TIME);
    }


    /**
     * 获取短的时间字符串
     *
     * @param context 上下文
     * @param time 时间
     * @return 时间字符串
     */
    public static String getShortTime(Context context, int time){
        Calendar date = getTodayDate();
        date.add(Calendar.MILLISECOND, time);
        return getShortTime(context, date.getTime());
    }

    public static String getShortTime(Context context, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return getShortTime(context, calendar.getTime());
    }

    public static String getShortTime(Context mContext, Date time){
        return DateUtils.formatDateTime(mContext, time.getTime(), DateUtils.FORMAT_SHOW_TIME);
    }


    /**
     * 获取相对于上次时间的距离的时间，比如2分钟之前
     *
     * @param date 日期
     * @return 时间字符串
     */
    public static String getPrettyTime(Date date) {
        if (date == null) {
            return "";
        }
        return getPrettyTime(date, BaseApplication.getContext().getResources().getConfiguration().locale);
    }

    private static String getPrettyTime(Date date, Locale locale) {
        if (date == null) {
            return "";
        }
        PrettyTime pt = new PrettyTime();
        if (locale != null) {
            pt.setLocale(locale);
        }
        return pt.format(date);
    }

    // endregion


    // region 获取某“月”的时间信息

    /**
     * 获取某个月的天数
     *
     * @param year 年
     * @param month 1代表1月
     * @return 天数
     */
    public static int getDaysOfMonth(int year, int month){
        final int MONTHS_YEAR = 12;
        if ((month<1) || (month>MONTHS_YEAR)){
            return 0;
        }
        int days = daysOfMonth[month-1];
        if ((month == 2) && isLeapYear(year)){
            days++;
        }
        return days;
    }

    /**
     * 获取指定月份的开始和截止的毫秒数：比如2017年5月，获取的是
     * 2017年5月1日0:0:0 0'的毫秒数和2017年5月31日23:59:59 999'的毫秒数
     *
     * @param year 年
     * @param month 1代表1月
     * @return 开始和截止的毫秒数
     */
    public static long[] getStartAndEndMillisOfMonth(int year, int month){
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, year);
        startTime.set(Calendar.MONTH, month - 1);
        startTime.set(Calendar.DAY_OF_MONTH, 1);
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);

        int daysOfMonth = getDaysOfMonth(year, month);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.DAY_OF_MONTH, daysOfMonth);
        return new long[]{startTime.getTimeInMillis(), endTime.getTimeInMillis() - 1};
    }

    // endregion


    // region 获取某“周”的时间信息

    /**
     * 根据当前周历上的第一个可见的日期获取标题栏的子标题字符串
     *
     * @param newFirstVisibleDay 当前周历上的第一个可见的日期
     * @param mNumberOfVisibleDays 日历上可见的日期
     * @return 子标题字符串
     */
    public static String getWeekCalendarSubTitle(
            Context context, Calendar newFirstVisibleDay, int mNumberOfVisibleDays){
        if (mNumberOfVisibleDays == 1) {
            return getShortDate(context, newFirstVisibleDay);
        } else {
            String str1 = getShortDate(context, newFirstVisibleDay);
            newFirstVisibleDay.add(Calendar.DAY_OF_YEAR, mNumberOfVisibleDays - 1);
            String str2 = getShortDate(context, newFirstVisibleDay);
            return str1 + "-" + str2;
        }
    }

    public static Calendar sevenDaysAgo() {
        Calendar sevenDaysAgo = Calendar.getInstance();
        sevenDaysAgo.set(Calendar.HOUR_OF_DAY, 0);
        sevenDaysAgo.set(Calendar.MINUTE, 0);
        sevenDaysAgo.set(Calendar.SECOND, 0);
        sevenDaysAgo.set(Calendar.MILLISECOND, 0);
        sevenDaysAgo.add(Calendar.DAY_OF_YEAR, -6);
        return sevenDaysAgo;
    }

    // endregion


    // region 获取某“日”的时间信息

    /**
     * 获取今天0时0分0秒0毫秒时的标准时间的毫秒数
     *
     * @return 毫秒数
     */
    public static long getMillisTodayStart(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取今天的23:59 59'999''的毫秒数
     *
     * @return 毫秒数
     */
    public static long getMillisTodayEnd() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    /**
     * 获取明天0时0分0秒0毫秒时的毫秒数
     *
     * @return 毫秒数
     */
    public static long getStandardMillisTomorrow(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, 1);
        return c.getTimeInMillis();
    }

    /**
     * 获取指定日期的周次，周日(1)，周一(2)，周二(3)
     *
     * @param month 月，0为一月
     * @return 周次
     */
    public static int getDayOfWeek(int year, int month, int day){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        return date.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取指定日期的标准开始时间，指定日期的0时0分0秒的时间
     *
     * @return 日期
     */
    public static Date getStartDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的标准结束时间，23时59分59秒999毫秒
     *
     * @return 结束时间
     */
    public static Date getEndDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    // endregion


    // region 获取今天、明天、本周五、本周日和下周一的标准时间

    /**
     * 获取今天的标准日期，小时5以后的单位的数值都是0
     *
     * @return  今天的Calendar对象，其中“天”之后的时间全部置为0
     */
    public static Calendar getTodayDate(){
        final Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now;
    }

    public static Calendar getTomorrowDate(){
        final Calendar today = getTodayDate();
        today.add(Calendar.DAY_OF_YEAR, 1);
        return today;
    }
    // endregion


    // region 其他的时间工具方法

    /**
     * 两个日期之间相距的天数
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 相距的天数
     */
    public static int daysSpan(Date start, Date end) {
        return (int) ((end.getTime() - start.getTime()) / DateUtils.DAY_IN_MILLIS);
    }

    /**
     * 录音时间的格式化
     *
     * @param recordMillis 录音时间（毫秒）
     * @return 时间字符串
     */
    public static String getRecordTime(long recordMillis) {
        int minute = (int) (recordMillis / DateUtils.MINUTE_IN_MILLIS);
        int seconds = (int) ((recordMillis % DateUtils.MINUTE_IN_MILLIS ) / 1000);
        String time = "";
        if (minute < 10) {
            time += "0" + minute;
        } else {
            time += minute;
        }
        time += ":";
        if (seconds < 10) {
            time += "0" + seconds;
        } else {
            time += seconds;
        }
        return time;
    }

    /**
     * 获取指定的起止毫秒之间的进度信息
     *
     * @param startMillis 开始的毫秒
     * @param endMillis 结束的毫秒
     * @return 真实进度
     */
    public static String getRealProgress(long startMillis, long endMillis){
        long current = System.currentTimeMillis();
        if (current > endMillis) return "100%";
        if (current < startMillis) return "0%";
        long pastedMillis = current - startMillis;
        long spenMillis = endMillis - startMillis;
        float progress = pastedMillis / (spenMillis * 1.0f);
        return progress + "%";
    }

    public static int getHour(int timeMillis) {
        return (int) (timeMillis / DateUtils.HOUR_IN_MILLIS);
    }

    public static int getMinute(int timeMillis) {
        return (int) (timeMillis % DateUtils.HOUR_IN_MILLIS / DateUtils.MINUTE_IN_MILLIS);
    }

    public static int getTimeInMillis(int hour, int minute){
        return (int) (DateUtils.HOUR_IN_MILLIS * hour + DateUtils.MINUTE_IN_MILLIS * minute);
    }

    public static String getTimeLength(Context context, long startMillis, long endMillis){
        if (endMillis < startMillis) return "--";
        long timeSpan = endMillis - startMillis;
        int days = (int) (timeSpan / DateUtils.DAY_IN_MILLIS);
        int hours = (int) (timeSpan % DateUtils.DAY_IN_MILLIS / DateUtils.HOUR_IN_MILLIS);
        int minutes = (int) (timeSpan % DateUtils.DAY_IN_MILLIS % DateUtils.HOUR_IN_MILLIS / DateUtils.MINUTE_IN_MILLIS);
        StringBuilder sb = new StringBuilder();
        if (days != 0){
            sb.append(String.valueOf(days));
        }
        if (hours != 0){
            sb.append(String.valueOf(hours));
        }
        if (minutes != 0){
            sb.append(String.valueOf(minutes));
        }
        return sb.toString();
    }

    /**
     * 计算周历中需要滚动到的时间
     *
     * @return 要滚动到的时间
     */
    public static int calTimeToGo() {
        Calendar calendar = Calendar.getInstance();
        int current = calendar.get(Calendar.HOUR_OF_DAY);
        current -= 3;
        return current < 0 ? 0 : current;
    }

    // endregion


    // region 农历相关的方法

    /**
     * 获取指定年的属相
     *
     * @param year 年
     * @return 属相字符串
     */
    public static String getShuXiang(int year){
        final String shuStrs[] = new String[]{"鼠年", "牛年", "虎年", "兔年",
                "龙年", "蛇年", "马年", "羊年", "猴年", "鸡年", "狗年", "猪年"};
        int x1 = year - 2008;
        if (x1<0){
            x1 = -x1;
            int x2 = x1 % 12;
            if (x2 == 0){
                return shuStrs[0];
            } else {
                x2 = 12 - x2;
                return shuStrs[x2];
            }
        } else{
            int x2 = x1 % 12;
            return shuStrs[x2];
        }
    }

    /**
     * 计算指定年份的干支
     *
     * @param year 年
     */
    public static String getGanZhi(int year){
        final String[] ganStrs = new String[]{"甲", "乙", "丙", "丁", "戊", "己","庚", "辛", "壬", "癸"};
        final String[] zhiStrs = new String[]{"子", "丑", "寅", "卯", "辰", "巳","午", "未", "申", "酉", "戌", "亥"};
        String ganStr, zhiStr;
        int sc = year-2000;
        int gan = (7+sc)%10;
        int zhi = (5+sc)%12;
        if(gan<0){
            gan+=10;
        }
        if(zhi<0){
            zhi+=12;
        }
        if (gan == 0){
            ganStr = ganStrs[9];
        } else {
            ganStr = ganStrs[gan-1];
        }
        if (zhi == 0){
            zhiStr = zhiStrs[11];
        } else {
            zhiStr = zhiStrs[zhi-1];
        }
        return ganStr+zhiStr;
    }

    // endregion


    private static boolean isLeapYear(int year){
        return ((year%4 == 0) && (year%100 != 0))||(year%400 == 0);
    }
}
