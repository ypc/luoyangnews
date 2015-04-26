package ypc.com.luoyangnews.views;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.lidroid.xutils.BitmapUtils;

import ypc.com.luoyangnews.R;
import ypc.com.luoyangnews.dao.NewsDao;
import ypc.com.luoyangnews.utils.BitmapUtilsFactory;

public class SettingsActivity extends PreferenceActivity {

    /**
     * 移动网络下是否加载数据的开关
     */
    public static final String SW_LOAD_IMAGE = "sw_loadImage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.setting_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPreferencesFromResource(R.xml.perference);

        Preference p = findPreference("cleanCache");
        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new CleanCacheTask().execute();
                return false;
            }
        });
    }

    /**
     * 清理缓存的异步线程
     */
    class CleanCacheTask extends AsyncTask<String, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("清理中...");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            BitmapUtils bitmapUtils = BitmapUtilsFactory.getInstance(SettingsActivity.this);
            NewsDao newsDao = new NewsDao(getApplicationContext());
            newsDao.cleanCache();
            bitmapUtils.clearCache();
            //清理实质上是在一个异步线程中，这里休眠3秒，优化用户交互效果
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.cancel();
            }
        }
    }
}
