package com.wonderful.lion.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wonderful.lion.R;
import com.wonderful.lion.activity.DetailActivity;
import com.wonderful.lion.uitl.ConfigUtil;
import com.wonderful.lion.uitl.LogFile;
import com.wonderful.lion.uitl.LogUtil;
import com.wonderful.lion.uitl.Util;


/**
 * Created by Sun Ruichuan on 2015/8/29.
 */
public class FloatingService extends Service {
    private final int BLUE = 2;
    private final int GREEN = 0;
    private final int RED = 1;
    private final int WHITE = 4;
    private int statusBarHeight;
    private View view;
    private boolean viewAdded = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private TextView tv_memory, tv_traffic, tv_cpu, tv_battery;
    // private String logfolder = "/" + "TestData" + "/";
    private ImageView iv_search;
    private LinearLayout ll;
    private LogUtil logutil;
    private String LogPath = Environment.getExternalStorageDirectory()
            .toString() + "/" + "TestData" + "/";
    private Util util;
    private DisplayMetrics metric;
    private String LogName;
    private boolean isShow = true, isLog = true;
    private String battery;
    private int log_battery;
    private SetData setdata;
    private BroadcastReceiver MyBatteryReceiver;
    private IntentFilter batteryFilter;
    private ConfigUtil configutil;
    private Handler handler = new Handler();
    private String packagename;
    private long logTime;
    private int logNum = 0;
    private long time;
    private Intent intent;
    private int changeColor_delay = 150;
    private long action_down_time;
    private long action_up_time;

