package com.wonderful.lion.uitl;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/8/31.
 */
public class ClearCache {

    private String dirpath;
    private String Mount;

    public boolean clearCache(String path) {
        Mount = Environment.getExternalStorageDirectory().toString();
        dirpath = Mount + path;

        boolean success = true;
        File file = new File(dirpath);
        if (file.exists()) {
            File[] list = file.listFiles();
            if (list != null) {
                int len = list.length;
                for (int i = 0; i < len; ++i) {
                    if (list[i].isDirectory()) {
                        clearCache(list[i].getPath());
                    } else {
                        success = list[i].delete();
                    }
                }
            }
        } else {
            success = false;
        }
        if (success) {
            file.delete();
        }
        return success;
    }
}
