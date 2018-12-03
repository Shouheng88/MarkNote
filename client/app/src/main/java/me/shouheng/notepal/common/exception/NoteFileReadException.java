package me.shouheng.notepal.common.exception;

import me.shouheng.data.entity.Note;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: NoteFileReadException, v 0.1 2018/12/1 0:44 shouh Exp$
 */
public class NoteFileReadException extends Exception {

    private Note note;

    public NoteFileReadException(Note note) {
        this.note = note;
    }

    @Override
    public String getMessage() {
        return "Failed to read file of note " + note;
    }
}
