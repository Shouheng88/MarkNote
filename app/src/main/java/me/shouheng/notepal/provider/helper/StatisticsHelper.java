package me.shouheng.notepal.provider.helper;

import android.content.Context;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.model.Stats;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.TimelineStore;
import me.shouheng.notepal.provider.schema.TimelineSchema;
import me.shouheng.notepal.util.TimeUtils;

/**
 * Created by wang shouheng on 2018/1/19.*/
public class StatisticsHelper {

    public static List<Integer> getAddedStatistics(Context context, ModelType modelType) {
        Calendar sevenDaysAgo = TimeUtils.sevenDaysAgo();
        List<Integer> states = new LinkedList<>();
        for (int i=0; i<7; i++) {
            long startMillis = sevenDaysAgo.getTimeInMillis();
            sevenDaysAgo.add(Calendar.DAY_OF_YEAR, 1);
            long endMillis = sevenDaysAgo.getTimeInMillis();
            String whereSQL = TimelineSchema.ADDED_TIME + " >= " + startMillis
                    + " AND " + TimelineSchema.ADDED_TIME + " < " + endMillis
                    + " AND " + TimelineSchema.MODEL_TYPE + " = " + modelType.id
                    + " AND " + TimelineSchema.OPERATION + " = " + Operation.ADD.id;
            states.add(TimelineStore.getInstance(context).getCount(whereSQL, Status.NORMAL, false));
        }
        return states;
    }

    public static Stats getStats(Context context) {
        return new Stats();
    }
}
