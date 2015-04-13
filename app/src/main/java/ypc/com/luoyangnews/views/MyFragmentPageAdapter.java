package ypc.com.luoyangnews.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ypc.com.luoyangnews.utils.CategoryUtils;
import ypc.com.luoyangnews.views.fragment.NewsListFragment;

/**
 * Created by ypc on 2015/4/10.
 */
public class MyFragmentPageAdapter extends FragmentPagerAdapter {

    private List<String> titles;

    public MyFragmentPageAdapter(FragmentManager fm) {
        super(fm);
        titles = CategoryUtils.getCodes();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return NewsListFragment.newInstance(CategoryUtils.LYXW);
            case 1:
                return NewsListFragment.newInstance(CategoryUtils.HLSP);
            case 2:
                return NewsListFragment.newInstance(CategoryUtils.ZYJJ);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CategoryUtils.getZhName(titles.get(position));
    }

    @Override
    public int getCount() {
        return titles.size();
    }
}
