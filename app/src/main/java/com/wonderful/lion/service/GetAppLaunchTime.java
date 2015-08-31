package com.wonderful.lion.service;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Intent;

import com.wonderful.lion.uitl.DebugLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class GetAppLaunchTime {

    private Method startActivityMethod = null;
    private IActivityManager activityManager;

    public long startActivityWithTime(Intent intent) {

        long thisTime = 0;
        int currentApiLevel = android.os.Build.VERSION.SDK_INT;

        getStartActivityMethod();

        if (startActivityMethod == null) {
            return 0;
        }

        if (currentApiLevel == 21) {
            thisTime = startActivityWithFieldsForApi21(intent);
        } else if (currentApiLevel == 20) {
            thisTime = startActivityWithFieldsForApi20(intent);
        } else if (currentApiLevel == 19) {
            thisTime = startActivityWithFieldsForApi19(intent);
        } else if (currentApiLevel == 18) {
            thisTime = startActivityWithFieldsForApi18(intent);
        } else if (currentApiLevel == 17) {
            thisTime = startActivityWithFieldsForApi17(intent);
        } else if (currentApiLevel == 16) {
            thisTime = startActivityWithFieldsForApi16(intent);
        } else if (currentApiLevel == 15) {
            thisTime = startActivityWithFieldsForApi15(intent);
        } else if (currentApiLevel == 14) {
            thisTime = startActivityWithFieldsForApi14(intent);
        } else if (currentApiLevel == 13) {
            thisTime = startActivityWithFieldsForApi13(intent);
        } else if (currentApiLevel == 12) {
            thisTime = startActivityWithFieldsForApi12(intent);
        } else if (currentApiLevel == 11) {
            thisTime = startActivityWithFieldsForApi11(intent);
        } else if (currentApiLevel == 10) {
            thisTime = startActivityWithFieldsForApi10(intent);
        } else if (currentApiLevel == 9) {
            thisTime = startActivityWithFieldsForApi9(intent);
        } else if (currentApiLevel == 8) {
            thisTime = startActivityWithFieldsForApi8(intent);
        }

        return thisTime;
    }

    private void getStartActivityMethod() {
        activityManager = ActivityManagerNative.getDefault();
        Method[] methods = activityManager.getClass().getDeclaredMethods();

        if (DebugLog.DEBUG)
            DebugLog.e(methods.toString());

        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();

            if (DebugLog.DEBUG)
                DebugLog.e(methodName);

            if (methodName.contains("startActivityAndWait")) {
                startActivityMethod = methods[i];
                startActivityMethod.setAccessible(true);
                Class<?>[] methods1 = startActivityMethod.getParameterTypes();

                if (DebugLog.DEBUG)
                    DebugLog.e(Arrays.toString(methods1));

                break;
            }
        }
    }

    // 5.0
    private long startActivityWithFieldsForApi21(Intent intent) {
        Object[] objects = new Object[]{null, null, intent, null, null, null,
                0, 0, null, null, 0};
        return startActivityForResult(objects);
    }

    // 5.0 preview
    private long startActivityWithFieldsForApi20(Intent intent) {
        Object[] objects = new Object[]{null, null, intent, null, null, null,
                0, 0, null, null, 0};
        return startActivityForResult(objects);
    }

    // 4.4
    private long startActivityWithFieldsForApi19(Intent intent) {
        Object[] objects = new Object[]{null, null, intent, null, null, null,
                0, 0, null, null, null, 0};
        return startActivityForResult(objects);
    }

    // 4.3
    private long startActivityWithFieldsForApi18(Intent intent) {
        Object[] objects = new Object[]{null, null, intent, null, null, null,
                0, 0, null, null, null, 0};
        return startActivityForResult(objects);
    }

    // 4.2.2
    private long startActivityWithFieldsForApi17(Intent intent) {
        Object[] objects = new Object[]{null, intent, null, null, null, 0, 0,
                null, null, null, 0};
        return startActivityForResult(objects);
    }

    // 4.1.2
    private long startActivityWithFieldsForApi16(Intent intent) {
        Object[] objects = new Object[]{null, intent, null, null, null, 0, 0,
                null, null, null};
        return startActivityForResult(objects);
    }

    // 4.0.3
    private long startActivityWithFieldsForApi15(Intent intent) {
        Object[] objects = new Object[]{null, intent, null, null, 0, null,
                null, 0, false, false, null, null, false};
        return startActivityForResult(objects);
    }

    // 4.0
    private long startActivityWithFieldsForApi14(Intent intent) {
        Object[] objects = new Object[]{null, intent, null, null, 0, null,
                null, 0, false, false, null, null, false};
        return startActivityForResult(objects);
    }

    // 3.2
    private long startActivityWithFieldsForApi13(Intent intent) {
        Object[] objects = new Object[]{};
        return startActivityForResult(objects);
    }

    // 3.1
    private long startActivityWithFieldsForApi12(Intent intent) {
        Object[] objects = new Object[]{};
        return startActivityForResult(objects);
    }

    // 3.0
    private long startActivityWithFieldsForApi11(Intent intent) {
        Object[] objects = new Object[]{};
        return startActivityForResult(objects);
    }

    // 2.3.3
    private long startActivityWithFieldsForApi10(Intent intent) {
        Object[] objects = new Object[]{};
        return startActivityForResult(objects);
    }

    // There is no Api level 9
    private long startActivityWithFieldsForApi9(Intent intent) {
        Object[] objects = new Object[]{null, intent, intent.getType(), null,
                0, null, null, 0, false, false};
        return startActivityForResult(objects);
    }

    // 2.2
    private long startActivityWithFieldsForApi8(Intent intent) {
        Object[] objects = new Object[]{null, intent, intent.getType(), null,
                0, null, null, 0, false, false};
        return startActivityForResult(objects);
    }

    private long startActivityForResult(Object[] objects) {
        try {
            Object object = startActivityMethod
                    .invoke(activityManager, objects);
            IActivityManager.WaitResult waitResult = (IActivityManager.WaitResult) object;
            return waitResult.thisTime;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
