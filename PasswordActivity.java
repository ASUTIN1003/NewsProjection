package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
 * 重置密码
 */
public class PasswordActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private Activity activity;
    private ActionBar mTitleBar;//标题栏
    private EditText etEmail;
    private EditText etNewPassword;
    private Integer userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity =this;
        setContentView(R.layout.activity_password);
        helper = new MySqliteOpenHelper(this);
        userId = (Integer) SPUtils.get(activity,SPUtils.USER_ID,0);
        etEmail = findViewById(R.id.et_email);
        etNewPassword = findViewById(R.id.et_new_password);
        mTitleBar = (ActionBar)findViewById(R.id.myActionBar);
        mTitleBar.setData(activity,"重置密码", R.drawable.ic_back, 0, 0,getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
            }
        });
    }

    //保存信息
    public void save(View v){
        //关闭虚拟键盘
        InputMethodManager inputMethodManager= (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
        String email = etEmail.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        if ("".equals(email)){//邮箱为空
            Toast.makeText(activity,"邮箱为空", Toast.LENGTH_LONG).show();
            return;
        }
        if ("".equals(newPassword)){//密码为空
            Toast.makeText(activity,"新密码为空", Toast.LENGTH_LONG).show();
            return;
        }
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
        if (user != null && email.equals(user.getEmail())) {
            String updateSql = "update user set password=? where id =?";
            db.execSQL(updateSql,new Object[]{newPassword,userId});
            Toast.makeText(activity, "密码修改成功", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(activity, "邮箱错误", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}
