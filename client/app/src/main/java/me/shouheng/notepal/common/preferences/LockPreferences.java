package me.shouheng.notepal.common.preferences;

import android.content.Context;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;


/**
 * Created by shouh on 2018/4/9.*/
public class LockPreferences extends BasePreferences {

    private static LockPreferences preferences;

    public static LockPreferences getInstance() {
        if (preferences == null) {
            synchronized (LockPreferences.class) {
                if (preferences == null) {
                    preferences = new LockPreferences(PalmApp.getContext());
                }
            }
        }
        return preferences;
    }

    private LockPreferences(Context context) {
        super(context);
    }

    public void setPasswordRequired(boolean isRequired) {
        putBoolean(R.string.key_security_psd_required, isRequired);
    }

    public boolean isPasswordRequired() {
        return getBoolean(R.string.key_security_psd_required, false);
    }

    public void setPassword(String password) {
        putString(R.string.key_security_psd, password);
    }

    public String getPassword() {
        return getString(R.string.key_security_psd, null);
    }

    public int getPasswordFreezeTime() {
        return getInt(R.string.key_security_freeze_time, 5);
    }

    public void setPasswordFreezeTime(int time) {
        putInt(R.string.key_security_freeze_time, time);
    }

    public void setPasswordQuestion(String question) {
        putString(R.string.key_security_psd_question, question);
    }

    public String getPasswordQuestion() {
        return getString(R.string.key_security_psd_question, null);
    }

    public void setPasswordAnswer(String answer) {
        putString(R.string.key_security_psd_answer, answer);
    }

    public String getPasswordAnswer() {
        return getString(R.string.key_security_psd_answer, null);
    }

    public void setLastInputErrorTime(long millis) {
        putLong(R.string.key_security_error_time, millis);
    }

    public long getLastInputErrorTime() {
        return getLong(R.string.key_security_error_time, 0);
    }
}
