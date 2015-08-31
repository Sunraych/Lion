package com.wonderful.lion.uitl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class ConfigUtil {

    private final String PROCESS = "process";
    private final String DELAY = "Delay";
    private final String LOG_TIME = "LogTime";
    private final int Default_DELAY = 3000;

    private final String FLOW_TYPE = "flow_type";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String name = "my_config";

    @SuppressLint("CommitPrefEdits")
    public ConfigUtil(Context context) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public int getFLOW_TYPE() {
        return sp.getInt(FLOW_TYPE, 1);
    }

    public void setFLOW_TYPE(int fLOW_TYPE) {
        editor.putInt(FLOW_TYPE, fLOW_TYPE);
        editor.commit();
    }

    public long getLogTime() {
        return sp.getLong(LOG_TIME, -1);
    }

    public void setLogTime(long logTime) {
        editor.putLong(LOG_TIME, logTime);
        editor.commit();
    }

    public int getDELAY() {
        return sp.getInt(DELAY, Default_DELAY);
    }

    public void setDELAY(int dELAY) {
        editor.putInt(DELAY, dELAY);
        editor.commit();
    }

    public String getProcess() {
        return sp.getString(PROCESS, "");
    }

    public void setProcess(String process) {
        editor.putString(PROCESS, process);
        editor.commit();
    }

}
