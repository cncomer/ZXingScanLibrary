package com.bestjoy.library.scan.utils;

import android.util.Log;

/**
 * Created by bestjoy on 16/3/8.
 */
public class DebugUtils {
    public static final boolean DEBUG_DECODE_THREAD = true;
    public static final boolean DEBUG_QRGEN = true;
    private static final boolean debug = true;
    private static final boolean logProvider = true;


    public static void logD(String tag, String msg) {
        if (debug) {
            Log.d(tag, msg);
        }

    }
    public static void logProvider(String tag, String msg) {
        if (logProvider) {
            Log.d(tag, msg);
        }

    }

    public static void logE(String tag, String msg) {
        Log.e(tag, msg);
    }


}
