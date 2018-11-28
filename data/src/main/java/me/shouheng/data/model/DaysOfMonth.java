package me.shouheng.data.model;

import java.util.Calendar;

/**
 * Created by wangshouheng on 2017/4/23.*/
public class DaysOfMonth {

    private int dayOfMonth;

    public static DaysOfMonth getInstance(int dayOfMonth){
        return new DaysOfMonth(dayOfMonth);
    }

    private DaysOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    private boolean isSet(int day) {
        return (dayOfMonth & 1 << (day - 1)) > 0;
    }

    public void set(int day, boolean set) {
        if (set) {
            dayOfMonth |= 1 << (day - 1);
        } else {
            dayOfMonth &= ~(1 << (day - 1));
        }
    }

    public void set(DaysOfMonth dom) {
        dayOfMonth = dom.dayOfMonth;
    }

    public int getCoded() {
        return dayOfMonth;
    }

    public int getNextDay() {
        if (dayOfMonth == 0) {
            return -1;
        }
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int day = today + 1;
        for (;day<32;day++){
            if (isSet(day)){
                return day;
            }
        }
        if (day == 32){
            for (day = 1; day<=today;day++){
                if (isSet(day)){
                    break;
                }
            }
        }
        return day;
    }

    public boolean isRepeatSet() {
        return dayOfMonth != 0;
    }

    @Override
    public String toString() {
        if (dayOfMonth == 0) return "never";
        if (dayOfMonth == 0x7f) return "everyday";
        StringBuilder ret = new StringBuilder();
        ret.append("Day:");
        for (int i = 1; i < 32; i++) {
            if ((dayOfMonth & 1 << i) != 0) {
                ret.append(" i ");
            }
        }
        return ret.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DaysOfMonth other = (DaysOfMonth) obj;
        return dayOfMonth == other.dayOfMonth;
    }
}
