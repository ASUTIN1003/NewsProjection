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

import com.example.newssqlitefull.R;
import com.example.newssqlitefull.adapter.NewsAdapter;
import com.example.newssqlitefull.adapter.UserAdapter;
import com.example.newssqlitefull.bean.Browse;
import com.example.newssqlitefull.bean.Collect;
import com.example.newssqlitefull.bean.News;
import com.example.newssqlitefull.bean.User;
import com.example.newssqlitefull.enums.UserTypeEnum;
import com.example.newssqlitefull.ui.activity.UserDetailActivity;
import com.example.newssqlitefull.util.KeyBoardUtil;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 用户管理
 */
public class UserManageFragment extends Fragment {
    MySqliteOpenHelper helper = null;
    private Activity myActivity;
    private LinearLayout llEmpty;
    private RecyclerView rvUserList;
    private UserAdapter mUserAdapter;
    private FloatingActionButton btnAdd;
    private EditText etQuery;//搜索内容
    private ImageView ivSearch;//搜索图标
    private List<User> mUsers;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_manage,container,false);
        helper = new MySqliteOpenHelper(myActivity);
        rvUserList = view.findViewById(R.id.rv_user_list);
        llEmpty = view.findViewById(R.id.ll_empty);
        etQuery=view.findViewById(R.id.et_query);
        ivSearch=view.findViewById(R.id.iv_search);
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
        return view;
    }

    private void initView() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(myActivity);
        //=1.2、设置为垂直排列，用setOrientation方法设置(默认为垂直布局)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //=1.3、设置recyclerView的布局管理器
        rvUserList.setLayoutManager(layoutManager);
        //==2、实例化适配器
        //=2.1、初始化适配器
        mUserAdapter=new UserAdapter();
        //=2.3、设置recyclerView的适配器
        rvUserList.setAdapter(mUserAdapter);
        mUserAdapter.setItemListener(new UserAdapter.ItemListener() {
            @Override
            public void ItemClick(User user) {
                Intent intent = new Intent(myActivity, UserDetailActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 100);
            }

            @Override
            public void Delete(Integer id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(myActivity);
                dialog.setMessage("确认要删除该用户吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        String sql = "delete from news where id=?";
                        String sql1 = "delete from collect where userId=?";
                        String sql2 = "delete from browse where userId=?";
                        db.execSQL(sql,new String[]{String.valueOf(id)});
                        db.execSQL(sql1,new String[]{String.valueOf(id)});
                        db.execSQL(sql2,new String[]{String.valueOf(id)});
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
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        String content=etQuery.getText().toString();//获取搜索内容
        mUsers = new ArrayList<>();
        User mUser = null;
        String sql = "select * from user where userType ="+ UserTypeEnum.User.getCode();//普通用户
        if (!"".equals(content)){
            sql+=" and nickName like ?";
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,!"".equals(content)?new String[]{"%"+content+"%"}:null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                Integer dbId = cursor.getInt(0);
                String dbAccount = cursor.getString(1);
                String dbPassword = cursor.getString(2);
                String dbNickName = cursor.getString(3);
                Integer dbAge = cursor.getInt(4);
                String dbEmail = cursor.getString(5);
                Integer dbUserType = cursor.getInt(6);
                mUser = new User(dbId, dbAccount,dbPassword,dbNickName,dbAge,dbEmail,dbUserType);
                mUsers.add(mUser);
            }
        }
        Collections.reverse(mUsers);
        if (mUsers !=null && mUsers.size()>0){
            rvUserList.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
            mUserAdapter.addItem(mUsers);
        }else {
            rvUserList.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK){
            loadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
