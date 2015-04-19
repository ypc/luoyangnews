package ypc.com.luoyangnews.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by ypc on 2015/4/20.
 */
public class BaseActivity extends ActionBarActivity {


    /**
     * 是否是连接WIFI
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }

        return false ;
    }


    /**
     * 是否允许从网络加载图片
     * @param context
     * @return
     */
    public static boolean allowLoadImage(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean loadImage = sp.getBoolean(SettingsActivity.SW_LOAD_IMAGE, true);
        Boolean isWifi = isWifiConnected(context);
        return isWifi || loadImage;
    }
}
