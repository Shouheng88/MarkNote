package me.shouheng.notepal.async.onedrive;

import android.os.AsyncTask;

import java.util.List;

import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.provider.AttachmentsStore;

/**
 * Created by shouh on 2018/4/3.*/
public class GetUploadAttachmentTask extends AsyncTask<Void, String, List<Attachment>>{

    private GetUploadAttachmentListener getUploadAttachmentListener;

    GetUploadAttachmentTask(GetUploadAttachmentListener getUploadAttachmentListener) {
        this.getUploadAttachmentListener = getUploadAttachmentListener;
    }

    @Override
    protected List<Attachment> doInBackground(Void... voids) {
        return AttachmentsStore.getInstance().getUploadForOneDrive();
    }

    @Override
    protected void onPostExecute(List<Attachment> attachments) {
        if (getUploadAttachmentListener != null) {
            getUploadAttachmentListener.onGetAttachment(attachments);
        }
    }

    public interface GetUploadAttachmentListener {
        void onGetAttachment(List<Attachment> attachments);
    }
}
