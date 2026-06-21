package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newssqlitefull.R;
import com.example.newssqlitefull.bean.User;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.widget.ActionBar;

import org.litepal.crud.DataSupport;

/**
 * 个人信息
 */
public class PersonActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private Activity mActivity;
    private ActionBar mTitleBar;//标题栏
    private TextView tvAccount;
    private TextView etNickName;
    private TextView etAge;
    private TextView etEmail;
    private Button btnSave;//保存
    private Integer userId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        mActivity = this;
        helper = new MySqliteOpenHelper(this);
        userId = (Integer) SPUtils.get(mActivity,SPUtils.USER_ID,0);
        tvAccount = findViewById(R.id.tv_account);
        etNickName = findViewById(R.id.tv_nickName);
        etAge = findViewById(R.id.tv_age);
        etEmail = findViewById(R.id.tv_email);
        btnSave = findViewById(R.id.btn_save);
        mTitleBar = (ActionBar) findViewById(R.id.myActionBar);
        mTitleBar.setData(mActivity,"个人信息", R.drawable.ic_back, 0, 0, getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
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

    /**
     * 初始化
     */
    private void initView() {
        User user = null;
        String sql = "select * from user where id = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                Integer dbId = cursor.getInt(0);
                String dbAccount = cursor.getString(1);
                String dbPassword = cursor.getString(2);
                String dbNickName = cursor.getString(3);
                Integer dbAge = cursor.getInt(4);
                String dbEmail = cursor.getString(5);
                Integer dbUserType = cursor.getInt(6);
                user = new User(dbId, dbAccount,dbPassword,dbNickName,dbAge,dbEmail,dbUserType);
            }
        }
        db.close();
        if (user != null) {
            tvAccount.setText(user.getAccount());
            etNickName.setText(user.getNickName());
            etAge.setText(String.valueOf(user.getAge()));
            etEmail.setText(user.getEmail());
        }
        //保存
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getWritableDatabase();
                String nickName = etNickName.getText().toString();
                String age = etAge.getText().toString();
                String email = etEmail.getText().toString();
                if ("".equals(nickName)) {
                    Toast.makeText(mActivity,"昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(age)) {
                    Toast.makeText(mActivity,"年龄不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(email)) {
                    Toast.makeText(mActivity,"邮箱不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String updateSql = "update user set nickName=?,age=?,email=? where id =?";
                db.execSQL(updateSql,new Object[]{nickName,age,email,userId});
                db.close();
                Toast.makeText(mActivity,"保存成功", Toast.LENGTH_SHORT).show();
                finish();//关闭页面
            }
        });
    }

}
