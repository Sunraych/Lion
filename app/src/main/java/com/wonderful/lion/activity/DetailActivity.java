package com.wonderful.lion.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderful.lion.Data.DataGlobal;
import com.wonderful.lion.R;
import com.wonderful.lion.service.FloatingService;
import com.wonderful.lion.service.GetAppLaunchTime;
import com.wonderful.lion.uitl.ConfigUtil;
import com.wonderful.lion.uitl.DebugLog;
import com.wonderful.lion.uitl.LogFile;
import com.wonderful.lion.uitl.ShellUtil;
import com.wonderful.lion.uitl.Util;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Sun Ruichuan on 2015/8/29.
 */
public class DetailActivity extends Activity implements OnClickListener {
    private final int JSONRETURN = 1;
    private final int CONNECTION_FAILED = 2;
    private final int LOADING = 1;
    private final int UPLOADING = 4;
    private final int APP_FOUND = DataGlobal.APP_FOUND;
    private final int APP_NOT_FOUND = DataGlobal.APP_NOT_FOUND;
    public Button title_left_button;
    public Button title_right_button;
    public TextView title_textView;
    public ArrayList<LogFile> logsUnDone;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case APP_FOUND:
                    String spendTime = getString(R.string.startapp)
                            + (DataGlobal.END_TIME - DataGlobal.START_TIME)
                            + getString(R.string.ms);
                    Toast.makeText(getApplicationContext(), spendTime,
                            Toast.LENGTH_LONG).show();
                    break;

