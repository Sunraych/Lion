package com.wonderful.lion.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wonderful.lion.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by Sun Ruichuan on 2015/9/1.
 */
public class PhoneMsgActivity extends Activity implements View.OnClickListener {

    private Button title_left_button;
    private Button title_right_button;
    private TextView title_textView;
    private ActivityManager activityManager;
    private TextView textView_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonemsg);

        initTitle();
        showMessage();
    }

    private void initTitle() {
        title_left_button = (Button) findViewById(R.id.left_btn);
        title_left_button.setBackgroundResource(R.drawable.title_back);
        title_left_button.setOnClickListener(this);
        title_textView = (TextView) findViewById(R.id.title_text);
        title_right_button = (Button) findViewById(R.id.right_btn);
        title_right_button.setBackgroundResource(R.drawable.title_setting);
        title_right_button.setOnClickListener(this);
        title_right_button.setVisibility(View.GONE);
        title_textView.setText(R.string.system_detail);
    }

    private void showMessage() {

        HashMap<String, String> info = getInfo();

        textView_msg = (TextView) findViewById(R.id.textView);
        textView_msg.setText("屏幕宽度：" + info.get("width") + "\n" + "屏幕高度：" + info.get("height") + "\n" + "WiFi MAC地址：" + info.get("wifiMac") + "\n" + "CPU型号：" + info.get("cpumodel") + "\n" + "CPU频率：" + info.get("cpufre") + "\n" + "内存大小：" + info.get("memtotal") + "\n" + "可用内存：" + info.get("memavil") + "\n" + "IMEI：" + info.get("imei") + "\n" + "手机序列号：" + info.get("imsi") + "\n" + "手机型号：" + info.get("model") + "\n" + "手机品牌：" + info.get("brand") + "\n" + "手机号码：" + info.get("number") + "\n");
    }

    private HashMap<String, String> getInfo() {
        int[] display = getWeithAndHeight();
        String[] cpuInfo = getCpuInfo();
        String[] memInfo = getTotalMemory();
        String[] getPhoneInfo = getPhoneInfo();

        HashMap<String, String> info = new HashMap<>();
        info.put("width", String.valueOf(display[0]));
        info.put("height", String.valueOf(display[1]));
        info.put("wifiMac", getMacAddress());
        info.put("cpumodel", cpuInfo[0]);
        info.put("cpufre", new BigDecimal(Double.valueOf(getMaxCpuFreq()) / 1024 / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " GHz");
        info.put("memtotal", memInfo[0]);
        info.put("memavil", memInfo[1]);
        info.put("imei", getPhoneInfo[0]);
        info.put("imsi", getPhoneInfo[1]);
        info.put("model", getPhoneInfo[2]);
        info.put("brand", getPhoneInfo[3]);
        info.put("number", getPhoneInfo[4]);

        return info;
    }

    //获取手机分辨率
    private int[] getWeithAndHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;              //宽
        int height = dm.heightPixels;           //高
        int display[] = {width, height};
        return display;
    }

    //获取手机mac地址
    private String getMacAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    //获取手机cpu型号和频率
    private String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};  //1-cpu型号  //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }

    public String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    //获取手机总内存和可用内存
    private String[] getTotalMemory() {
        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        String[] result = {"", ""};  //1-total 2-avail
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mi);
        long mTotalMem = 0;
        long mAvailMem = mi.availMem;
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            mTotalMem = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result[0] = Formatter.formatFileSize(this, mTotalMem);
        result[1] = Formatter.formatFileSize(this, mAvailMem);
        return result;
    }

    //获取手机IMEI和型号
    private String[] getPhoneInfo() {
        TelephonyManager mTm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号
        String mtyb = android.os.Build.BRAND;//手机品牌
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        String[] phoneInfo = {imei, imsi, mtype, mtyb, numer};
        return phoneInfo;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                break;
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }
    }
}
