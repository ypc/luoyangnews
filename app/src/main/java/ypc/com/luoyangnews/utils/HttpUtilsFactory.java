package ypc.com.luoyangnews.utils;

import com.lidroid.xutils.HttpUtils;

/**
 * Created by ypc on 2015/4/14.
 */
public class HttpUtilsFactory {
    private static HttpUtils instance;

    public static HttpUtils getInstance() {
        if (instance == null) {
            instance = new HttpUtils();
            instance.configResponseTextCharset("GB2312");
        }
        return instance;
    }
}
