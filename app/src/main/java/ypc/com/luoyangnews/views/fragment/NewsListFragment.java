package ypc.com.luoyangnews.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ypc.com.luoyangnews.R;
import ypc.com.luoyangnews.model.NewsInfo;
import ypc.com.luoyangnews.utils.BitmapUtilsFactory;
import ypc.com.luoyangnews.utils.CategoryUtils;
import ypc.com.luoyangnews.utils.HttpUtilsFactory;
import ypc.com.luoyangnews.views.NewsContentActivity;

/**
 * 新闻列表页
 */
public class NewsListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String ARG_CATEGORY = "category";
    private static final String TAG = "NewsListFragment";

    private String category;

    private PullToRefreshListView lvNewsList;
    private ArrayList<NewsInfo> newsInfos;
    NewsListAdapter newsAdapter;

    private Context appContext;
    private BitmapUtils bitmapUtils;


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

        lvNewsList.setOnItemClickListener(this);
        //在listview快速滑动时暂停加载图片数据
        lvNewsList.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true));
        lvNewsList.setMode(PullToRefreshBase.Mode.BOTH);
        lvNewsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                new RefreshNewsListTask().execute(category);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                HttpUtils http = HttpUtilsFactory.getInstance();
                http.send(HttpRequest.HttpMethod.GET, CategoryUtils.getAddress(category), new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        newsInfos.addAll(NewsInfo.parse(objectResponseInfo.result));
                        newsAdapter.notifyDataSetChanged();
                        lvNewsList.onRefreshComplete();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        });
        newsInfos = new ArrayList<>();
        newsAdapter = new NewsListAdapter(inflater);
        lvNewsList.setAdapter(newsAdapter);


        return v;
    }

    /**
     * list条目点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsInfo info = newsInfos.get(position);
        Intent intent = new Intent(getActivity(), NewsContentActivity.class);
        intent.putExtra(NewsContentActivity.NEWSURL, info.getUrl());
        intent.putExtra(NewsContentActivity.NEWSTITLE, info.getTitle());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        HttpUtils http = HttpUtilsFactory.getInstance();
        http.send(HttpRequest.HttpMethod.GET, CategoryUtils.getAddress(category), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                //数据加载成功后更新adapter
                newsInfos.addAll(NewsInfo.parse(objectResponseInfo.result));
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(getActivity(), getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        });
    }




    /**
     * Listview Item缓存对象
     */
    public final class NewsListViewHolder {
        public ImageView image;
        public TextView title;
        public TextView desc;
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
                holder.desc = (TextView) convertView.findViewById(R.id.tv_newslist_desc);
                convertView.setTag(holder);
            } else {
                holder = (NewsListViewHolder) convertView.getTag();
            }
            NewsInfo info = newsInfos.get(position);
            holder.title.setText(info.getTitle());
            holder.desc.setText(info.getDesc());
            bitmapUtils.display(holder.image, info.getImageUrl());
            return convertView;
        }
    }


    class RefreshNewsListTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //延迟2S加载数据，优化体验
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HttpUtils http = HttpUtilsFactory.getInstance();
            try {
                ResponseStream responseStream = http.sendSync(HttpRequest.HttpMethod.GET, CategoryUtils.getAddress(category));
                return responseStream.readString();
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            List<NewsInfo> newData = NewsInfo.parse(response);
            for (NewsInfo n : newData) {
                n.setTitle("new " + n.getTitle());
            }
            newsInfos.clear();
            newsInfos.addAll(newData);
            newsAdapter.notifyDataSetChanged();
            lvNewsList.onRefreshComplete();
        }
    }
}
