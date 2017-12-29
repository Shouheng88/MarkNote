package me.shouheng.notepal.util.tools;

import android.os.Handler;
import android.os.Looper;

public class Invoker extends Thread {

	private Callback callback;

	public Invoker(Callback callback) {
		this.callback = callback;
	}

	@Override
	public synchronized void start() {
		callback.onBefore();
		super.start();
	}

	@Override
	public void run() {
		final boolean b = callback.onRun();
		new Handler(Looper.getMainLooper()).post(() -> callback.onAfter(b));
	}
}
