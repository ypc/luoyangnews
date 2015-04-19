package ypc.com.luoyangnews.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ypc.com.luoyangnews.R;
import ypc.com.luoyangnews.dao.NewsDao;
import ypc.com.luoyangnews.model.NewsInfo;
import ypc.com.luoyangnews.utils.BitmapUtilsFactory;
import ypc.com.luoyangnews.utils.CategoryUtils;
import ypc.com.luoyangnews.utils.HttpUtilsFactory;
import ypc.com.luoyangnews.views.MainActivity;
import ypc.com.luoyangnews.views.NewsContentActivity;

/**
 * 新闻列表页
 */
public class NewsListFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    public static final String TAG = "NewsListFragment";

    private String category;
    private String nextPageUrl;
    private int currentPageNum;

    private PullToRefreshListView lvNewsList;
    private ArrayList<NewsInfo> newsInfos;
    NewsListAdapter newsAdapter;

    private Context appContext;
    private BitmapUtils bitmapUtils;
    private NewsDao newsDao;

    public static NewsListFragment newInstance(String category) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_newslist, container, false);
        lvNewsList = (PullToRefreshListView) v.findViewById(R.id.lv_newslist);
        appContext = getActivity().getApplicationContext();
        bitmapUtils = BitmapUtilsFactory.getInstance(appContext);
        newsDao = new NewsDao(getActivity());

        lvNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsInfo info = (NewsInfo) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), NewsContentActivity.class);
                intent.putExtra(NewsContentActivity.NEWSID, info.getId());
                startActivity(intent);
            }
        });
        //在listview快速滑动时暂停加载图片数据
        lvNewsList.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true));
        lvNewsList.setMode(PullToRefreshBase.Mode.BOTH);
        lvNewsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                new LoadnewsDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                new LoadnewsDataTask().execute(nextPageUrl, (++currentPageNum) + "");
            }
        });
        //listview滚动事件，设置自动隐藏toolbar
        lvNewsList.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int oldTop;
            private int oldFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                MainActivity mainActivity = (MainActivity) getActivity();
                View view = absListView.getChildAt(0);
                int top = (view == null) ? 0 : view.getTop();

                if (firstVisibleItem == oldFirstVisibleItem) {
                    //偏移量超过10像素才算滚动
                    if (Math.abs(top - oldTop) > 10) {
                        if (top > oldTop) {
                            //down
                            if (mainActivity.toolbarIsHidden()) {
                                mainActivity.showToolbar();
                            }
                        } else if (top < oldTop) {
                            //up
                            if (mainActivity.toolbarIsShown()) {
                                mainActivity.hideToolbar();
                            }
                        }
                    }
                } else {
                    if (firstVisibleItem < oldFirstVisibleItem) {
                        if (mainActivity.toolbarIsHidden()) {
                            mainActivity.showToolbar();
                        }
                    } else {
                        if (mainActivity.toolbarIsShown()) {
                            mainActivity.hideToolbar();
                        }
                    }
                }

                oldTop = top;
                oldFirstVisibleItem = firstVisibleItem;
            }
        });
        newsInfos = new ArrayList<>();
        newsAdapter = new NewsListAdapter(inflater);
        lvNewsList.setAdapter(newsAdapter);
        initData();

        return v;
    }

    private void initData() {
        //初始化加载数据
        new LoadnewsDataTask().execute();
        currentPageNum = 1;
    }


    /**
     * Listview Item缓存对象
     */
    public final class NewsListViewHolder {
        public ImageView image;
        public TextView title;
        public TextView putDate;
    }

    /**
     * 自定义NewsList的适配器
     */
    class NewsListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        NewsListAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;
        }

        @Override
        public int getCount() {
            return newsInfos.size();
        }

        @Override
        public NewsInfo getItem(int position) {
            return newsInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NewsListViewHolder holder;
            if (convertView == null) {
                holder = new NewsListViewHolder();
                convertView = mInflater.inflate(R.layout.newslist_item, null);
                holder.image = (ImageView) convertView.findViewById(R.id.iv_newslist_image);
                holder.title = (TextView) convertView.findViewById(R.id.tv_newslist_title);
                holder.putDate = (TextView) convertView.findViewById(R.id.tv_newslist_pubdate);
                convertView.setTag(holder);
            } else {
                holder = (NewsListViewHolder) convertView.getTag();
            }
            NewsInfo info = newsInfos.get(position);
            holder.title.setText(info.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            holder.putDate.setText(sdf.format(info.getPubDate()));
            //是否允许从网络加载图片
            if (((MainActivity)getActivity()).allowLoadImage(getActivity())) {
                //为ImageView设置一个TAG标志，在不需要重新display的时候跳过，解决listview闪烁的问题
                if (!info.getImageUrl().equals(holder.image.getTag())) {

                    bitmapUtils.display(holder.image, info.getImageUrl());
                    holder.image.setTag(info.getImageUrl());
                }
            }
            return convertView;
        }
    }


    /**
     * 执行从互联网加载数据的异步线程
     */
    class LoadnewsDataTask extends AsyncTask<String, Integer, List<NewsInfo>> {

        private String urlAddress;
        private int pageNum;
        private boolean loadMore = false;

        @Override
        protected List<NewsInfo> doInBackground(String... params) {
            if (params.length > 0) {
                urlAddress = params[0];
                pageNum = Integer.parseInt(params[1]);
                loadMore = true;
            } else {
                //如果没有传递参数进来，URL地址就是默认的首页地址
                urlAddress = CategoryUtils.getAddress(category);
                pageNum = 1;
            }
            HttpUtils http = HttpUtilsFactory.getInstance();
            try {
                ResponseStream responseStream = http.sendSync(HttpRequest.HttpMethod.GET, urlAddress);
                String resultStr =  responseStream.readString();
                StringBuilder sb = new StringBuilder();
                List<NewsInfo> newData = NewsInfo.parse(resultStr, sb);
                nextPageUrl = sb.toString();
                for (NewsInfo i : newData) {
                    i.setCategory(category);
                }
                newsDao.saveToDb(newData);
                return newsDao.findByPage(pageNum, category);
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<NewsInfo> infos) {

            //如果是下拉刷新，清空旧数据
            if (!loadMore) {
                newsInfos.clear();
            }
            if (infos != null && infos.size() > 0) {
                newsInfos.addAll(infos);
                newsAdapter.notifyDataSetChanged();
                lvNewsList.onRefreshComplete();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
