package ypc.com.luoyangnews.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import ypc.com.luoyangnews.R;
import ypc.com.luoyangnews.views.fragment.NewsListFragment;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ViewPager pager;
    private SlidingTabLayout slidingTabLayout;
    private ActionBarDrawerToggle drawerToggle;

    private Toolbar toolbar;
    private ViewGroup content;
    private boolean toolbarAnimRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        content = (ViewGroup) findViewById(R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new MyFragmentPageAdapter(getSupportFragmentManager()));

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab);
        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);

        String[] values = new String[]{
                "发送反馈", "关于"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    public boolean toolbarIsShown(){
        return toolbar.getTranslationY() == 0;
    }

    public boolean toolbarIsHidden(){
        return toolbar.getTranslationY() == -toolbar.getHeight();
    }

    public void showToolbar(){
        moveToolbar(0);
    }


    public void hideToolbar(){
        moveToolbar(-toolbar.getHeight());
    }


    /**
     * 将toolbar移动到某个位置
     * @param toTranslationY 移动到的Y轴位置
     */
    private void moveToolbar(float toTranslationY){
        //防止重复加载动画
        if (toolbarAnimRunning) {
            return;
        }
        if(toolbar.getTranslationY() == toTranslationY){
            return;
        }
        //利用动画过渡移动的过程
        final ValueAnimator animator = ValueAnimator.ofFloat(toolbar.getTranslationY(),toTranslationY).
                setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float translationY = (Float) animator.getAnimatedValue();
                toolbar.setTranslationY(translationY);
                content.setTranslationY(translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) content.getLayoutParams();
                lp.height = (int) (getScreenHeight() - translationY
                        - lp.topMargin);
                content.requestLayout();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                toolbarAnimRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toolbarAnimRunning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    /**
     * 获取屏幕高度
     * @return
     */
    private int getScreenHeight(){
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
