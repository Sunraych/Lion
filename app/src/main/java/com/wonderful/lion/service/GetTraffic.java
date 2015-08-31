package com.wonderful.lion.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import java.util.List;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class GetTraffic {

    public Fluent mobile;
    public Fluent wifi;
    private Context context;
    private ConnectivityManager mConnectivityManager;
    private String packageName;
    private int uid = -1;
    private int count = 0;
    private Fluent lastState;

    public GetTraffic(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
        mConnectivityManager = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        lastState = new Fluent();
        mobile = new Fluent();
        wifi = new Fluent();
        getUid();
    }

    public void getUid() {
        PackageManager pckMan = context.getPackageManager();
        List<PackageInfo> packs = pckMan.getInstalledPackages(0);

        for (PackageInfo p : packs) {
            if (p.applicationInfo.packageName.equals(packageName)) {
                this.uid = p.applicationInfo.uid;
            }
        }
    }

    public void GetFluent() {
        // init traffic data
        if (uid != -1) {
            long rxdata = TrafficStats.getUidRxBytes(uid);
            rxdata = rxdata / 1024;
            long txdata = TrafficStats.getUidTxBytes(uid);
            txdata = txdata / 1024;
            long data_total = rxdata + txdata;

            long up = txdata;
            long down = rxdata;
            long total = data_total;

            if (count == 0) {
                lastState = new Fluent(up, down, total);
                count++;
            }

            Fluent increment = new Fluent(up - lastState.up, down
                    - lastState.down, total - lastState.total);
            lastState = new Fluent(up, down, total);

            switch (getConnectedType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    mobile.add(increment);
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    wifi.add(increment);
                    break;
                default:
                    break;
            }
        }
    }

    public int getConnectedType() {
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            return mNetworkInfo.getType();
        }
        return -1;
    }

    class Fluent {
        public long up;
        public long down;
        public long total;

        public Fluent(long up, long down, long total) {
            this.up = up;
            this.down = down;
            this.total = total;
        }

        public Fluent() {
            this.up = 0;
            this.down = 0;
            this.total = 0;
        }

        public void add(Fluent m) {
            this.up += m.up;
            this.down += m.down;
            this.total += m.total;
        }
    }
}
