package ypc.com.luoyangnews.utils;

import android.content.Context;
import android.view.animation.AlphaAnimation;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCacheListener;

/**
 * Created by ypc on 2015/4/14.
 */
public class BitmapUtilsFactory {

    private static BitmapUtils instance;

    public static BitmapUtils getInstance(Context context) {
        if (instance == null) {
            instance = new BitmapUtils(context);
            //图像的加载动画
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(500);
            instance.configDefaultImageLoadAnimation(animation);

        }
        return instance;
    }
}
