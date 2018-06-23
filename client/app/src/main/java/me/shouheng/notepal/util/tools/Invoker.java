package me.shouheng.notepal.util.tools;

import android.os.Handler;
import android.os.Looper;

public class Invoker<T> extends Thread {

	private Callback<T> callback;

	public Invoker(Callback<T> callback) {
		this.callback = callback;
	}

	@Override
	public synchronized void start() {
		callback.onBefore();
		super.start();
	}

	@Override
	public void run() {
		Message<T> message = callback.onRun();
		new Handler(Looper.getMainLooper()).post(() -> callback.onAfter(message));
	}
}
