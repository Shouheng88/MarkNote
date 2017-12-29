package me.shouheng.notepal.util.tools;

public interface Callback {

	void onBefore();

	boolean onRun();

	void onAfter(boolean b);
}
