package com.wonderful.lion.service;

import android.content.Context;
import android.text.TextUtils;

import com.wonderful.lion.uitl.ConfigUtil;
import com.wonderful.lion.uitl.Util;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class SetData {

    private GetMemory getmemory;
    private GetPID getPID;
    private GetCpu getCPU;
    private GetTraffic getTraffic;
    private Context context;
    private String myProcess;
    private Util util = Util.getUtil();
    private ConfigUtil configutil;

    private long traffic_temp_wifi = 0;
    private long traffic_temp_3g = 0;
    private long traffic_wifi = 0;
    private long traffic_3g = 0;
    private long time_tmp = 0;
    private long traffic_time = 0;

    public SetData(Context context) {
        this.context = context;
        this.myProcess = util.getPACKAGENAME();
        getmemory = new GetMemory(context);
        getPID = new GetPID();
        getCPU = new GetCpu();
        getPID.getPid(context, myProcess);
        getTraffic = new GetTraffic(context, util.getPACKAGENAME());
        configutil = new ConfigUtil(context);
    }

    public int startSetData() {
        if (TextUtils.isEmpty(myProcess)) {
            myProcess = configutil.getProcess();
            if (TextUtils.isEmpty(myProcess)) {
                return -1;
            }
        }
        // 判断程序被结束后，实时刷新悬浮窗，显示为程序没有运行，需要实时获取pid
        getPID.getPid(context, myProcess);
        // 程序尚未运行
        if (util.getPID() == -1) {
            return -5;
        }

        if (setCPU() != 0) {
            return -2;
        }

        if (setMemory() != 0) {
            return -3;
        }

        if (setTraffic() != 0) {
            return -4;
        }

        return 0;
    }

    public int setMemory() {
        int pid = util.getPID();
        if (pid != -1) {
            int pids[] = new int[1];
            pids[0] = pid;
            int pss = getmemory.getPss(pids);
            int privateDirty = getmemory.getPrivateDirty(pids);
            int sharedirty = getmemory.getShareDirty(pids);

            util.setPSS_MEM(pss);
            util.setPrivateDirty(privateDirty);
            util.setShareDirty(sharedirty);
            return 0;
        } else {
            return 1;
        }
    }

    public int setCPU() {
        getCPU.getCpuRatioInfo(util.getPID());
        return 0;
    }

    public int setTraffic() {
        if (util.getPID() != -1) {
            getTraffic.GetFluent();

            traffic_wifi = getTraffic.wifi.total;
            traffic_3g = getTraffic.mobile.total;
            traffic_time = System.currentTimeMillis();

            if (configutil.getFLOW_TYPE() == 1) {
                util.setTRAFFIC_3G(String.valueOf(traffic_3g));
                util.setTRAFFIC_WIFI(String.valueOf(traffic_wifi));
            } else {
                if (time_tmp == 0) {

                    util.setTRAFFIC_WIFI(String
                            .valueOf((traffic_wifi - traffic_temp_wifi) * 1000
                                    / configutil.getDELAY()));
                    util.setTRAFFIC_3G(String
                            .valueOf((traffic_3g - traffic_temp_3g) * 1000
                                    / configutil.getDELAY()));

                } else {
                    util.setTRAFFIC_WIFI(String
                            .valueOf((traffic_wifi - traffic_temp_wifi) * 1000
                                    / (traffic_time - time_tmp)));


                    util.setTRAFFIC_3G(String
                            .valueOf((traffic_3g - traffic_temp_3g) * 1000
                                    / (traffic_time - time_tmp)));
                }
                traffic_temp_wifi = traffic_wifi;
                traffic_temp_3g = traffic_3g;
                time_tmp = traffic_time;
            }

            // util.setTRAFFIC_3G(String.valueOf(getTraffic.mobile.total));
            // util.setTRAFFIC_WIFI(String.valueOf(getTraffic.wifi.total));
            return 0;
        }
        return -1;
    }

}
