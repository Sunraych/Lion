package com.wonderful.lion.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wonderful.lion.R;
import com.wonderful.lion.uitl.DebugLog;
import com.wonderful.lion.uitl.ShellUtil;

/**
 * Created by Jason on 2016/11/15.
 */

public class ExcShellActivity extends Activity implements View.OnClickListener {

    private Button title_left_button;
    private Button title_right_button;
    private TextView title_textView;

    private EditText editText;
    private Button buttonExc;
    private TextView textViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excshell);
        initTitle();
        initView();
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
        title_textView.setText(R.string.excshell);
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.et_input_sh);
        buttonExc = (Button) findViewById(R.id.btn_exc);
        buttonExc.setOnClickListener(this);
        textViewResult = (TextView) findViewById(R.id.tv_cmdresult);
        textViewResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void excShell(){
        String cmd = editText.getText().toString();
//        String[] cmds = cmd.split(" ");

        String[] cmds = {cmd};

        ShellUtil.CommandResult cmdresult = ShellUtil.execCommand(cmds,false);

        textViewResult.setText("");

        if (cmdresult.result != -1 && !TextUtils.isEmpty(cmdresult.successMsg)) {
            textViewResult.setText(cmdresult.successMsg);
        } else {
            textViewResult.setText(cmdresult.errorMsg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.btn_exc:
                excShell();
                break;
            default:
                break;
        }
    }
}
