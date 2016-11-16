package com.wonderful.lion.uitl;

import android.util.Log;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class DebugLog {
    public static boolean DEBUG = true;
    private static String name = "Debug";

    public static void e(String msg) {

        if (msg != null)
            Log.e(name, msg);
        else
            Log.e(name, "error, log msg is null!");
    }

    public static void e(int msg) {

        Log.e(name, msg + "");

    }

    public static void e(double msg) {

        Log.e(name, msg + "");

    }
}
