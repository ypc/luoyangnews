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

import ypc.com.luoyangnews.R;

public class SettingsActivity extends PreferenceActivity {

    public static final String CBX_LOADIMAGE = "sw_loadImage";


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

        Preference p = findPreference("clearCache");
        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ClearCacheTask().execute();
                return false;
            }
        });
    }

    /**
     * 清理缓存的异步线程
     */
    class ClearCacheTask extends AsyncTask<String, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("清理中...");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
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
