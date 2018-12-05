package me.shouheng.data;

import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.shouheng.commons.BaseApplication;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.TimeUtils;
import me.shouheng.commons.utils.UserUtil;
import me.shouheng.data.entity.Alarm;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Location;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.entity.Model;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.entity.TimeLine;
import me.shouheng.data.entity.Weather;
import me.shouheng.data.model.DaysOfMonth;
import me.shouheng.data.model.DaysOfWeek;
import me.shouheng.data.model.enums.AlarmType;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.model.enums.NoteType;
import me.shouheng.data.model.enums.Operation;
import me.shouheng.data.model.enums.Portrait;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.model.enums.WeatherType;

import static me.shouheng.data.DBConfig.CATEGORY_SPLIT;
import static me.shouheng.data.DBConfig.TIMELINE_CONTENT_LENGTH;

/**
 * Created by wangshouheng on 2017/11/17. */
public class ModelFactory {

    private static long getLongCode() {
        return System.currentTimeMillis();
    }

    private static int getIntegerCode() {
        Calendar now = Calendar.getInstance();
        int appendix = now.get(Calendar.HOUR_OF_DAY) * 3600000
                + now.get(Calendar.MINUTE) * 60000
                + now.get(Calendar.SECOND) * 1000
                + now.get(Calendar.MILLISECOND);
        int prefix = (int)(Math.random() * 21); // 生成一个小于[0,20]的整数
        return prefix * 100000000 + appendix;
    }

    private static <T extends Model> T getModel(Class<T> itemType) {
        try {
            T newItem = itemType.newInstance();
            newItem.setCode(getLongCode());
            newItem.setUserId(UserUtil.getInstance(BaseApplication.getContext()).getUserIdKept());
            newItem.setAddedTime(new Date());
            newItem.setLastModifiedTime(new Date());
            newItem.setLastSyncTime(new Date(0));
            newItem.setStatus(Status.NORMAL);
            return newItem;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Alarm getAlarm() {
        Calendar c = Calendar.getInstance();
        Alarm alarm = getModel(Alarm.class);

        assert alarm != null;
        alarm.setCode(getIntegerCode());

        alarm.setModelType(ModelType.NONE);
        alarm.setModelCode(0);

        alarm.setEnabled(true);
        alarm.setAlarmType(AlarmType.SPECIFIED_DATE);
        alarm.setNextTime(c);

        alarm.setDaysOfWeek(DaysOfWeek.getInstance(0));
        alarm.setDaysOfMonth(DaysOfMonth.getInstance(0));

        alarm.setStartDate(new Date());
        alarm.setEndDate(TimeUtils.getTomorrowDate().getTime());

        alarm.setHour(c.get(Calendar.HOUR_OF_DAY));
        alarm.setMinute(c.get(Calendar.MINUTE));

        return alarm;
    }

    public static Attachment getAttachment() {
        Attachment attachment = getModel(Attachment.class);
        assert attachment != null;
        attachment.setModelType(ModelType.NONE);
        attachment.setModelCode(0);
        return attachment;
    }

    public static Location getLocation() {
        Location location = getModel(Location.class);
        assert location != null;
        location.setModelType(ModelType.NONE);
        return location;
    }

    public static QuickNote getQuickNote() {
        return getModel(QuickNote.class);
    }

    public static Notebook getNotebook() {
        Notebook notebook = getModel(Notebook.class);
        assert notebook != null;
        notebook.setColor(PalmUtils.getColorCompact(R.color.default_notebook_color));
        return notebook;
    }

    public static Note getNote() {
        Note note = getModel(Note.class);
        assert note != null;
        note.setNoteType(NoteType.NORMAL);
        return note;
    }

    public static Note getNote(@Nullable Notebook notebook, @Nullable Category category) {
        Note note = getNote();
        if (notebook != null) {
            note.setParentCode(notebook.getCode());
            note.setTreePath(notebook.getTreePath() + "|" + note.getCode());
        } else {
            note.setTreePath(String.valueOf(note.getCode()));
        }

        if (category != null) {
            note.setTags(getTags(Collections.singletonList(category)));
        }

        return note;
    }

    public static <T extends Model> TimeLine getTimeLine(T model, Operation operation) {
        TimeLine timeLine = new TimeLine();

        timeLine.setCode(ModelFactory.getLongCode());
        timeLine.setUserId(model.getUserId());
        timeLine.setAddedTime(new Date());
        timeLine.setLastModifiedTime(new Date());
        timeLine.setLastSyncTime(new Date(0));
        timeLine.setStatus(Status.NORMAL);

        timeLine.setOperation(operation);
        timeLine.setModelName(getModelName(model));
        timeLine.setModelCode(model.getCode());
        timeLine.setModelType(ModelType.getTypeByName(model.getClass()));
        return timeLine;
    }

    public static Category getCategory() {
        Category category = getModel(Category.class);
        assert category != null;
        category.setPortrait(Portrait.FOLDER);
        category.setCategoryOrder(0);
        // use the primary color as the category color
        category.setColor(PalmUtils.getColorCompact(R.color.default_category_color));
        return category;
    }

    public static Weather getWeather(WeatherType type, int temperature) {
        Weather weather = getModel(Weather.class);
        assert weather != null;
        weather.setType(type);
        weather.setTemperature(temperature);
        return weather;
    }

    private static <M extends Model> String getModelName(M model) {
        String modelName = null;
        if (model instanceof Attachment) {
            return ((Attachment) model).getUri().toString();
        } else if (model instanceof QuickNote) {
            modelName = ((QuickNote) model).getContent();
        } else if (model instanceof Note) {
            modelName = ((Note) model).getTitle();
        } else if (model instanceof Notebook) {
            modelName = ((Notebook) model).getTitle();
        } else if (model instanceof Location) {
            Location location = ((Location) model);
            modelName = location.getCountry() + "|" + location.getCity() + "|" + location.getDistrict();
        } else if (model instanceof Weather) {
            Weather weather = ((Weather) model);
            modelName = PalmUtils.getStringCompact(weather.getType().nameRes) + "|" + weather.getTemperature();
        } else if (model instanceof Category) {
            Category category = (Category) model;
            modelName = category.getName();
        }
        if (modelName != null && modelName.length() > TIMELINE_CONTENT_LENGTH) {
            return modelName.substring(0, TIMELINE_CONTENT_LENGTH);
        }
        return modelName;
    }

    private static String getTags(List<Category> categories) {
        if (categories == null || categories.isEmpty()) return null;
        int len = categories.size();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<len; i++) {
            sb.append(categories.get(i).getCode());
            if (i != len - 1) sb.append(CATEGORY_SPLIT);
        }
        LogUtils.d(sb.toString());
        return sb.toString();
    }
}
