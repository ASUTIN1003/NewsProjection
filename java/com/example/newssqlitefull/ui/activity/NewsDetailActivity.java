package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newssqlitefull.R;
import com.example.newssqlitefull.adapter.CommentAdapter;
import com.example.newssqlitefull.adapter.UserAdapter;
import com.example.newssqlitefull.bean.Browse;
import com.example.newssqlitefull.bean.Collect;
import com.example.newssqlitefull.bean.Comment;
import com.example.newssqlitefull.bean.CommentVo;
import com.example.newssqlitefull.bean.News;
import com.example.newssqlitefull.bean.User;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.widget.ActionBar;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 新闻明细信息
 */
public class NewsDetailActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private Activity mActivity;
    private ImageView ivImg;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvContent;
    private TextView tvIssuer;
    private EditText etContent;
    private ImageView ivLike;
    private ImageView ivLikeCheck;
    private ActionBar mActionBar;//标题栏
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer userId;
    private SQLiteDatabase db;
    private News news;
    private LinearLayout llEmpty;
    private RecyclerView rvList;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_news_detail);
        helper = new MySqliteOpenHelper(this);
        ivImg = findViewById(R.id.img);
        tvTitle = findViewById(R.id.title);
        tvDate = findViewById(R.id.date);
        tvContent = findViewById(R.id.content);
        tvIssuer = findViewById(R.id.issuer);
        etContent = findViewById(R.id.et_content);
        ivLike = findViewById(R.id.iv_like);
        ivLikeCheck = findViewById(R.id.iv_like_check);
        llEmpty = findViewById(R.id.ll_empty);
        rvList = findViewById(R.id.rv_list);
        mActionBar = findViewById(R.id.myActionBar);
        //侧滑菜单
        mActionBar.setData(mActivity, "明细信息", R.drawable.ic_back, 0, 0, getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
            }
        });
        userId = (Integer) SPUtils.get(mActivity, SPUtils.USER_ID, 0);
        news = (News) getIntent().getSerializableExtra("news");
        boolean isBrowse = getIntent().getBooleanExtra("isBrowse", false);
        tvTitle.setText(news.getTitle());
        tvDate.setText(news.getDate());
        tvContent.setText(news.getContent());
        tvIssuer.setText(news.getIssuer());
        Glide.with(mActivity)
                .asBitmap()
                .skipMemoryCache(true)
                .load(news.getImg())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivImg);
        db = helper.getWritableDatabase();
        if (!isBrowse) {//首页进来记录
            String insertSql = "insert into browse(newsId,userId) values(?,?)";
            db.execSQL(insertSql, new Object[]{news.getId(), userId});
        }

        //查询点赞状态
        String selectSql = "select * from collect where newsId=" + news.getId() + " and userId=" + userId;
        Collect collect = null;
        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                Integer dbId = cursor.getInt(0);
                Integer dbNewsId = cursor.getInt(1);
                Integer dbUserId = cursor.getInt(2);
                collect = new Collect(dbId, dbNewsId, dbUserId);
            }
        }
        ivLike.setVisibility(collect == null ? View.VISIBLE : View.GONE);
        ivLikeCheck.setVisibility(collect == null ? View.GONE : View.VISIBLE);

        //查询评论列表
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter();
        rvList.setAdapter(commentAdapter);
        commentAdapter.setItemListener(new CommentAdapter.ItemListener() {
            @Override
            public void ItemClick(CommentVo comment) {

            }

            @Override
            public void Delete(Integer id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                dialog.setMessage("确认要删除该评论吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        if (db.isOpen()) {
                            db.execSQL("delete from comment where id = "+id);
                        }
                        Toast.makeText(mActivity,"删除成功",Toast.LENGTH_LONG).show();
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
        getCommentList();
        //点赞
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String insertSql = "insert into collect(newsId,userId) values(?,?)";
                db.execSQL(insertSql, new Object[]{news.getId(), userId});
                Toast.makeText(mActivity, "点赞成功", Toast.LENGTH_SHORT).show();
                ivLike.setVisibility(View.GONE);
                ivLikeCheck.setVisibility(View.VISIBLE);
            }
        });
        //取消点赞
        ivLikeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deleteSql = "delete from collect where newsId=? and userId=?";
                db.execSQL(deleteSql, new Object[]{news.getId(), userId});
                Toast.makeText(mActivity, "取消成功", Toast.LENGTH_SHORT).show();
                ivLike.setVisibility(View.VISIBLE);
                ivLikeCheck.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取评论列表
     */
    private void getCommentList() {
        String commentSql = "select c.*,u.nickName from comment c,user u where c.userId = u.id and c.newsId=" + news.getId();
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
                CommentVo comment = new CommentVo(dbId, dbNewsId, dbUserId, dbContent, dbDate,nickName);
                list.add(comment);
            }
        }
        if (list.size() > 0){
            commentAdapter.addItem(list);
            rvList.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }else {
            rvList.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
    }

    //发表
    public void publish(View view) {
        String content = etContent.getText().toString();
        if ("".equals(content)) {
            Toast.makeText(mActivity, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String insertSql = "insert into comment(newsId,userId,content,date) values(?,?,?,?)";
        db.execSQL(insertSql, new Object[]{news.getId(), userId, content, sf.format(new Date())});
        Toast.makeText(mActivity, "发表成功", Toast.LENGTH_SHORT).show();
        etContent.setText("");
        getCommentList();
    }
}
