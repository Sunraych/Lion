package com.wonderful.lion.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

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

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            int pid = appProcessInfo.pid;
            String processName = appProcessInfo.processName;
            if (processName.equals(process)) {
                util.setPID(pid);
                return pid;
            }
        }
        util.setPID(-1);
        return -1;
    }
}
