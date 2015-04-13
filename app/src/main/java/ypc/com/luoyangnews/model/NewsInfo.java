package ypc.com.luoyangnews.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ypc.com.luoyangnews.utils.MD5Utils;

/**
 * Created by ypc on 2015/4/13.
 */
public class NewsInfo {
    private String title;
    private String url;
    private String urlMd5;
    private String imageUrl;
    private String desc;
    private String content;

    public static List<NewsInfo> parse(String html) {
        List<NewsInfo> newsInfos = new ArrayList<NewsInfo>();
        Document doc = Jsoup.parse(html);
        Elements nodes = doc.select(".dhsbk");
        for (Element node : nodes) {
            NewsInfo info = new NewsInfo();
            Element linkNode = node.select("span.style3 a").first();
            info.title = linkNode.text();
            info.url = linkNode.attr("href");
            info.urlMd5 = MD5Utils.md5(info.url);
            info.desc = node.select("span.style2").first().text();
            info.imageUrl = node.select("td img[width=120]").first().attr("src");
            newsInfos.add(info);
        }

        return newsInfos;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlMd5() {
        return urlMd5;
    }

    public void setUrlMd5(String urlMd5) {
        this.urlMd5 = urlMd5;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
