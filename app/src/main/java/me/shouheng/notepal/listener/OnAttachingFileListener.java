package me.shouheng.notepal.listener;

import me.shouheng.notepal.model.Attachment;

public interface OnAttachingFileListener {

    void onAttachingFileErrorOccurred(Attachment attachment);

    void onAttachingFileFinished(Attachment attachment);
}
