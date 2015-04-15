package ypc.com.luoyangnews.views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import ypc.com.luoyangnews.R;
import ypc.com.luoyangnews.model.NewsInfo;

public class NewsContentActivity extends ActionBarActivity {

    public static String NEWSINFO = "news_info";

    private WebView wvContent;
    private NewsInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        info = (NewsInfo) getIntent().getExtras().getSerializable(NEWSINFO);


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
        http.send(HttpRequest.HttpMethod.GET, info.getUrl(), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                info.setContent(objectResponseInfo.result);
                info.filterContent();
                wvContent.loadDataWithBaseURL("", info.getContent(), "text/html", "GB2312", "");
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
