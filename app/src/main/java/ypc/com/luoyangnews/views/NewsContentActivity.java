package ypc.com.luoyangnews.views;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;


import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import ypc.com.luoyangnews.R;

public class NewsContentActivity extends ActionBarActivity {

    public static String NEWSURL = "news_url";
    public static String NEWSTITLE = "news_title";

    private WebView wvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String newsUrl = getIntent().getStringExtra(NEWSURL);
        final String newsTitle = getIntent().getStringExtra(NEWSTITLE);


        //使用FadingActionBar初始化界面
        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.activity_news_content);
        setContentView(helper.createView(this));
        helper.initActionBar(this);


        wvContent = (WebView) findViewById(R.id.wv_content);

        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.GET, newsUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                Document doc = Jsoup.parse(objectResponseInfo.result);
                Element body = doc.select("#textbody").first();
                //加入标题节点
                body.child(0).before("<h2 align=\"center\">" + newsTitle + "</h2> <hr>");
                //设置图片宽度为100%，webView就不会出现横向滚动条
                Elements imgs = body.getElementsByTag("img");
                for (Element img : imgs) {
                    img.attr("width", "100%");
                }
                //fadingActionBar中的webView组件存在bug，会引起webview底部的一部分内容无法滚动到
                //在这里强制加入一些空行将webView布局撑高，变相解决bug问题
                for (int i = 0; i < 15; i++) {
                    body.append("<br />");
                }
                //删除最后的洛阳新闻网的链接
                Element bottomElem = body.select("span.style7").first();
                bottomElem.remove();
                wvContent.loadDataWithBaseURL("", body.outerHtml(), "text/html", "GB2312", "");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(NewsContentActivity.this, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
