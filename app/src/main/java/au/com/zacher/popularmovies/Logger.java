package au.com.zacher.popularmovies;

import android.content.Context;
import android.util.Log;

/**
 * Wrapper for the logger to simplify logging a little bit with string resources
 */
public class Logger {
    private static boolean VERBOSE = true;

    static {
        // prevent accidental verbose logging in release
        if (!BuildConfig.DEBUG) {
            VERBOSE = false;
        }
    }

    private static Context appContext;
    public static void setContext(Context appContext) {
        Logger.appContext = appContext;
    }

    @SuppressWarnings("unused")
    public static void d(int formatStringId, Object... arguments) {
        Logger.writeLog(Log.DEBUG, formatStringId, arguments);
    }

    @SuppressWarnings("unused")
    public static void e(int formatStringId, Object... arguments) {
        Logger.writeLog(Log.ERROR, formatStringId, arguments);
    }

    @SuppressWarnings("unused")
    public static void i(int formatStringId, Object... arguments) {
        Logger.writeLog(Log.INFO, formatStringId, arguments);
    }

    @SuppressWarnings("unused")
    public static void v(int formatStringId, Object... arguments) {
        // don't verbose log if we're not in debug mode
        if (VERBOSE) {
            Logger.writeLog(Log.VERBOSE, formatStringId, arguments);
        }
    }

    @SuppressWarnings("unused")
    public static void w(int formatStringId, Object... arguments) {
        Logger.writeLog(Log.WARN, formatStringId, arguments);
    }

    @SuppressWarnings("unused")
    public static void wtf(Throwable tr, Object... arguments) {
        Logger.wtf(R.string.log_wtf, tr, arguments);
    }
    @SuppressWarnings("unused")
    public static void wtf(int formatStringId, Throwable tr, Object... arguments) {
        String tag = appContext.getString(R.string.log_tag);
        String logStr = appContext.getString(formatStringId, arguments);

        Log.wtf(tag, logStr, tr);
    }

    private static void writeLog(int type, int formatStringId, Object... arguments) {
        String tag = appContext.getString(R.string.log_tag);
        String logStr = appContext.getString(formatStringId, arguments);

        Log.println(type, tag, logStr);
    }


    @SuppressWarnings("unused")
    public static void logActionCreate(String className) {
        Logger.v(R.string.log_onCreate_formatter, className);
    }
    @SuppressWarnings("unused")
    public static void logMethodCall(String name, String className) {
        Logger.v(R.string.log_method_call_formatter, name, className);
    }
}