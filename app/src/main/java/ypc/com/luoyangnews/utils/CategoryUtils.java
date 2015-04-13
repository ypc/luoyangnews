package ypc.com.luoyangnews.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ypc on 2015/4/13.
 */
public class CategoryUtils {
    public static String LYXW = "lyxw";
    public static String HLSP = "hlsp";
    public static String ZYJJ = "zyjj";


    public static List<NewsCategory> categories = new ArrayList<>();

    static {
        categories.add(new NewsCategory(LYXW, "洛阳新闻", "http://news.lyd.com.cn/luoyang/luoyangxinwen/index.shtml"));
        categories.add(new NewsCategory(HLSP, "河洛时评", "http://news.lyd.com.cn/luoyang/shiping/"));
        categories.add(new NewsCategory(ZYJJ, "中原聚焦", "http://news.lyd.com.cn/zhongyuanjujiao/index.shtml"));
    }


    /**
     * 获取所有分类代码列表
     *
     * @return
     */
    public static List<String> getCodes() {
        List<String> codes = new ArrayList<>();
        for (NewsCategory c : categories) {
            codes.add(c.code);
        }
        return codes;
    }


    /**
     * 根据分类代码获取分类中文名
     *
     * @param category
     * @return
     */
    public static String getAddress(String category) {
        for (NewsCategory c : categories) {
            if (c.code.equals(category)) {
                return c.url;
            }
        }
        return null;
    }

    /**
     * 根据分类代码获取分类数据源地址
     *
     * @param category
     * @return
     */
    public static String getZhName(String category) {
        for (NewsCategory c : categories) {
            if (c.code.equals(category)) {
                return c.zhName;
            }
        }
        return null;
    }


    static class NewsCategory {
        private String code;
        private String zhName;
        private String url;

        NewsCategory(String code, String zhName, String url) {
            this.code = code;
            this.zhName = zhName;
            this.url = url;
        }
    }
}
