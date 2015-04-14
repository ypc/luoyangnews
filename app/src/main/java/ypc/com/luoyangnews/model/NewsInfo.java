package ypc.com.luoyangnews.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ypc.com.luoyangnews.utils.MD5Utils;

/**
 * Created by ypc on 2015/4/13.
 */
public class NewsInfo {
    private String title;       //标题
    private String url;         //新闻的URL地址
    private String urlMd5;      //URL地址的MD5值，作为该新闻的唯一标示
    private String imageUrl;    //新闻列表上缩略图的URL地址
    private String desc;        //新闻列表上的详情简介
    private String content;     //新闻详细内容
    private Date pubDate;       //新闻发布时间

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
            String dateStr = node.select("font[color=#999999]").first().text();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                info.setPubDate(sdf.parse(dateStr));
            } catch (ParseException e) {
                info.setPubDate(null);
            }
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

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }
}
