package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newssqlitefull.R;
import com.example.newssqlitefull.bean.News;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.widget.ActionBar;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 添加新闻页面
 */
public class AddNewsActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private ActionBar mActionBar;//标题栏
    private Activity myActivity;
    private EditText etTitle;//标题
    private EditText etIssuer;//发布单位
    private EditText etImg;//图片
    private Spinner spType;//类型
    private EditText etContent;//内容
    private ImageView ivImg;//图片
    SimpleDateFormat sf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private News mNews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = this;
        helper = new MySqliteOpenHelper(this);
        setContentView(R.layout.activity_news_add);
        etTitle = findViewById(R.id.title);
        etIssuer = findViewById(R.id.issuer);
        spType = findViewById(R.id.type);
        etImg = findViewById(R.id.img);
        etContent = findViewById(R.id.content);
        ivImg = findViewById(R.id.iv_img);
        mActionBar = findViewById(R.id.myActionBar);
        initView();
        //侧滑菜单
        mActionBar.setData(myActivity,"编辑新闻信息", R.drawable.ic_back, mNews != null?R.drawable.ic_comment:0, 0, getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(myActivity,CommentActivity.class);
                intent.putExtra("id",mNews.getId());
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mNews = (News) getIntent().getSerializableExtra("news");
        if (mNews !=null){
            etTitle.setText(mNews.getTitle());
            spType.setSelection(mNews.getTypeId());
            etImg.setText(mNews.getImg());
            etIssuer.setText(mNews.getIssuer());
            etContent.setText(mNews.getContent());
            spType.setSelection(mNews.getTypeId(),true);
            Glide.with(myActivity)
                    .asBitmap()
                    .load(mNews.getImg())
                    .error(R.drawable.ic_error)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivImg);
        }
        ivImg.setVisibility(mNews ==null? View.GONE: View.VISIBLE);
    }

    public void save(View view){
        SQLiteDatabase db = helper.getWritableDatabase();
        String title = etTitle.getText().toString();
        String issuer = etIssuer.getText().toString();
        String img = etImg.getText().toString();
            String content = etContent.getText().toString();
            Integer typeId =  spType.getSelectedItemPosition();
        if ("".equals(title)) {
            Toast.makeText(myActivity,"标题不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if ("".equals(issuer)) {
            Toast.makeText(myActivity,"发布单位不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if ("".equals(img)) {
            Toast.makeText(myActivity,"图片地址不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if ("".equals(content)) {
            Toast.makeText(myActivity,"新闻描述不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (mNews == null ){//新增
            String insertSql = "insert into news(typeId,title,img,content,issuer,date) values(?,?,?,?,?,?)";
            db.execSQL(insertSql,new Object[]{typeId,title,img,content,issuer,sf.format(new Date())});
        }else {//修改
            String updateSql = "update news set typeId=?,title=?,img=?,content=?,issuer=? where id =?";
            db.execSQL(updateSql,new Object[]{typeId,title,img,content,issuer,mNews.getId()});
        }
        Toast.makeText(myActivity,"保存成功", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
        db.close();
    }
}
