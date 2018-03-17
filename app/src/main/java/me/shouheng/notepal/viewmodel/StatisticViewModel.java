package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.async.NormalAsyncTask;
import me.shouheng.notepal.model.Stats;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.helper.StatisticsHelper;
import me.shouheng.notepal.util.LogUtils;

/**
 * Created by Employee on 2018/3/15.*/
public class StatisticViewModel extends ViewModel {

    /**
     * The days count of added model statistic. */
    public final static int DAYS_OF_ADDED_MODEL = 7;

    /**
     * The default value of added model */
    private final static int DEFAULT_ADDED_VALUE = 0;

    private final static int DEFAULT_TOTAL_VALUE = 0;

    // region line chart data of added models
    public LineChartData getDefaultNoteData(int lineColor) {
        List<Integer> defaultValues = new LinkedList<>();
        for (int i=0; i<DAYS_OF_ADDED_MODEL; i++) {
            defaultValues.add(DEFAULT_ADDED_VALUE);
        }
        return getLineChartData(Collections.singletonList(getLine(defaultValues, lineColor)));
    }

    private Line getLine(List<Integer> lineStatistics, int color) {
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

    private LineChartData getLineChartData(List<Line> lines) {
        DateTime daysAgo = new DateTime().withTimeAtStartOfDay().minusDays(DAYS_OF_ADDED_MODEL - 1);
        List<String> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        for (int i=0; i<DAYS_OF_ADDED_MODEL; i++){
            days.add(sdf.format(daysAgo.toDate()));
            daysAgo = daysAgo.plusDays(1);
        }

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setAxisXBottom(null);
        data.setAxisYLeft(null);
        data.setBaseValue(-0.1f);
        data.setValueLabelBackgroundColor(Color.TRANSPARENT);
        Axis axis = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f), days);
        data.setAxisXBottom(axis);
        return data;
    }

    public LiveData<Resource<List<Integer>>> getAddedModelData(ModelType modelType) {
        MutableLiveData<Resource<List<Integer>>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> StatisticsHelper.getAddedStatistics(modelType, DAYS_OF_ADDED_MODEL)).execute();
        return result;
    }
    // endregion

    // region get models statistic
    public ColumnChartData getDefaultModelsData() {
        ColumnChartData data = new ColumnChartData(Arrays.asList(
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_lime_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_light_blue_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_green_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_pink_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_red_500))));

        Axis axisX = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f),
                Arrays.asList(PalmApp.getStringCompact(R.string.model_name_note),
                        PalmApp.getStringCompact(R.string.model_name_notebook),
                        PalmApp.getStringCompact(R.string.model_name_category),
                        PalmApp.getStringCompact(R.string.model_name_attachment),
                        PalmApp.getStringCompact(R.string.model_name_location)));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(null);

        return data;
    }

    private Column getColumn(float value, int color) {
        Column column = new Column(Collections.singletonList(new SubcolumnValue(value, color)));
        column.setHasLabels(true);
        return column;
    }
    // endregion

    // region get attachment stats
    public ColumnChartData getDefaultAttachmentData() {
        ColumnChartData data = new ColumnChartData(Arrays.asList(
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_lime_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_light_blue_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_pink_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_green_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmApp.getColorCompact(R.color.md_red_500))));

        Axis axisX = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f),
                Arrays.asList(PalmApp.getStringCompact(R.string.attachment_type_files),
                        PalmApp.getStringCompact(R.string.attachment_type_images),
                        PalmApp.getStringCompact(R.string.attachment_type_sketches),
                        PalmApp.getStringCompact(R.string.attachment_type_videos),
                        PalmApp.getStringCompact(R.string.attachment_type_recordings)));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(null);

        return data;
    }
    // endregion

    // region get all stats one shot
    public LiveData<Resource<Stats>> getStats() {
        MutableLiveData<Resource<Stats>> result = new MutableLiveData<>();
        new NormalAsyncTask<>(result, () -> StatisticsHelper.getStats(PalmApp.getContext())).execute();
        return result;
    }
    // endregion
}
