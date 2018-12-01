package me.shouheng.data.model;

import android.content.Context;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import me.shouheng.data.R;

/*
 * 使用整数代表重复的周次信息
 * 0x00   0  (0000 0000): 不重复
 * 0x01   1  (0000 0001): 周一
 * 0x02   2  (0000 0010): 周二
 * 0x04   4  (0000 0100): 周三
 * 0x08   8  (0000 1000): 周四
 * 0x10  16  (0001 0000): 周五
 * 0x20  32  (0010 0000): 周六
 * 0x40  64  (0100 0000): 周日
 *
 * 0x1F  31  (0001 1111): 工作日
 * 0x60  96  (0110 0000): 周末
 * 0x7F 127  (0111 1111): 每天重复
 */
public final class DaysOfWeek {

    /**
     * 在java中提供的API中，下面的几个值分别是：
     * Calendar.SUNDAY == 1
     * Calendar.MONDAY == 2
     * …… */
    private static int[] DAY_MAP = new int[] {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY,
    };

    private int mDays;

    /**
     * 使用传入的布尔数组获取一个DaysOfWeek的实例
     *
     * @param booleanArray 该布尔数组的各个位置的元素代表的依次是：周一，周二，……
     * @return DaysOfWeek 的实例 */
    public static DaysOfWeek getInstance(boolean[] booleanArray){
        DaysOfWeek daysOfWeek = new DaysOfWeek();
        int length = booleanArray.length;
        for (int index = 0;index<length; index++){
            daysOfWeek.set(index, booleanArray[index]);
        }
        return daysOfWeek;
    }

    /**
     * 通过直接传入值的方式来创建一个DaysOfWeek实例
     *
     * @param days 天的整数值
     * @return DaysOfWeek实例 */
    public static DaysOfWeek getInstance(int days){
        return new DaysOfWeek(days);
    }

    private DaysOfWeek(){}

    private DaysOfWeek(int days) {
        mDays = days;
    }

    /**
     * 判断指定的日期是否被设置为重复的
     *
     * @param day 其值等于DAY_MAP中的索引的值
     * @return 指定的日期是否被设置 */
    public boolean isSet(int day) {
        return (mDays & 1 << day) > 0;
    }

    /**
     * 设置某天的mDays的值
     *
     * @param day 某天的顺序
     * @param set 指定的天是否设置为重复的：set为true时，设置为1，否则为0 */
    public void set(int day, boolean set) {
        if (set) {
            mDays |= 1 << day;
        } else {
            mDays &= ~(1 << day);
        }
    }

    public void set(DaysOfWeek dow) {
        mDays = dow.mDays;
    }

    public int getCoded() {
        return mDays;
    }

    public boolean[] getBooleanArray() {
        boolean[] ret = new boolean[7];
        for (int i = 0; i < 7; i++) {
            ret[i] = isSet(i);
        }
        return ret;
    }

    public boolean isRepeatSet() {
        return mDays != 0;
    }

    public boolean isEveryDay() {
        return mDays == 0x7f;
    }

    /**
     * 返回从当前日期到下一个响铃日期需要的天数
     *
     * @param nextTime 传入的日期，如果闹钟的时间小于当前时间，就将闹钟日期加1，然后设置为指定时间；
     *          如果闹钟时间大于当前时间，就将闹钟设置为指定的时间，日期是当前日期
     * @return 获取闹钟需要增加的天数，如果返回的值小于等于0就表示无需在之前的nextTime基础上增加天数 */
    public int getNextAlarm(Calendar nextTime) {
        if (mDays == 0) return -1; // 非重复的闹钟类型，无需增加？

        int today = (nextTime.get(Calendar.DAY_OF_WEEK) + 5) % 7; // 将nextTime的天转换成的DAY_MAP中的索引

        // 使用从今天到下周的今天的循环方式计算距离下一个提醒需要的天数
        int day = 0, dayCount = 0;
        for (; dayCount < 7; dayCount++) {
            day = (today + dayCount) % 7;
            if (isSet(day)) {
                break;
            }
        }
        return dayCount;
    }

    @Override
    public String toString() {
        if (mDays == 0) return "never";
        if (mDays == 0x7f) return "everyday";
        StringBuilder ret = new StringBuilder();
        String[] dayList = new DateFormatSymbols().getShortWeekdays();
        for (int i = 0; i < 7; i++) {
            if ((mDays & 1 << i) != 0) {
                ret.append(dayList[DAY_MAP[i]]);
            }
        }
        return ret.toString();
    }

    public String toString(Context context, boolean showNever) {
        StringBuilder ret = new StringBuilder();
        if (mDays == 0) {
            return showNever ? context.getText(R.string.text_one_shot).toString() : "";
        }
        if (mDays == 0x7f) {
            return context.getText(R.string.text_every_day).toString();
        }
        int dayCount = 0, days = mDays;
        while (days > 0) {
            if ((days & 1) == 1) {
                dayCount++;
            }
            days >>= 1;
        }
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] dayList = dayCount > 1 ? dfs.getShortWeekdays() : dfs.getWeekdays();
        for (int i = 0; i < 7; i++) {
            if ((mDays & 1 << i) != 0) {
                ret.append(dayList[DAY_MAP[i]]);
                dayCount -= 1;
                if (dayCount > 0) {
                    ret.append(context.getText(R.string.text_day_concat));
                }
            }
        }
        return ret.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mDays;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DaysOfWeek other = (DaysOfWeek) obj;
        return mDays == other.mDays;
    }
}