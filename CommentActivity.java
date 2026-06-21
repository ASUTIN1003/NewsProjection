package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newssqlitefull.R;
import com.example.newssqlitefull.adapter.CollectAdapter;
import com.example.newssqlitefull.adapter.CommentAdapter;
import com.example.newssqlitefull.bean.CommentVo;
import com.example.newssqlitefull.bean.News;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论
 */
public class CommentActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private Activity myActivity;
    private ActionBar mTitleBar;//标题栏
    private LinearLayout llEmpty;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private SQLiteDatabase db;
    private Integer userId;
    private int newId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        myActivity = this;
        helper = new MySqliteOpenHelper(this);
        db = helper.getWritableDatabase();
        newId = getIntent().getIntExtra("id", 0);
        recyclerView = findViewById(R.id.rv_list);
        llEmpty = findViewById(R.id.ll_empty);
        mTitleBar = (ActionBar) findViewById(R.id.myActionBar);
        mTitleBar.setData(myActivity, "评论管理  ", R.drawable.ic_back, 0, 0, getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
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
        recyclerView.setLayoutManager(layoutManager);
        //==2、实例化适配器
        //=2.1、初始化适配器
        commentAdapter = new CommentAdapter();
        //=2.3、设置recyclerView的适配器
        recyclerView.setAdapter(commentAdapter);
        getCommentList();//加载数据
        commentAdapter.setItemListener(new CommentAdapter.ItemListener() {

            @Override
            public void ItemClick(CommentVo comment) {

            }

            @Override
            public void Delete(Integer id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(myActivity);
                dialog.setMessage("确认要删除该评论吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        if (db.isOpen()) {
                            db.execSQL("delete from comment where id = "+id);
                        }
                        Toast.makeText(myActivity,"删除成功",Toast.LENGTH_LONG).show();
                        getCommentList();
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
    }


    /**
     * 获取评论列表
     */
    private void getCommentList() {
        String commentSql = "select c.*,u.nickName from comment c,user u where c.userId = u.id and c.newsId=" + newId;
        List<CommentVo> list = new ArrayList<>();
        Cursor cursor1 = db.rawQuery(commentSql, null);
        if (cursor1 != null && cursor1.getColumnCount() > 0) {
            while (cursor1.moveToNext()) {
                Integer dbId = cursor1.getInt(0);
                Integer dbNewsId = cursor1.getInt(1);
                Integer dbUserId = cursor1.getInt(2);
                String dbContent = cursor1.getString(3);
                String dbDate = cursor1.getString(4);
                String nickName = cursor1.getString(5);
                CommentVo comment = new CommentVo(dbId, dbNewsId, dbUserId, dbContent, dbDate, nickName);
                list.add(comment);
            }
        }
        if (list.size() > 0) {
            commentAdapter.addItem(list);
            recyclerView.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getCommentList();
    }
}
