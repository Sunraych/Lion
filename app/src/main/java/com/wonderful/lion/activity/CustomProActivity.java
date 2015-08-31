package com.wonderful.lion.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderful.lion.R;
import com.wonderful.lion.service.FloatingService;
import com.wonderful.lion.uitl.ConfigUtil;
import com.wonderful.lion.uitl.Util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sun Ruichuan on 2015/8/29.
 */
public class CustomProActivity extends Activity implements View.OnClickListener {

    private Button title_left_button;
    private Button title_right_button;
    private TextView title_textView;
    private ArrayList<HashMap<String, Object>> allProcess;
    private Util util = Util.getUtil();

    private EditText et_custompro_input;
    private Button btn_start;
    private ConfigUtil configUtil;
    private String custom_proc_name;
    private boolean isBtnClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custompro);
        init();
        initTitle();
    }

    private void init() {
        allProcess = util.getAllProcess();

        et_custompro_input = (EditText) findViewById(R.id.et_input_pro);
        if (util.getPACKAGENAME() != null && !util.getPACKAGENAME().isEmpty())
            et_custompro_input.setText(util.getPACKAGENAME());

        btn_start = (Button) findViewById(R.id.btn_start_from_custom);
        btn_start.setOnClickListener(this);

        configUtil = new ConfigUtil(this);

        checkStarted();
    }

    private void initTitle() {
        title_left_button = (Button) findViewById(R.id.left_btn);
        title_left_button.setBackgroundResource(R.drawable.title_back);
        title_left_button.setOnClickListener(this);

        title_textView = (TextView) findViewById(R.id.title_text);
        title_right_button = (Button) findViewById(R.id.right_btn);
        title_right_button.setVisibility(View.GONE);
        title_textView.setText(R.string.custompro_activity_title);
    }

    // 查看是否输入的包名有已有的包名
    private void start_test() {

        if (util.isServiceRunning()) {
            stop();
        }

        custom_proc_name = et_custompro_input.getText().toString();

        if (custom_proc_name.isEmpty()) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.custompro_activity_title),
                    Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < allProcess.size(); i++) {
            if (custom_proc_name.equals(allProcess.get(i).get("packagename"))) {

                util.setPACKAGENAME(String.valueOf(allProcess.get(i).get(
                        "packagename")));
                util.setAPPname(String
                        .valueOf(allProcess.get(i).get("appname")));
                util.setLOGnum(0);
                util.setLOG_FLAG(!util.isOffline());
                util.setFLOW_FLAG(true);
                start_flow(custom_proc_name);
                return;
            } else {
                util.setPACKAGENAME(custom_proc_name);
                util.setAPPname(custom_proc_name);
            }
        }

        start_flow(custom_proc_name);

    }

    public void checkStarted() {
        if (util.isServiceRunning())
            isBtnClick = true;
    }

    private void stop() {
        Intent intent = new Intent();
        intent.setClass(this, FloatingService.class);
        util.setDetailActivity(null);
        util.setProcessCpuRatio("0.00%");
        configUtil.setProcess("");
        stopService(intent);
    }

    private void start_flow(String packagename) {
        Intent intent = new Intent();
        intent.setClass(this, FloatingService.class);
        util.setDetailActivity(null);
        long time = System.currentTimeMillis();
        configUtil.setLogTime(time);
        configUtil.setProcess(packagename);
        startService(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.btn_start_from_custom:
                start_test();
                break;

            default:
                break;
        }
    }
}
