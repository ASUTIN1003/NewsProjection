package com.example.newssqlitefull.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newssqlitefull.MyApplication;
import com.example.newssqlitefull.R;
import com.example.newssqlitefull.adapter.NewsAdapter;
import com.example.newssqlitefull.bean.News;
import com.example.newssqlitefull.bean.User;
import com.example.newssqlitefull.enums.UserTypeEnum;
import com.example.newssqlitefull.ui.activity.AddNewsActivity;
import com.example.newssqlitefull.ui.activity.LoginActivity;
import com.example.newssqlitefull.ui.activity.NewsDetailActivity;
import com.example.newssqlitefull.util.KeyBoardUtil;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.widget.ActionBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 新闻
 */

public class NewsFragment extends Fragment {
    MySqliteOpenHelper helper = null;
    private Activity myActivity;//上下文
    private TabLayout tabTitle;
    private RecyclerView rvNewsList;
    private NewsAdapter mNewsAdapter;
    private LinearLayout llEmpty;
    private EditText etQuery;//搜索内容
    private ImageView ivSearch;//搜索图标
    private FloatingActionButton btnAdd;
    private String[] state = {"0", "1", "2", "3", "4", "5"};
    private String[] title = {"科技", "娱乐", "体育", "军事", "汽车", "健康"};
    private String typeId = "0";
    private List<News> mNews;
    private Integer userType;
    private Integer userId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        helper = new MySqliteOpenHelper(myActivity);
        tabTitle = (TabLayout) view.findViewById(R.id.tab_title);
        rvNewsList = (RecyclerView) view.findViewById(R.id.rv_news_list);
        llEmpty = view.findViewById(R.id.ll_empty);
        etQuery = view.findViewById(R.id.et_query);
        ivSearch = view.findViewById(R.id.iv_search);
        btnAdd = (FloatingActionButton) view.findViewById(R.id.btn_add);

        //获取控件
        initView();
        //软键盘搜索
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();//加载数据
            }
        });
        //点击软键盘中的搜索
        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyBoardUtil.hideKeyboard(v);//隐藏软键盘
                    loadData();//加载数据
                    return true;
                }
                return false;
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myActivity, AddNewsActivity.class);
                startActivityForResult(intent, 100);
            }
        });
        return view;
    }

    /**
     * 初始化页面
     */
    private void initView() {
        userType = (Integer) SPUtils.get(myActivity, SPUtils.USER_TYPE, 0);
        userId = (Integer) SPUtils.get(myActivity, SPUtils.USER_ID, 0);
        btnAdd.setVisibility(userType.intValue() == UserTypeEnum.Admin.getCode() ? View.VISIBLE : View.GONE);
        tabTitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        //为TabLayout添加tab名称
        for (int i = 0; i < title.length; i++) {
            tabTitle.addTab(tabTitle.newTab().setText(title[i]));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(myActivity);
        //=1.2、设置为垂直排列，用setOrientation方法设置(默认为垂直布局)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //=1.3、设置recyclerView的布局管理器
        rvNewsList.setLayoutManager(layoutManager);
        //==2、实例化适配器
        //=2.1、初始化适配器
        mNewsAdapter = new NewsAdapter();
        //=2.3、设置recyclerView的适配器
        rvNewsList.setAdapter(mNewsAdapter);
        loadData();
        mNewsAdapter.setItemListener(new NewsAdapter.ItemListener() {
            @Override
            public void ItemClick(News news) {
                if (userId == 0) {//未登录,跳转到登录页面
                    MyApplication.Instance.getMainActivity().finish();
                    startActivity(new Intent(myActivity, LoginActivity.class));
                } else {//已经登录
                    Intent intent;
                    if (userType.intValue() == UserTypeEnum.Admin.getCode()) {
                        intent = new Intent(myActivity, AddNewsActivity.class);
                    } else {
                        intent = new Intent(myActivity, NewsDetailActivity.class);
                    }
                    intent.putExtra("news", news);
                    startActivityForResult(intent, 100);
                }
            }

            @Override
            public void Delete(Integer id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(myActivity);
                dialog.setMessage("确认要删除该新闻吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        String sql = "delete from news where id=?";
                        db.execSQL(sql,new String[]{String.valueOf(id)});
                        db.close();
                        Toast.makeText(myActivity, "删除成功", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                });
                dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        tabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                typeId = state[tab.getPosition()];
                loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadData() {
        String contentStr = etQuery.getText().toString();//获取搜索内容
        mNews = new ArrayList<>();
        News news = null;
        String sql = "select * from news where typeId ="+typeId;
        if (!"".equals(contentStr)){
            sql+=" and title like ?";
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,!"".equals(contentStr)?new String[]{"%"+contentStr+"%"}:null);
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
        Collections.reverse(mNews);
        if (mNews != null && mNews.size() > 0) {
            rvNewsList.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
            mNewsAdapter.addItem(mNews);
        } else {
            rvNewsList.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadData();//加载数据
        }
    }
}
