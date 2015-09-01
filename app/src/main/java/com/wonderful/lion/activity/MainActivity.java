package com.wonderful.lion.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderful.lion.R;
import com.wonderful.lion.service.FloatingService;
import com.wonderful.lion.uitl.ListTools;
import com.wonderful.lion.uitl.Util;
import com.wonderful.lion.widget.RefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements OnClickListener {

    private static final int REFRESH_DONE = 1111;
    public Button title_left_button;
    public Button title_right_button;
    public TextView title_textView;
    public ListTools listTools = new ListTools();
    public ItemsAdapter adapter;
    private RefreshListView processList;
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_DONE:
                    adapter.notifyDataSetChanged();
                    processList.changeHeaderToRefershed();
                    Toast.makeText(MainActivity.this,
                            R.string.process_list_refresh_done, Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private ArrayList<HashMap<String, Object>> allProcess;
    // private final int DELAY = 2000;
    private Dialog alertdialog_exit;
    private Util util;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTitle();
        initView();
        initProcessList();
    }

    private void initTitle() {
        title_left_button = (Button) findViewById(R.id.left_btn);
        title_left_button.setOnClickListener(this);
        title_left_button.setVisibility(View.GONE);
        title_textView = (TextView) findViewById(R.id.title_text);
        title_right_button = (Button) findViewById(R.id.right_btn);
        title_right_button.setBackgroundResource(R.drawable.title_setting);
        title_right_button.setOnClickListener(this);
        title_textView.setText(R.string.main_activity_title);
        title_textView.setOnClickListener(this);
    }

    private void initView() {
        processList = (RefreshListView) findViewById(R.id.process_list);
        processList.setOnRefreshListener(new RefreshListView.RefreshListener() {
            public void refreshing() {

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        allProcess = listTools.getItems(MainActivity.this);
                        handler.sendEmptyMessage(REFRESH_DONE);
                    }
                }.start();
            }
        });
    }

    private void initProcessList() {

        util = new Util();

        allProcess = listTools.getItems(this);
        adapter = new ItemsAdapter(this);
        processList.setAdapter(adapter);
        processList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> tempItem = (HashMap<String, Object>) arg0
                        .getAdapter().getItem(arg2);

                String appname = tempItem.get("appname").toString();
                String packagename = tempItem.get("packagename").toString();
                String mainActivity = tempItem.get("mainactivity").toString();

                Intent intent = new Intent(MainActivity.this,
                        DetailActivity.class);
                intent.putExtra("appname", appname);
                intent.putExtra("packagename", packagename);
                intent.putExtra("mainactivity", mainActivity);

                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                setting();
                title_right_button.setBackgroundResource(R.drawable.title_setting);
                break;
            case R.id.title_text:
                count++;
                if (count == 5) {
                    customPro();
                    count = 0;
                }
                break;
            default:
                break;
        }
    }

    private void setting() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void customPro() {
        util.setAllProcess(allProcess);
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, CustomProActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            alertdialog_exit = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.exit))
                    .setMessage(getResources().getString(R.string.exit_dialog))
                    .setPositiveButton(
                            getResources().getString(R.string.continue_monitor),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(
                                            Intent.ACTION_MAIN, null);
                                    intent.addCategory(Intent.CATEGORY_HOME);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                    startActivity(intent);
                                    onStop();
                                }
                            })
                    .setNeutralButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                    MainActivity.this.onDestroy();
                                    System.exit(0);
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

            if (alertdialog_exit != null) {
                if (alertdialog_exit.isShowing()) {

                } else {
                    alertdialog_exit.show();
                }
            } else {
                if (alertdialog_exit != null)
                    alertdialog_exit.show();
            }

			/* �������back�˳� */
            // if (System.currentTimeMillis() - timeTemp < DELAY) {
            // finish();
            // this.onDestroy();
            // System.exit(0);
            // } else {
            // timeTemp = System.currentTimeMillis();
            // Toast.makeText(MainActivity.this, R.string.exit_toast,
            // Toast.LENGTH_SHORT).show();
            // }
        }
        return true;
    }

    @Override
    protected void onDestroy() {

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, FloatingService.class);
        stopService(intent);

        handler.removeCallbacksAndMessages(this);
        util.setPACKAGENAME(null);
        super.onDestroy();
    }

    class ItemsAdapter extends BaseAdapter {
        private LayoutInflater _inflater = null;

        public ItemsAdapter(Context context) {
            _inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return allProcess.size();
        }

        @Override
        public HashMap<String, Object> getItem(int position) {
            return allProcess.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            View view;
            if (convertView == null) {
                holder = new Holder();
                view = _inflater.inflate(R.layout.app_item, null);
                holder.imgView = (ImageView) view.findViewById(R.id.item_image);
                holder.appnameText = (TextView) view
                        .findViewById(R.id.item_appname);
                holder.packageText = (TextView) view
                        .findViewById(R.id.item_packagename);

                view.setTag(holder);
            } else {
                view = convertView;
                holder = (Holder) view.getTag();
            }

            holder.imgView.setImageDrawable((Drawable) allProcess.get(position)
                    .get("appimage"));
            holder.appnameText.setText(allProcess.get(position).get("appname")
                    .toString());
            holder.packageText.setText(allProcess.get(position)
                    .get("packagename").toString());

            return view;
        }

        public class Holder {
            ImageView imgView;
            TextView appnameText;
            TextView packageText;
        }
    }
}
