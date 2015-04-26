package ypc.com.luoyangnews.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;

import ypc.com.luoyangnews.R;

/**
 * Created by ypc on 2015/4/26.
 */
public class SplashActivity extends ActionBarActivity {

    private Handler welcomeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        welcomeHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onBackPressed() {

    }
}
