package ypc.com.luoyangnews.views;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import org.jsoup.select.Elements;

import ypc.com.luoyangnews.R;

public class NewsContentActivity extends ActionBarActivity {

    public static String NEWSURL = "news_url";

    private WebView wvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.activity_news_content);
        setContentView(helper.createView(this));
        helper.initActionBar(this);

        String url = getIntent().getStringExtra(NEWSURL);

        wvContent = (WebView) findViewById(R.id.wv_content);

        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                Document doc = Jsoup.parse(objectResponseInfo.result);
                Element body = doc.select("#textbody").first();
                Elements imgs = body.getElementsByTag("img");
                //设置图片宽度为100%，webView就不会出现横向滚动条
                for (Element img : imgs) {
                    img.attr("width", "100%");
                }
                //fadingActionBar中的webView组件存在bug，会引起webview底部的一部分内容无法滚动到
                //在这里强制加入一些空行将webView布局撑高，变相解决bug问题
                for (int i = 0; i < 15; i++) {
                    body.append("<br />");
                }
                wvContent.loadDataWithBaseURL("", body.html(), "text/html", "GB2312", "");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(NewsContentActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
