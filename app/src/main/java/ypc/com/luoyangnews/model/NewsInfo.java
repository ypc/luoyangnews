package ypc.com.luoyangnews.model;

import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Unique;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ypc.com.luoyangnews.utils.MD5Utils;

/**
 * Created by ypc on 2015/4/13.
 */
@Table(name = "newsInfo")
public class NewsInfo implements Serializable {

    @Column(column = "_id")
    private int id;

    @Column(column = "title")
    private String title;       //标题

    @Column(column = "url")
    private String url;         //新闻的URL地址

    @Unique
    @Column(column = "urlMd5")
    private String urlMd5;      //URL地址的MD5值，作为该新闻的唯一标示

    @Column(column = "imageUrl")
    private String imageUrl;    //新闻列表上缩略图的URL地址

    @Column(column = "desc")
    private String desc;        //新闻列表上的详情简介

    @Column(column = "content")
    private String content;     //新闻详细内容

    @Column(column = "pubDate")
    private Date pubDate;       //新闻发布时间

    @Column(column = "category")
    private String category;    //新闻的类别


    /**
     * 从原始的网页内容解析出可用的新闻列表数据
     * @param html
     * @return
     */
    public static List<NewsInfo> parse(String html, StringBuilder sb) {
        List<NewsInfo> newsInfos = new ArrayList<NewsInfo>();
        Document doc = Jsoup.parse(html);
        Elements nodes = doc.select(".dhsbk");
        for (Element node : nodes) {
            NewsInfo info = new NewsInfo();
            Element titleNode = node.select("span.style3 a").first();
            info.title = titleNode.text();
            info.url = titleNode.attr("href");
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
        //获取下一页连接地址，通过StringBuilder对象回传
        Elements linkNodes = doc.getElementsByTag("a");
        for (Element node : linkNodes) {
            if (node.text().equals("下一页")) {
                sb.append(node.attr("href"));
                break;
            }
        }

        return newsInfos;
    }


    public void filterContent() {
        Document doc = Jsoup.parse(content);
        Element body = doc.select("#textbody").first();
        //加入标题节点
//        body.child(0).before("<h2 align=\"center\">" + title + "</h2> <hr>");
        //设置图片宽度为100%，webView就不会出现横向滚动条
        Elements imgs = body.getElementsByTag("img");
        for (Element img : imgs) {
            img.attr("width", "100%");
        }
        //fadingActionBar中的webView组件存在bug，会引起webview底部的一部分内容无法滚动到
        //在这里强制加入一些空行将webView布局撑高，变相解决bug问题
        for (int i = 0; i < 10; i++) {
            body.append("<br />");
        }
        //删除最后的洛阳新闻网的链接
        Element bottomElem = body.select("span.style7").first();
        if (bottomElem != null) {
            bottomElem.remove();
        }
        content = body.outerHtml();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
