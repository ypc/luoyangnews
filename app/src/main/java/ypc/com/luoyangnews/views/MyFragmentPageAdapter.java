package ypc.com.luoyangnews.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ypc.com.luoyangnews.views.fragment.NewsListFragment;

/**
 * Created by ypc on 2015/4/10.
 */
public class MyFragmentPageAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public MyFragmentPageAdapter(FragmentManager fm, String[] titles2) {
        super(fm);
        titles = titles2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return NewsListFragment.newInstance(titles[position]);
            case 1:
                return NewsListFragment.newInstance(titles[position]);
            case 2:
                return NewsListFragment.newInstance(titles[position]);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
