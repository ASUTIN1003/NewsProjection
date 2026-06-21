package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newssqlitefull.R;
import com.example.newssqlitefull.bean.User;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.widget.ActionBar;

import org.litepal.crud.DataSupport;

/**
 * 用户明细
 */
public class UserDetailActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private ActionBar mActionBar;//标题栏
    private Activity mActivity;
    private TextView account;
    private EditText nickName;
    private EditText age;
    private EditText email;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mActivity = this;
        helper = new MySqliteOpenHelper(this);
        account = findViewById(R.id.account);
        nickName = findViewById(R.id.nickName);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email);
        mActionBar = findViewById(R.id.myActionBar);
        //侧滑菜单
        mActionBar.setData(mActivity,"用户信息", R.drawable.ic_back, 0, 0, getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
            }
        });
        mUser = (User) getIntent().getSerializableExtra("user");
        if (mUser != null) {
            account.setText(mUser.getAccount());
            nickName.setText(mUser.getNickName());
            age.setText(String.valueOf(mUser.getAge()));
            email.setText(mUser.getEmail());
        }
    }

    //保存
    public void save(View view){
        SQLiteDatabase db = helper.getWritableDatabase();
        String nickNameStr = nickName.getText().toString();
        String ageStr = age.getText().toString();
        String emailStr = email.getText().toString();
        if ("".equals(nickNameStr)) {
            Toast.makeText(mActivity,"昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(ageStr)) {
            Toast.makeText(mActivity,"年龄不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(emailStr)) {
            Toast.makeText(mActivity,"邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mUser != null) {
            String insertSql = "update user set nickName=?,age=?,email=? where id =?";
            db.execSQL(insertSql,new Object[]{nickNameStr,ageStr,emailStr,mUser.getId()});
            db.close();
            Toast.makeText(mActivity,"保存成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }else {
            Toast.makeText(mActivity,"保存失败", Toast.LENGTH_SHORT).show();
        }

    }
}
