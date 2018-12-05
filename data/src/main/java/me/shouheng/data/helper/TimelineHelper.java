package me.shouheng.data.helper;

import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Location;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.entity.Model;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.entity.TimeLine;
import me.shouheng.data.entity.Weather;
import me.shouheng.data.model.enums.Operation;
import me.shouheng.data.store.TimelineStore;

/**
 * Created by WngShhng on 2017/11/3.
 */
public class TimelineHelper {

    public static <T extends Model> void addTimeLine(T model, Operation operation) {
        if (!hasTimeLine(model, operation)) return;
        TimelineStore.getInstance().saveModel(ModelFactory.getTimeLine(model, operation));
    }

    public static <T extends Model> TimeLine getTimeLine(T model, Operation operation) {
        if (!hasTimeLine(model, operation)) return null;
        return ModelFactory.getTimeLine(model, operation);
    }

    private static<T extends Model> boolean hasTimeLine(T model, Operation operation) {
        return model != null && (model instanceof Note
                || model instanceof Notebook
                || model instanceof QuickNote
                || model instanceof Category
                || (model instanceof Weather && Operation.ADD == operation)
                || (model instanceof Location && Operation.ADD == operation)
                || (model instanceof Attachment && Operation.ADD == operation));
    }
}
