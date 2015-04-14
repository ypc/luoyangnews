package ypc.com.luoyangnews.utils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;

/**
 * Created by ypc on 2015/4/14.
 */
public class BitmapUtilsFactory {

    private static BitmapUtils instance;

    public static BitmapUtils getInstance(Context context) {
        if (instance == null) {
            instance = new BitmapUtils(context);
        }
        return instance;
    }
}
