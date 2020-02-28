package me.shouheng.notepal.vm;

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

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.data.helper.StatisticsHelper;
import me.shouheng.data.model.Stats;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;

/**
 * Created by Employee on 2018/3/15.*/
public class StatisticViewModel extends ViewModel {

    /**
     * The days count of added model statistic.
     */
    private final static int DAYS_OF_ADDED_MODEL = 7;

    /**
     * The default value of added model.
     */
    private final static int DEFAULT_ADDED_VALUE = 0;

    /**
     * The default total values.
     */
    private final static int DEFAULT_TOTAL_VALUE = 0;

    private int lineStrokeWidth = ViewUtils.dp2Px(PalmApp.getContext(), 1);

    private MutableLiveData<Resource<Stats>> statsLiveData;

    public MutableLiveData<Resource<Stats>> getStatsLiveData() {
        if (statsLiveData == null) {
            statsLiveData = new MutableLiveData<>();
        }
        return statsLiveData;
    }

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
        line.setPointRadius(2);
        line.setStrokeWidth(2);

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

    public ColumnChartData getDefaultModelsData() {
        ColumnChartData data = new ColumnChartData(Arrays.asList(
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_lime_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_light_blue_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_green_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_pink_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_red_500))));

        Axis axisX = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f),
                Arrays.asList(PalmUtils.getStringCompact(R.string.model_name_note),
                        PalmUtils.getStringCompact(R.string.model_name_notebook),
                        PalmUtils.getStringCompact(R.string.model_name_category),
                        PalmUtils.getStringCompact(R.string.model_name_attachment),
                        PalmUtils.getStringCompact(R.string.model_name_location)));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(null);

        return data;
    }

    private Column getColumn(float value, int color) {
        Column column = new Column(Collections.singletonList(new SubcolumnValue(value, color)));
        column.setHasLabels(true);
        return column;
    }

    public ColumnChartData getDefaultAttachmentData() {
        ColumnChartData data = new ColumnChartData(Arrays.asList(
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_lime_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_light_blue_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_pink_500)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_green_600)),
                getColumn(DEFAULT_TOTAL_VALUE, PalmUtils.getColorCompact(R.color.md_red_500))));

        Axis axisX = Axis.generateAxisFromCollection(Arrays.asList(0.0f, 1.0f, 2.0f, 3.0f, 4.0f),
                Arrays.asList(PalmUtils.getStringCompact(R.string.attachment_type_files),
                        PalmUtils.getStringCompact(R.string.attachment_type_images),
                        PalmUtils.getStringCompact(R.string.attachment_type_sketches),
                        PalmUtils.getStringCompact(R.string.attachment_type_videos),
                        PalmUtils.getStringCompact(R.string.attachment_type_voice)));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(null);

        return data;
    }

    public Disposable getStats() {
        return Observable
                .create((ObservableOnSubscribe<Stats>) emitter -> {
                    Stats stats = StatisticsHelper.getStats(PalmApp.getContext());
                    emitter.onNext(stats);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(stats -> {
                    if (statsLiveData != null) {
                        statsLiveData.setValue(Resource.success(stats));
                    }
                });
    }
}
