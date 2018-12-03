package me.shouheng.notepal.common.exception;

/**
 * Created WngShhng on 2018/11/30.
 */
public class NoteNotFoundException extends Exception {

    private long code;

    public NoteNotFoundException(long code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return "Note with code " + code + " not found! ";
    }
}
