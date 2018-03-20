package me.shouheng.notepal.provider.helper;


import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Model;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.TimeLine;
import me.shouheng.notepal.model.Weather;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.provider.TimelineStore;

/**
 * Created by wangshouheng on 2017/11/3.*/
public class TimelineHelper {

    public static <T extends Model> void addTimeLine(T model, Operation operation) {
        if (!hasTimeLine(model, operation)) return;
        TimelineStore.getInstance(PalmApp.getContext()).saveModel(ModelFactory.getTimeLine(model, operation));
    }

    public static <T extends Model> TimeLine getTimeLine(T model, Operation operation) {
        if (!hasTimeLine(model, operation)) return null;
        return ModelFactory.getTimeLine(model, operation);
    }

    private static<T extends Model> boolean hasTimeLine(T model, Operation operation) {
        return model != null && (model instanceof Note
                || model instanceof Notebook
                || model instanceof MindSnagging
                || (model instanceof Weather && Operation.ADD == operation)
                || (model instanceof Location && Operation.ADD == operation)
                || (model instanceof Attachment && Operation.ADD == operation));
    }
}
