package com.wonderful.lion.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.text.TextUtils;

import com.wonderful.lion.uitl.DebugLog;
import com.wonderful.lion.uitl.Util;

import java.util.List;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class GetPID {

    public ActivityManager activitymanager;
    private Util util = Util.getUtil();

    public int getPid(Context context, String process) {

        activitymanager = (ActivityManager) context
                .getSystemService(Service.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcessList = activitymanager
                .getRunningAppProcesses();

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            int pid = getRunningAppProcesses(context, process);
            util.setPID(pid);
            return pid;
        } else {
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
                int pid = appProcessInfo.pid;
                String processName = appProcessInfo.processName;
                if (processName.equals(process)) {
                    util.setPID(pid);
                    return pid;
                }
            }
        }

        util.setPID(-1);
        return -1;
    }


    private int getRunningAppProcesses(Context context, String pkgName) {

        int pid;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(pkgName, list.get(i).service.getPackageName())) {
                if (DebugLog.DEBUG)
                    DebugLog.e("找到 pkgName: " + pkgName);
                pid = list.get(i).pid;
                return pid;
            }
        }
        return -1;
    }
}

