package me.shouheng.notepal.async;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.ModelFactory;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.provider.AttachmentsStore;
import me.shouheng.notepal.provider.MindSnaggingStore;
import me.shouheng.notepal.provider.NotebookStore;
import me.shouheng.notepal.provider.NotesStore;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.FileHelper;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.util.ModelHelper;
import me.shouheng.notepal.util.NotificationsHelper;
import me.shouheng.notepal.util.PreferencesUtils;

/**
 * Created by shouh on 2018/3/21.*/
public class SnaggingTransferService extends IntentService {

    public SnaggingTransferService() {
        super("SnaggingTransferService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        new TransferTask().execute();
    }

    private static class TransferTask extends AsyncTask<Void, Void, List<Note>> {

        @Override
        protected List<Note> doInBackground(Void... voids) {
            List<MindSnagging> snaggings = MindSnaggingStore.getInstance(PalmApp.getContext()).get(null, null, null, false);
            List<Note> notes = new LinkedList<>();
            String extension = PreferencesUtils.getInstance(PalmApp.getContext()).getNoteFileExtension();

            for (MindSnagging snagging : snaggings) {
                // Create new note
                Note note = ModelFactory.getNote();
                String content = snagging.getContent() + "![](" + snagging.getPicture() + ")";
                note.setTitle(ModelHelper.getNoteTitle(snagging.getContent(), snagging.getContent()));
                note.setContent(content);
                note.setPreviewImage(snagging.getPicture());
                note.setPreviewContent(ModelHelper.getNotePreview(snagging.getContent()));

                File noteFile = FileHelper.createNewAttachmentFile(PalmApp.getContext(), extension);
                try {
                    // Create note content attachment
                    Attachment atFile = ModelFactory.getAttachment();
                    FileUtils.writeStringToFile(noteFile, note.getContent(), "utf-8");
                    atFile.setUri(FileHelper.getUriFromFile(PalmApp.getContext(), noteFile));
                    atFile.setSize(FileUtils.sizeOf(noteFile));
                    atFile.setPath(noteFile.getPath());
                    atFile.setName(noteFile.getName());
                    AttachmentsStore.getInstance(PalmApp.getContext()).saveModel(atFile);

                    note.setContentCode(atFile.getCode());
                } catch (IOException e) {
                    LogUtils.e(e);
                }

                notes.add(note);
            }
            return notes;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            new CreateNoteTask(notes).execute();
        }
    }

    private static class CreateNoteTask extends AsyncTask<Void, String, String> {

        private List<Note> notes;

        CreateNoteTask(List<Note> notes) {
            this.notes = notes;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Create default notebook
            Notebook notebook = ModelFactory.getNotebook();
            notebook.setTreePath(String.valueOf(notebook.getCode()));
            notebook.setTitle(PalmApp.getStringCompact(R.string.model_name_mind_snagging));
            notebook.setParentCode(0);
            notebook.setColor(ColorUtils.primaryColor(PalmApp.getContext()));
            NotebookStore.getInstance(PalmApp.getContext()).saveModel(notebook);

            for (Note note : notes) {
                note.setTreePath(notebook.getTreePath() + "|" + note.getCode());
                note.setParentCode(notebook.getCode());

                // Save note
                NotesStore.getInstance(PalmApp.getContext()).saveModel(note);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent intentLaunch = new Intent(PalmApp.getContext(), MainActivity.class);
            intentLaunch.setAction(Constants.ACTION_RESTART_APP);
            PendingIntent notifyIntent = PendingIntent.getActivity(PalmApp.getContext(),
                    0, intentLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationsHelper mNotificationsHelper = new NotificationsHelper(PalmApp.getContext());
            mNotificationsHelper.createNotification(R.drawable.ic_save_white,
                    PalmApp.getStringCompact(R.string.snagging_transfer_title), notifyIntent)
                    .setMessage(PalmApp.getStringCompact(R.string.snagging_transfer_message))
                    .setLedActive();
            mNotificationsHelper.show();
        }
    }
}
