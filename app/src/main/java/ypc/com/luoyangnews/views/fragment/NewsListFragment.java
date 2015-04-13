package ypc.com.luoyangnews.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import ypc.com.luoyangnews.R;
import ypc.com.luoyangnews.model.NewsInfo;
import ypc.com.luoyangnews.utils.CategoryUtils;

/**
 * 新闻列表页
 */
public class NewsListFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";

    private String category;

    private ListView lvNewsList;
    private List<NewsInfo> newsInfos;
    NewsListAdapter newsAdapter;

    private Context appContext;


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
        appContext = getActivity().getApplicationContext();

        lvNewsList = (ListView) v.findViewById(R.id.lv_newslist);
        newsInfos = new ArrayList<>();
        newsAdapter = new NewsListAdapter(inflater);
        lvNewsList.setAdapter(newsAdapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.GET, CategoryUtils.getAddress(category), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                newsInfos.addAll(NewsInfo.parse(objectResponseInfo.result));
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
            return convertView;
        }
    }
}
