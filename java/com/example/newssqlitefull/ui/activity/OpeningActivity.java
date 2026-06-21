package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newssqlitefull.R;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.util.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 开屏页面
 */
public class OpeningActivity extends AppCompatActivity {
    private Activity myActivity;
    MySqliteOpenHelper helper = null;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = this;
        helper = new MySqliteOpenHelper(this);
        //设置页面布局
        setContentView(R.layout.activity_opening);
        try {
            initView();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }
    private void initView() throws IOException, JSONException {
        StatusBarUtil.setStatusBar(myActivity,true);//设置当前界面是否是全屏模式（状态栏）
        StatusBarUtil.setStatusBarLightMode(myActivity,true);//状态栏文字颜色
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
                    finish();
                    return;
                }
                Boolean isFirst= (Boolean) SPUtils.get(myActivity,SPUtils.IF_FIRST,true);
                Integer userId= (Integer) SPUtils.get(myActivity,SPUtils.USER_ID,0);
                if (isFirst){//第一次进来  初始化本地数据
                    SQLiteDatabase db = helper.getWritableDatabase();
                    SPUtils.put(myActivity,SPUtils.IF_FIRST,false);//第一次
                    //初始化数据
                    //获取json数据
                    String rewardJson = "";
                    String rewardJsonLine;
                    //assets文件夹下db.json文件的路径->打开db.json文件
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(myActivity.getAssets().open("db.json")));
                        while (true) {
                            if (!((rewardJsonLine = bufferedReader.readLine()) != null)) break;
                            rewardJson += rewardJsonLine;
                        }
                        JSONObject jsonObject = new JSONObject(rewardJson);
                        JSONArray newsList = jsonObject.getJSONArray("news");//获得新闻列表
                        //把物品列表保存到本地
                        for (int i = 0, length = newsList.length(); i < length; i++) {//初始化新闻
                            JSONObject o = newsList.getJSONObject(i);
                            int typeId = o.getInt("typeId");
                            String title = o.getString("title");
                            String img = o.getString("img");
                            String content = o.getString("content");
                            String issuer = o.getString("issuer");
                            String date = sf.format(new Date());
                            String insertSql = "insert into news(typeId,title,img,content,issuer,date) values(?,?,?,?,?,?)";
                            db.execSQL(insertSql,new Object[]{typeId,title,img,content,issuer,date});
                        }
                        db.close();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                //两秒后跳转到主页面
                Intent intent = new Intent();
                if (userId > 0) {//已登录
                    intent.setClass(OpeningActivity.this, MainActivity.class);
                }else {
                    intent.setClass(OpeningActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }


    @Override
    public void onBackPressed() {

    }
}
