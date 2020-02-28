package me.shouheng.commons.utils;

import android.util.Log;

import java.util.List;
import java.util.Map;

import me.shouheng.commons.BuildConfig;

public class LogUtils {

    private static final boolean showLog = BuildConfig.DEBUG;

    private final static String DEFAULT_LOG_TAG = "logger";

    private static final int V = 1;
    private static final int D = 2;
    private static final int I = 3;
    private static final int W = 4;
    private static final int E = 5;

    public static void v(Object obj) {
        logs(LogUtils.V, null, obj);
    }

    public static void v(String tag, Object obj) {
        logs(LogUtils.V, tag, obj);
    }

    public static void d(Object obj) {
        logs(LogUtils.D, null, obj);
    }

    public static void d(String tag, Object obj) {
        logs(LogUtils.D, tag, obj);
    }

    public static void i(Object obj) {
        logs(LogUtils.I, null, obj);
    }

    public static void i(String tag, Object obj) {
        logs(LogUtils.I, tag, obj);
    }

    public static void w(Object obj) {
        logs(LogUtils.W, null, obj);
    }

    public static void w(String tag, Object obj) {
        logs(LogUtils.W, tag, obj);
    }

    public static void e(Object obj) {
        logs(LogUtils.E, null, obj);
    }

    public static void e(String tag, Object obj) {
        logs(LogUtils.E, tag, obj);
    }

    private static void logs(int logType, String tagStr, Object obj) {
        if (!showLog) {
            return;
        }
        String msg;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        String tag = (tagStr == null ? DEFAULT_LOG_TAG : tagStr);
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");

        if (obj == null) {
            msg = "Log with null Object";
        } else {
            if (obj instanceof Map) {
                msg = StringUtils.MapToString((Map<?, ?>) obj);
            } else if (obj instanceof List) {
                msg = StringUtils.ListToString((List<?>) obj);
            } else {
                msg = obj.toString();
            }
        }

        if (msg != null) {
            stringBuilder.append(msg);
        }

        String logStr = stringBuilder.toString();

        switch (logType) {
            case V: Log.v(tag, logStr);break;
            case D: Log.d(tag, logStr);break;
            case I: Log.i(tag, logStr);break;
            case W: Log.w(tag, logStr);break;
            case E: Log.e(tag, logStr);break;
        }
    }
}