                case APP_NOT_FOUND:
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.startapp_timeout), Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    public ShellUtil shellUtil;
    private TextView cpuText, ramText, grafficText, batteryText, debugText;
    private CheckBox logCheck, flowCheck;
    private Button controlBtn;
    private String appname;
    private String packagename;
    private String uid;
    private String appuid;
    private Intent intent;
    private ConfigUtil configutil;
    private Util util = Util.getUtil();
    private boolean isBtnClick = false;
    private long time;
    private int errorn = -1;
    private String errormsg = null;
    private String LogPath = Environment.getExternalStorageDirectory()
            .toString() + "/" + "TestData" + "/";
    private ProgressDialog pd;
    private Dialog alertDialog;
    private HashMap<String, String> data;
    private String filename;
    private String mainActivity;
    private GetAppLaunchTime getAppLaunchTime = null;
    private long traffic_wifi = 0;
    private long traffic_3g = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent data = this.getIntent();
        appname = data.getStringExtra("appname");
        packagename = data.getStringExtra("packagename");
        mainActivity = data.getStringExtra("mainactivity");
        uid = data.getStringExtra("uid");
        //将int型uid转化为识别uid
        appuid = "u0a" + uid.substring(uid.length() - 3, uid.length());

        getAppLaunchTime = new GetAppLaunchTime();
        initTitle();
        initView();

        String command[] = {"dumpsys", "batterystats", "|", "grep", "Uid " + appuid};
        String command_test[] = {"dumpsys", "batterystats"};
        ShellUtil.CommandResult cmdresult = ShellUtil.execCommand(command_test, false);

        if (cmdresult.result != -1 && !TextUtils.isEmpty(cmdresult.successMsg)) {
            DebugLog.e(cmdresult.successMsg);
            debugText.setText(cmdresult.successMsg);
        } else {
            DebugLog.e(cmdresult.errorMsg);
            debugText.setText(cmdresult.errorMsg);
        }
    }

    private void initView() {

        configutil = new ConfigUtil(this);

        //调试框
        debugText = (TextView) findViewById(R.id.tv_debug);

        if (DebugLog.DEBUG) {
            debugText.setVisibility(View.VISIBLE);
        } else {
            debugText.setVisibility(View.GONE);
        }

        controlBtn = (Button) findViewById(R.id.control_btn);
        controlBtn.setOnClickListener(this);

        cpuText = (TextView) findViewById(R.id.cpu_text);
        ramText = (TextView) findViewById(R.id.ram_text);
        grafficText = (TextView) findViewById(R.id.traffic_text);
        batteryText = (TextView) findViewById(R.id.battery_text);

        logCheck = (CheckBox) findViewById(R.id.checkBox_log);
        flowCheck = (CheckBox) findViewById(R.id.checkBox_flow);

        if (!TextUtils.isEmpty(util.getPACKAGENAME())
                && util.getPACKAGENAME().equals(packagename)) {
            logCheck.setChecked(util.isLOG_FLAG());
            flowCheck.setChecked(util.isFLOW_FLAG());
            checkStarted();
            util.setDetailActivity(this);
        } else {
            logCheck.setChecked(true);
            flowCheck.setChecked(true);
        }
        logCheck.setOnCheckedChangeListener(new CheckListener());
        flowCheck.setOnCheckedChangeListener(new CheckListener());

    }

    private void initTitle() {
        title_left_button = (Button) findViewById(R.id.left_btn);
        title_left_button.setBackgroundResource(R.drawable.title_back);
        title_left_button.setOnClickListener(this);
        title_textView = (TextView) findViewById(R.id.title_text);

        title_right_button = (Button) findViewById(R.id.right_btn);
        title_right_button.setBackgroundResource(R.drawable.title_setting);
        title_right_button.setOnClickListener(this);

        if (TextUtils.isEmpty(util.getPACKAGENAME())) {
            // 没监测时显示设置按钮
            title_right_button.setVisibility(View.VISIBLE);
        } else {
            // 监测时不显示设置按钮
            title_right_button.setVisibility(View.GONE);
        }

        title_textView.setText(appname);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.control_btn:
                changeButtonStyle();

                break;
            case R.id.right_btn:
                settings();
                title_right_button.setBackgroundResource(R.drawable.title_setting);
                break;
            default:
                break;
        }
    }

    private void settings() {
        intent = new Intent();
        intent.setClass(this, SettingActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    public void changeButtonStyle() {
        if (isBtnClick == false) {
            isBtnClick = true;
            controlBtn.setText(getResources().getText(R.string.stop));
            controlBtn.setBackgroundResource(R.drawable.stop_btn_bg);

            title_right_button.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(util.getPACKAGENAME())) {
                stop();
            }

            util.setPACKAGENAME(packagename);
            util.setAPPname(appname);
            util.setLOGnum(0);
            util.setLOG_FLAG(logCheck.isChecked());
            util.setFLOW_FLAG(flowCheck.isChecked());

            // 开启显示悬浮窗Service
            start_flow();
            // 显示界面数据
            refreshUI();

            startApp();

        } else {
            isBtnClick = false;
            controlBtn.setText(getResources().getText(R.string.start));
            controlBtn.setBackgroundResource(R.drawable.start_btn_bg);
            util.setPACKAGENAME(null);

            title_right_button.setVisibility(View.VISIBLE);

            stop();
        }
    }

    private void startApp() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if ("com.wonderful.lion".equals(packagename)) {
            // 如果是自己就不kill
            return;
        } else {
            am.killBackgroundProcesses(packagename);

            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(packagename,
                    mainActivity));

            if (DebugLog.DEBUG)
                DebugLog.e(mainActivity);

            // thisTime即是App的启动时间
            long thisTime = getAppLaunchTime
                    .startActivityWithTime(launchIntent);
            Toast.makeText(
                    DetailActivity.this,
                    this.getString(R.string.startapp) + thisTime
                            + this.getString(R.string.ms), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void showDefaultText() {
        ramText.setText(getResources().getText(R.string.loading_data));
        cpuText.setText(getResources().getText(R.string.loading_data));
        grafficText.setText(getResources().getText(R.string.loading_data));
        batteryText.setText(getResources().getText(R.string.loading_data));
    }

    // 判断再次进入已被监测的进程时，设置按钮
    public void checkStarted() {
        if (isBtnClick == false) {
            isBtnClick = true;
            controlBtn.setText(getResources().getText(R.string.stop));
            controlBtn.setBackgroundResource(R.drawable.stop_btn_bg);
        }
        refreshUI();
    }

    private void start_flow() {
        intent = new Intent();
        intent.setClass(this, FloatingService.class);
        util.setDetailActivity(this);
        time = System.currentTimeMillis();
        configutil.setLogTime(time);
        configutil.setProcess(packagename);
        startService(intent);
    }

    public void refreshUI() {
        if (util.getFLAG() == 1) {
            cpuText.setText(getResources().getString(R.string.cpu_detail)
                    + util.getProcessCpuRatio());
            ramText.setText(getResources().getString(R.string.memory_detail)
                    + util.transSize(util.getPSS_MEM()));

            // grafficText.setText(getResources().getString(R.string.wifi)
            // + util.transSize(Long.parseLong(util.getTRAFFIC_WIFI()))
            // + "\r" + getResources().getString(R.string.mobile)
            // + util.transSize(Long.parseLong(util.getTRAFFIC_3G())));

            traffic_wifi = Long.parseLong(util.getTRAFFIC_WIFI());
            traffic_3g = Long.parseLong(util.getTRAFFIC_3G());

            if (configutil.getFLOW_TYPE() == 1) {
                // 显示总流量数
                grafficText.setText(getResources().getText(R.string.wifi)
                        .toString()
                        + util.transSize(traffic_wifi)
                        + "\r"
                        + getResources().getText(R.string.mobile).toString()
                        + util.transSize(traffic_3g));
            } else {
                grafficText.setText(getResources().getText(R.string.wifi)
                        .toString()
                        + util.transSpeed(traffic_wifi)
                        + "\r"
                        + getResources().getText(R.string.mobile).toString()
                        + util.transSpeed(traffic_3g));
            }

            if (util.isCharging()) {
                batteryText
                        .setText(getResources().getString(R.string.charging));
            } else {
                batteryText.setText(getResources().getString(
                        R.string.voltage_detail)
                        + util.getVoltage()
                        + getResources().getString(R.string.mv));
            }
        } else if (util.getFLAG() == 2) {
            ramText.setText(getResources().getText(R.string.no_process));
            cpuText.setText(getResources().getText(R.string.no_process));
            grafficText.setText(getResources().getText(R.string.no_process));
            batteryText.setText(getResources().getText(R.string.no_process));
        } else if (util.getFLAG() == 0) {
            showDefaultText();
        }
    }

    private void stop() {

        intent = new Intent();
        intent.setClass(this, FloatingService.class);
        util.setDetailActivity(null);
        util.setProcessCpuRatio("0.00%");
        configutil.setProcess("");
        stopService(intent);
        cpuText.setText(getResources().getText(R.string.result));
        ramText.setText(getResources().getText(R.string.result));
        grafficText.setText(getResources().getText(R.string.result));
        batteryText.setText(getResources().getText(R.string.result));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        util.setDetailActivity(null);
    }

    class CheckListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.checkBox_log:
                    if (!TextUtils.isEmpty(util.getPACKAGENAME())) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getText(R.string.log_check),
                                Toast.LENGTH_LONG).show();
                    }
                    util.setLOG_FLAG(isChecked);
                    break;
                case R.id.checkBox_flow:
                    if (!TextUtils.isEmpty(util.getPACKAGENAME())) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getText(R.string.log_check),
                                Toast.LENGTH_LONG).show();
                    }
                    util.setFLOW_FLAG(isChecked);
                    break;
                default:
                    break;
            }
        }
    }

}
