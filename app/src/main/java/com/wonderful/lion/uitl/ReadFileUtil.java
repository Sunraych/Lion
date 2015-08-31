package com.wonderful.lion.uitl;

import android.content.Context;

import org.apache.http.util.EncodingUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class ReadFileUtil {

    private String res;

    public String Read(Context context, int R_id, String Encoding) {

        try {
            InputStream is = context.getResources().openRawResource(R_id);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            // 设置编码
            res = EncodingUtils.getString(buffer, Encoding);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
