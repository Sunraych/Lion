package com.wonderful.lion.uitl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class ConfigUtil {

    private final String PROCESS = "process";
    private final String isUpdate = "isUpdate";
    private final String DELAY = "Delay";
    private final String LOG_TIME = "LogTime";
    private final int Default_DELAY = 3000;
    private final String UID = "uid";
    private final String PHONE_MODEL = "phone_model";
    private final String RELEASE_VERSION = "release_version";
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
        return sp.getInt(FLOW_TYPE, 0);
    }

    public void setFLOW_TYPE(int fLOW_TYPE) {
        editor.putInt(FLOW_TYPE, fLOW_TYPE);
        editor.commit();
    }

    public String getPHONE_MODEL() {
        return sp.getString(PHONE_MODEL, "");
    }

    public void setPHONE_MODEL(String pHONE_MODEL) {
        editor.putString(PHONE_MODEL, pHONE_MODEL);
        editor.commit();
    }

    public String getRELEASE_VERSION() {
        return sp.getString(RELEASE_VERSION, "");
    }

    public void setRELEASE_VERSION(String rELEASE_VERSION) {
        editor.putString(RELEASE_VERSION, rELEASE_VERSION);
        editor.commit();
    }


    public String getUID() {
        return sp.getString(UID, "123456789");
    }

    public void setUID(String uid) {
        editor.putString(UID, uid);
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

    public boolean isUpdate() {
        return sp.getBoolean(isUpdate, false);
    }

    public void setUpdate(boolean update) {
        editor.putBoolean(isUpdate, update);
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