    private long traffic_wifi = 0;
    private long traffic_3g = 0;
    /*
     * 闪烁提醒
     */
    private Handler h = new Handler() {
        public void handleMessage(android.os.Message msg) {

            int i = msg.what;
            int j = i % 3;

            if (i > 20)
                j = 4;

            switch (j) {
                case RED:
                    changeColor(R.color.red);
                    i++;
                    h.sendEmptyMessageDelayed(i, changeColor_delay);
                    break;
                case BLUE:
                    changeColor(R.color.blue);
                    i++;
                    h.sendEmptyMessageDelayed(i, changeColor_delay);
                    break;
                case GREEN:
                    changeColor(R.color.green);
                    i++;
                    h.sendEmptyMessageDelayed(i, changeColor_delay);
                    break;
                case WHITE:
                    changeColor(R.color.white);
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private Runnable refreshData = new Runnable() {
        @Override
        public void run() {

            int result = setdata.startSetData();
            if (result == -5) {
                util.setFLAG(2);
                if (isShow) {
                    RefreshUI_specail(getResources().getString(
                            R.string.no_process));
                }
            } else if (result == 0) {
                util.setFLAG(1);
                if (isShow) {
                    RefreshUI();
                }
                if (isLog) {
                    long currentTime = System.currentTimeMillis();
                    LogName = packagename + "_"
                            + logTime + "_" + logNum + ".log";
                    LogFile logFile = new LogFile();
                    logFile.setLogTime(String.valueOf(logTime));
                    logFile.setUserId(configutil.getUID());
                    logFile.setIsUpload(0);
                    logFile.setLogName(LogName);
                    logFile.setLogAppName(util.getAPPname());
                    logFile.setLogPath(LogPath + "/" + LogName);
                }
            }

            util.setLOGnum(logNum + 1);
            logutil.SaveLog(
                    LogPath + "/",
                    LogName,
                    System.currentTimeMillis() + "|"
                            + util.getProcessCpuRatio() + "|"
                            + util.getPSS_MEM() + "|" + log_battery
                            + "|" + util.getTRAFFIC_WIFI() + "|"
                            + util.getTRAFFIC_3G() + "\n");


            DetailActivity detailActivity = util.getDetailActivity();
            if (detailActivity != null) {
                detailActivity.refreshUI();
            }

            handler.postDelayed(refreshData, configutil.getDELAY());
            System.gc();
        }
    };

    public FloatingService() {
        util = Util.getUtil();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @SuppressLint({"InflateParams", "RtlHardcoded", "ClickableViewAccessibility"})
    @Override
    public void onCreate() {
        super.onCreate();
        // 检查log和悬浮窗是否显示的选项
        isShow = util.isFLOW_FLAG();
        isLog = util.isLOG_FLAG();

        final int FLAG_ACTIVITY_NEW_TASK = Intent.FLAG_ACTIVITY_NEW_TASK;

        // 监测电量
        GetBatteryStatus();

        // 初始化工具类
        Init();

        if (isShow) {
            view = LayoutInflater.from(this).inflate(R.layout.floating, null);
            windowManager = (WindowManager) this
                    .getSystemService(WINDOW_SERVICE);
            metric = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metric);
            /*
             * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
			 * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
			 * PixelFormat.TRANSPARENT：悬浮窗透明
			 */
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
                    LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
            layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;

            InitView();

            view.setOnTouchListener(new OnTouchListener() {
                @SuppressLint("RtlHardcoded")
                float[] temp = new float[]{0f, 0f};

                public boolean onTouch(View v, MotionEvent event) {
                    layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                    int eventaction = event.getAction();
                    switch (eventaction) {
                        case MotionEvent.ACTION_DOWN:
                            action_down_time = System.currentTimeMillis();
                            temp[0] = event.getX();
                            temp[1] = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            refreshView((int) (event.getRawX() - temp[0]),
                                    (int) (event.getRawY() - temp[1]));
                            break;
                        case MotionEvent.ACTION_UP:
                            action_up_time = System.currentTimeMillis();
                            // 若按下时间与抬起时间超过3000毫秒，则按Touch处理，否则按Click处理
                            if ((action_up_time - action_down_time) >= 1000) {
                                // Click处理
                                if ((event.getRawX() - temp[0]) > (metric.widthPixels / 2)) {
                                    refreshView(metric.widthPixels,
                                            (int) (event.getRawY() - temp[1]));
                                } else {
                                    refreshView(0,
                                            (int) (event.getRawY() - temp[1]));
                                }
                                Intent intent = new Intent();
                                intent.putExtra("appname", util.getAPPname());
                                intent.putExtra("packagename", packagename);
                                intent.setClass(FloatingService.this,
                                        DetailActivity.class);
                                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;

                            } else {
                                if ((event.getRawX() - temp[0]) > (metric.widthPixels / 2)) {
                                    refreshView(metric.widthPixels,
                                            (int) (event.getRawY() - temp[1]));
                                } else {
                                    refreshView(0,
                                            (int) (event.getRawY() - temp[1]));
                                }
                                break;
                            }
                    }
                    return false;
                }
            });

        }
    }

    private void InitView() {
        tv_memory = (TextView) view.findViewById(R.id.tv_memory);
        tv_traffic = (TextView) view.findViewById(R.id.tv_traffic);
        tv_cpu = (TextView) view.findViewById(R.id.tv_cpu);
        tv_battery = (TextView) view.findViewById(R.id.tv_battery);
//        iv_search = (ImageView) view.findViewById(R.id.iv_search);
//        iv_search.setImageDrawable(getResources().getDrawable(
//                R.drawable.ic_float));
        ll = (LinearLayout) view.findViewById(R.id.ll_float2);
        // 闪烁变换颜色

        h.sendEmptyMessageDelayed(RED, changeColor_delay);

        // h.sendEmptyMessageDelayed(WHITE, changeColor_delay * 5 * 10);
    }

    private void Init() {
        setdata = new SetData(getApplicationContext());
        logutil = new LogUtil();

    }

    // 移动刷新view
    private void refreshView(int x, int y) {
        if (statusBarHeight == 0) {
            View rootView = view.getRootView();
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            statusBarHeight = r.top;
        }
        layoutParams.x = x;
        layoutParams.y = y - statusBarHeight;// STATUS_HEIGHT;
        refresh();
    }

    private void refresh() {
        if (viewAdded) {
            windowManager.updateViewLayout(view, layoutParams);
        } else {
            windowManager.addView(view, layoutParams);
            viewAdded = true;
        }
    }

    private void removeView() {
        if (viewAdded) {
            windowManager.removeView(view);
            viewAdded = false;
        }
    }

    private void RefreshUI() {
        // 判断是否充电中
        if (util.isCharging()) {
            battery = getResources().getString(R.string.charging);
            log_battery = 0;
        } else {
            battery = getResources().getString(R.string.voltage)
                    + util.getVoltage() + getResources().getString(R.string.mv);
            log_battery = util.getVoltage();
        }

        tv_cpu.setVisibility(View.VISIBLE);
        ll.setVisibility(View.VISIBLE);
        tv_memory.setText(getResources().getString(R.string.memory)
                + util.transSize(util.getPSS_MEM()) + "\r");

        traffic_wifi = Long.parseLong(util.getTRAFFIC_WIFI());
        traffic_3g = Long.parseLong(util.getTRAFFIC_3G());

        if (configutil.getFLOW_TYPE() == 1) {
            // 显示总流量数
            tv_traffic.setText(getResources().getText(R.string.wifi).toString()
                    + util.transSize(traffic_wifi) + "\r"
                    + getResources().getText(R.string.mobile).toString()
                    + util.transSize(traffic_3g));
        } else {
            tv_traffic.setText(getResources().getText(R.string.wifi).toString()
                    + util.transSpeed(traffic_wifi) + "\r"
                    + getResources().getText(R.string.mobile).toString()
                    + util.transSpeed(traffic_3g));
        }

        tv_cpu.setText(getResources().getString(R.string.cpu)
                + util.getProcessCpuRatio());
        tv_battery.setText(battery);

    }

    private void changeColor(int Rcolor) {
        tv_memory.setTextColor(getResources().getColor(Rcolor));
        tv_traffic.setTextColor(getResources().getColor(Rcolor));
        tv_cpu.setTextColor(getResources().getColor(Rcolor));
        tv_battery.setTextColor(getResources().getColor(Rcolor));
    }

    private void RefreshUI_specail(String spc) {
        tv_memory.setText(spc);
        tv_cpu.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        util.setServiceRunning(true);
        if (isShow) {
            refresh();
            if (TextUtils.isEmpty(util.getPACKAGENAME()))
                RefreshUI_specail(getResources().getString(R.string.no_process));
            else
                RefreshUI_specail(getResources().getString(
                        R.string.loading_data));
        }
        configutil = new ConfigUtil(this);

        packagename = util.getPACKAGENAME();

        if (TextUtils.isEmpty(packagename)) {
            packagename = configutil.getProcess();
        }

        logTime = configutil.getLogTime();
        time = logTime;

        handler.post(refreshData);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        // logToast();
        util.setServiceRunning(false);
        util.setFLAG(0);
        removeView();
        unregisterReceiver(MyBatteryReceiver);
        handler.removeCallbacks(refreshData);
        super.onDestroy();
    }

    // private void logToast() {
    // if (isLog && util.getFLAG() == 1) {
    // Toast.makeText(
    // getApplicationContext(),
    // getResources().getString(R.string.log_location)
    // + getResources().getString(R.string.sdcard)
    // + logfolder + uid + "/" + LogName,
    // Toast.LENGTH_LONG).show();
    // }
    // }

    // 获取电量
    private void GetBatteryStatus() {
        MyBatteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                int voltage = intent.getIntExtra("voltage", -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                        -1);

                util.setCharging(status == BatteryManager.BATTERY_PLUGGED_AC
                        || status == BatteryManager.BATTERY_PLUGGED_USB);

                util.setVoltage(voltage);
            }
        };
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(MyBatteryReceiver, batteryFilter);
    }
}
