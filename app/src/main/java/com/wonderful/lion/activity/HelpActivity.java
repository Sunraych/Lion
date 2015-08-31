package com.wonderful.lion.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wonderful.lion.R;
import com.wonderful.lion.uitl.ReadFileUtil;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class HelpActivity extends Activity implements View.OnClickListener {
    private Button title_left_button;
    private Button title_right_button;
    private TextView title_textView;
    private TextView tv_help;

    private String res;
    private ReadFileUtil readUtil;
    private String Encoding = "UTF-8";
    private String help;
    private float textSize = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initTitle();
        initView();

        setHelp();

    }

    private void initTitle() {
        title_left_button = (Button) findViewById(R.id.left_btn);
        title_textView = (TextView) findViewById(R.id.title_text);
        title_right_button = (Button) findViewById(R.id.right_btn);
        title_right_button.setVisibility(View.GONE);
        title_left_button.setBackgroundResource(R.drawable.title_back);
        title_textView.setText(getResources().getString(R.string.help));
        title_left_button.setOnClickListener(this);
    }

    private void initView() {
        tv_help = (TextView) findViewById(R.id.tv_help);
    }

    private String readHelp() {
        readUtil = new ReadFileUtil();
        res = readUtil.Read(this, R.raw.help, Encoding);
        return res;
    }

    private void setHelp() {
        help = readHelp();
        if (!TextUtils.isEmpty(help)) {
            tv_help.setText(help);
            tv_help.setTextColor(getResources().getColor(R.color.black));
            tv_help.setTextSize(textSize);
            tv_help.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }
    }
}
