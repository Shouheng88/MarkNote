package me.shouheng.notepal.util.tools;

public interface Callback<T> {

	void onBefore();

	Message<T> onRun();

	void onAfter(Message<T> message);
}
