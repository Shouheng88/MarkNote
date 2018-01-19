package me.shouheng.notepal.provider.helper;

import android.content.Context;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

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
}
