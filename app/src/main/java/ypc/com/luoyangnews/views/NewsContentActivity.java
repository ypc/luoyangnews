package ypc.com.luoyangnews.views;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ypc.com.luoyangnews.R;

public class NewsContentActivity extends ActionBarActivity {

    public static String NEWSURL = "news_url";

    private Toolbar toolbar;
    private WebView wvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        String url = getIntent().getStringExtra(NEWSURL);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        wvContent = (WebView) findViewById(R.id.wv_content);

        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                Document doc = Jsoup.parse(objectResponseInfo.result);
                Element body = doc.select("#textbody").first();
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
