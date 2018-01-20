package me.shouheng.notepal.provider.helper;

import android.content.Context;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Location;
import me.shouheng.notepal.model.Stats;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.model.enums.Operation;
import me.shouheng.notepal.model.enums.Status;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.LocationsStore;
import me.shouheng.notepal.provider.MindSnaggingStore;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.provider.TimelineStore;
import me.shouheng.notepal.provider.schema.TimelineSchema;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.TimeUtils;

/**
 * Created by wang shouheng on 2018/1/19.*/
public class StatisticsHelper {

    /**
     * Get the statistics.
     *
     * @param context the context
     * @return the Stats object contains the actions result.
     */
    public static Stats getStats(Context context) {
        Stats stats = new Stats();

        NotesStore notesStore = NotesStore.getInstance(context);
        stats.setTotalNotes(notesStore.getCount(null, Status.DELETED, true));
        stats.setArchivedNotes(notesStore.getCount(null, Status.ARCHIVED, false));
        stats.setTrashedNotes(notesStore.getCount(null, Status.TRASHED, false));

        MindSnaggingStore mindSStore = MindSnaggingStore.getInstance(context);
        stats.setTotalMinds(mindSStore.getCount(null, Status.DELETED, true));
        stats.setArchivedMinds(mindSStore.getCount(null, Status.ARCHIVED, false));
        stats.setTrashedMinds(mindSStore.getCount(null, Status.TRASHED, false));

        LocationsStore locationsStore = LocationsStore.getInstance(context);
        List<Location> locations = locationsStore.getDistinct(null, null);
        stats.setLocCnt(locations.size());
        stats.setLocations(locations);
        stats.setTotalLocations(locationsStore.getCount(null, Status.DELETED, true));

        NotebookStore notebookStore = NotebookStore.getInstance(context);
        stats.setTotalNotebooks(notebookStore.getCount(null, Status.TRASHED, false));

        AttachmentsStore attachmentsStore = AttachmentsStore.getInstance(context);
        List<Attachment> attachments = attachmentsStore.get(null, null);
        int images = 0, videos = 0, audioRecordings = 0, sketches = 0, files = 0;
        for (Attachment attachment : attachments) {
            if (Constants.MIME_TYPE_IMAGE.equals(attachment.getMineType())) {
                images++;
            } else if (Constants.MIME_TYPE_VIDEO.equals(attachment.getMineType())) {
                videos++;
            } else if (Constants.MIME_TYPE_AUDIO.equals(attachment.getMineType())) {
                audioRecordings++;
            } else if (Constants.MIME_TYPE_SKETCH.equals(attachment.getMineType())) {
                sketches++;
            } else if (Constants.MIME_TYPE_FILES.equals(attachment.getMineType())) {
                files++;
            }
        }
        stats.setTotalAttachments(attachments.size());
        stats.setImages(images);
        stats.setVideos(videos);
        stats.setAudioRecordings(audioRecordings);
        stats.setSketches(sketches);
        stats.setFiles(files);

        stats.setNotesStats(getAddedStatistics(context, ModelType.NOTE));
        stats.setMindsStats(getAddedStatistics(context, ModelType.MIND_SNAGGING));

        return stats;
    }

    /**
     * Get added models of last seven days. This will lead to query action in main thread.
     * If you have already got the queried data, you may use {@link #getLine(List, int)} method
     * to get the line chart data.
     *
     * @param context context
     * @param modelType model type
     * @param color line color
     * @return the line chart data
     */
    public static LineChartData getLineChartData(Context context, ModelType modelType, int color) {
        return getLineChartData(Arrays.asList(getLine(context, modelType, color)));
    }

    /**
     * To get the line chart data use the queried data.
     *
     * @param lineStatistics line statistics
     * @param color the color of line
     * @return the line chart data
     */
    public static LineChartData getLineChartData(List<Integer> lineStatistics, int color) {
        return getLineChartData(Arrays.asList(getLine(lineStatistics, color)));
    }

    private static LineChartData getLineChartData(List<Line> lines) {
        Calendar sevenDaysAgo = Calendar.getInstance();
        sevenDaysAgo.set(Calendar.HOUR_OF_DAY, 0);
        sevenDaysAgo.set(Calendar.MINUTE, 0);
        sevenDaysAgo.set(Calendar.SECOND, 0);
        sevenDaysAgo.set(Calendar.MILLISECOND, 0);
        sevenDaysAgo.add(Calendar.DAY_OF_YEAR, -6);
        List<String> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        for (int i=0; i<7; i++){
            days.add(sdf.format(sevenDaysAgo.getTime()));
            sevenDaysAgo.add(Calendar.DAY_OF_YEAR, 1);
        }

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setAxisXBottom(null);
        data.setAxisYLeft(null);
        data.setBaseValue(0);
        data.setValueLabelBackgroundColor(Color.TRANSPARENT);
        Axis axis = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f), days);
        data.setAxisXBottom(axis);
        return data;
    }

    private static Line getLine(Context context, ModelType modelType, int color) {
        return getLine(StatisticsHelper.getAddedStatistics(context, modelType), color);
    }

    private static Line getLine(List<Integer> lineStatistics, int color) {
        List<PointValue> values = new LinkedList<>();
        int length = lineStatistics.size();
        for (int j = 0; j < length; ++j) {
            values.add(new PointValue(j, lineStatistics.get(j)));
        }
        LogUtils.d("getLineChartData: " + lineStatistics);

        Line line = new Line(values);
        line.setColor(color);
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(true);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setPointRadius(3);

        return line;
    }

    private static List<Integer> getAddedStatistics(Context context, ModelType modelType) {
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
}
