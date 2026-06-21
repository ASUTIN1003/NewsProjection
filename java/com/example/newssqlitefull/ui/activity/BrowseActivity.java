package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.newssqlitefull.R;
import com.example.newssqlitefull.adapter.BrowseAdapter;
import com.example.newssqlitefull.bean.Browse;
import com.example.newssqlitefull.bean.News;
import com.example.newssqlitefull.bean.User;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.widget.ActionBar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 浏览记录
 */
public class BrowseActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private Activity myActivity;
    private ActionBar mTitleBar;//标题栏
    private LinearLayout llEmpty;
    private RecyclerView rvBrowseList;
    private BrowseAdapter mBrowseAdapter;
    private List<News> mNews;
    private Integer userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        myActivity = this;
        helper = new MySqliteOpenHelper(this);
        rvBrowseList = findViewById(R.id.rv_collect_list);
        llEmpty = findViewById(R.id.ll_empty);
        mTitleBar = (ActionBar) findViewById(R.id.myActionBar);
        mTitleBar.setData(myActivity, "浏览记录", R.drawable.ic_back, 0, 0, getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
            }
        });
        initView();
    }

    private void initView() {
        userId = (Integer) SPUtils.get(myActivity, SPUtils.USER_ID, 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myActivity);
        //=1.2、设置为垂直排列，用setOrientation方法设置(默认为垂直布局)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //=1.3、设置recyclerView的布局管理器
        rvBrowseList.setLayoutManager(layoutManager);
        //==2、实例化适配器
        //=2.1、初始化适配器
        mBrowseAdapter = new BrowseAdapter();
        //=2.3、设置recyclerView的适配器
        rvBrowseList.setAdapter(mBrowseAdapter);
        loadData();//加载数据
        mBrowseAdapter.setItemListener(new BrowseAdapter.ItemListener() {
            @Override
            public void ItemClick(News news) {
                Intent intent = new Intent(myActivity, NewsDetailActivity.class);
                intent.putExtra("news", news);
                intent.putExtra("isBrowse",true);
                startActivityForResult(intent, 100);
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadData() {
        mNews = new ArrayList<>();
        News news = null;
        String sql = "select n.* from browse b,news n,user u where b.newsId = n.id and b.userId = u.id and b.userId = "+userId;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                Integer dbId = cursor.getInt(0);
                Integer typeId = cursor.getInt(1);
                String title = cursor.getString(2);
                String img = cursor.getString(3);
                String content = cursor.getString(4);
                String issuer = cursor.getString(5);
                String date = cursor.getString(6);
                news = new News(dbId,typeId, title, img, content, issuer, date);
                mNews.add(news);
            }
        }
        db.close();
        Collections.reverse(mNews);
        if (mNews != null && mNews.size() > 0) {
            rvBrowseList.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
            mBrowseAdapter.addItem(mNews);
        } else {
            rvBrowseList.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
