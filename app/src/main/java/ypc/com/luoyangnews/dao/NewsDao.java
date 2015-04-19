package ypc.com.luoyangnews.dao;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

import ypc.com.luoyangnews.model.NewsInfo;

/**
 * Created by ypc on 2015/4/15.
 */
public class NewsDao {

    private DbUtils db;

    private static int pageSize = 20;
    private static String nextPageUrl;

    public NewsDao(Context context) {
        db = DbUtils.create(context, "newsCache.db");
    }

    /**
     * 分页获取缓存中的新闻数据
     * @param pageNum
     * @return
     */
    public List<NewsInfo> findByPage(int pageNum, String category) {
        try {
            return db.findAll(
                    Selector.from(NewsInfo.class)
                            .where("category", "=", category)
                            .orderBy("pubDate", true)
                            .offset((pageNum - 1) * pageSize)
                            .limit(pageSize)
            );
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据ID获取指定的新闻数据
     * @param id
     * @return
     */
    public NewsInfo findById(int id) {
        try {
            return db.findById(NewsInfo.class, id);
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 保存新加载的数据到缓存中，在保存过程中会自动跳过已经存在的数据
     * @param infos
     */
    public void saveToDb(List<NewsInfo> infos) {
        try {
            for (NewsInfo info : infos) {
                if (!newsExists(info)) {
                    db.save(info);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新对象到数据库
     * @param info
     */
    public void update(NewsInfo info) {
        try {
            db.update(info, "content");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过URL Md5值判断该新闻是否已经存在于缓存数据库中
     * @param info
     * @return
     */
    public boolean newsExists(NewsInfo info) {
        try {
            NewsInfo existsInfo = db.findFirst(
                    Selector.from(NewsInfo.class)
                            .where("urlMd5", "=", info.getUrlMd5())
            );
            if (existsInfo != null) {
                return true;
            }
            return false;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清空表中的缓存数据
     */
    public void cleanCache() {
        try {
            db.deleteAll(NewsInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
