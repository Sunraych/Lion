package com.wonderful.lion.activity;

/**
 * Created by Sun Ruichuan on 2015/8/29.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderful.lion.R;
import com.wonderful.lion.uitl.ClearCacheUtil;
import com.wonderful.lion.uitl.ConfigUtil;

import java.io.File;

public class SettingActivity extends Activity implements OnClickListener {
    private static final int CLEARCACHE = 4;
    private static final int CLEARCACHE_FAIL = 5;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @SuppressWarnings("deprecation")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEARCACHE:
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.clearcache_message),
                            Toast.LENGTH_LONG).show();
                    break;
                case CLEARCACHE_FAIL:
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.clearcache_fail),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    private Button title_left_button;
    private Button title_right_button;
    private TextView title_textView;
    private LinearLayout refresh_time_layout;
    private LinearLayout tv_flow_option;
    private TextView current_refresh_time;
    private TextView current_flow_type;
    private TextView about_me;
    private TextView help;
    private String[] setTimes;
    private TextView clearcache;
    private ConfigUtil configutil;
    private ClearCacheUtil clearCache;
    private Dialog clearCacheDialog;
    private String logpath = "/TestData/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        configutil = new ConfigUtil(this);
        clearCache = new ClearCacheUtil();

        initTitle();
        initView();
        initSetTimes();
    }

    private void initTitle() {
        title_left_button = (Button) findViewById(R.id.left_btn);
        title_textView = (TextView) findViewById(R.id.title_text);
        title_right_button = (Button) findViewById(R.id.right_btn);
        title_left_button.setOnClickListener(this);
        title_left_button.setBackgroundResource(R.drawable.title_back);
        title_right_button.setVisibility(View.GONE);
        title_textView.setText(getResources().getString(R.string.setting));
    }

    private void initView() {
        refresh_time_layout = (LinearLayout) findViewById(R.id.setting_refresh_time);
        refresh_time_layout.setOnClickListener(this);
        current_refresh_time = (TextView) findViewById(R.id.refresh_time_current);
        current_refresh_time.setText(configutil.getDELAY() / 1000
                + getResources().getString(R.string.second));

        tv_flow_option = (LinearLayout) findViewById(R.id.setting_flow_option);
        tv_flow_option.setOnClickListener(this);
        current_flow_type = (TextView) findViewById(R.id.setting_flow_speed_current);
        if (configutil.getFLOW_TYPE() == 1) {
            current_flow_type.setText(getResources().getString(
                    R.string.flow_all));
        } else {
            current_flow_type.setText(getResources().getString(
                    R.string.flow_speed_now));
        }


        about_me = (TextView) findViewById(R.id.setting_about);
        about_me.setOnClickListener(this);

        help = (TextView) findViewById(R.id.setting_help);
        help.setOnClickListener(this);

        clearcache = (TextView) findViewById(R.id.setting_clearcache);
        clearcache.setOnClickListener(this);

    }

    private void initSetTimes() {
        setTimes = getResources().getStringArray(R.array.settingRefreshTime);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.setting_refresh_time:
                setRefreshTime();
                break;

            case R.id.setting_about:
                about();
                break;

            case R.id.setting_flow_option:
                setFlowOptions();
                break;
            case R.id.setting_clearcache:
                clearCache();
                break;
            case R.id.setting_help:
                help();
                break;
            default:
                break;
        }
    }

    // 设置刷新时间
    private void setRefreshTime() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.refresh_time_set)
                .setSingleChoiceItems(setTimes,
                        configutil.getDELAY() / 1000 - 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                configutil.setDELAY((which + 1) * 1000);
                                current_refresh_time.setText(setTimes[which]);
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
    }

    // 设置网络监控类型，流量or总量
    private void setFlowOptions() {

        String[] flowType = {
                getResources().getString(R.string.flow_speed_monitor),
                getResources().getString(R.string.flow_speed_monitor_all)};

        new AlertDialog.Builder(this)
                .setTitle(R.string.flow_speed)
                .setSingleChoiceItems(flowType, configutil.getFLOW_TYPE(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                configutil.setFLOW_TYPE(which);
                                dialog.dismiss();

                                // 0为网速，1为总量
                                switch (which) {
                                    case 0:
                                        current_flow_type
                                                .setText(getResources().getString(
                                                        R.string.flow_speed_now));

                                        break;
                                    case 1:

                                        current_flow_type.setText(getResources()
                                                .getString(R.string.flow_all));
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
    }

    private void about() {
        Intent intent = new Intent();
        intent.setClass(this, AboutActivity.class);
        startActivity(intent);
    }

    private void help() {
        Intent intent = new Intent();
        intent.setClass(this, HelpActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    private void clearCache() {
        File file = new File(Environment.getExternalStorageDirectory()
                .toString() + logpath);
        if (file.exists()) {
            if (clearCacheDialog != null && clearCacheDialog.isShowing()) {
                return;
            } else {
                showDialog(CLEARCACHE);
            }
        } else {
            handler.sendEmptyMessage(CLEARCACHE_FAIL);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CLEARCACHE:
                clearCacheDialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.clearcache))
                        .setMessage(
                                getResources().getString(
                                        R.string.clearcache_request))
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (clearCache.clearCache(logpath) != true) {
                                            handler.sendEmptyMessage(CLEARCACHE_FAIL);
                                        } else {
                                            handler.sendEmptyMessage(CLEARCACHE);
                                        }
                                    }
                                })
                        .setNegativeButton(
                                getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        return;
                                    }
                                }).create();
                return clearCacheDialog;
            default:
                break;
        }
        return super.onCreateDialog(id);
    }
}
