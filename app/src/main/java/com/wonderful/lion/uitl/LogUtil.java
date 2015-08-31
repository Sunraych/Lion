package com.wonderful.lion.uitl;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class LogUtil {
    public final static String FONT = "UTF-8";
    public String path;
    public String newlog;
    public String isMount;

    public int SaveLog(String path, String fname, String log) {
        isMount = Environment.getExternalStorageState();
        if (isMount.equals("mounted")) {
            File file = new File(path + fname);
            File dir = new File(path);
            ;
            try {
                if (!file.exists()) {
                    dir.mkdirs();
                    FileOutputStream fos = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, FONT);
                    osw.write(log);
                    osw.flush();
                    osw.close();
                    return 0;
                } else {
                    FileWriter fileWriter = new FileWriter(file, true);
                    fileWriter.write(log);
                    fileWriter.flush();
                    fileWriter.close();
                    return 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 2;
            }
        } else {
            return -1;
        }
    }
}
