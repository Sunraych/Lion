package com.wonderful.lion.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wonderful.lion.R;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class AboutActivity extends Activity implements View.OnClickListener {

    private Button title_left_button;
    private Button title_right_button;
    private TextView title_textView;
    private TextView tv_version;
    private ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initTitle();
        initView();
    }

    private void initTitle() {
        title_left_button = (Button) findViewById(R.id.left_btn);
        title_textView = (TextView) findViewById(R.id.title_text);
        title_right_button = (Button) findViewById(R.id.right_btn);
        title_right_button.setVisibility(View.GONE);
        title_left_button.setBackgroundResource(R.drawable.title_back);
        title_textView.setText(getResources().getString(R.string.about_me));
        title_left_button.setOnClickListener(this);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo.setOnClickListener(this);
        iv_logo.setImageResource(R.mipmap.icon);
    }

    private void initView() {
        tv_version = (TextView) findViewById(R.id.tv_version);

        try {
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            tv_version.setText(getResources().getString(R.string.version_code) + "v"
                    + pi.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }

    }
}
