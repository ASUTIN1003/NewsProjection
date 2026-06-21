package com.example.newssqlitefull.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.newssqlitefull.MyApplication;
import com.example.newssqlitefull.R;
import com.example.newssqlitefull.enums.UserTypeEnum;
import com.example.newssqlitefull.ui.activity.BrowseActivity;
import com.example.newssqlitefull.ui.activity.CollectActivity;
import com.example.newssqlitefull.ui.activity.LoginActivity;
import com.example.newssqlitefull.ui.activity.PasswordActivity;
import com.example.newssqlitefull.ui.activity.PersonActivity;
import com.example.newssqlitefull.util.SPUtils;


/**
 * 个人中心
 */
public class UserFragment extends Fragment {
    private Activity mActivity;
    private LinearLayout llPerson;
    private LinearLayout llSecurity;
    private LinearLayout llBrowse;
    private LinearLayout llFavorite;
    private Button btnLogout;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        llPerson = view.findViewById(R.id.person);
        llSecurity = view.findViewById(R.id.security);
        llBrowse = view.findViewById(R.id.browse);
        llFavorite = view.findViewById(R.id.favorite);
        btnLogout = view.findViewById(R.id.logout);
        initView();
        return view;
    }

    private void initView() {
        //用户显示
        Integer userType = (Integer) SPUtils.get(mActivity,SPUtils.USER_TYPE,0);
        llBrowse.setVisibility(userType.intValue() == UserTypeEnum.User.getCode() ? View.VISIBLE: View.GONE);
        llFavorite.setVisibility(userType.intValue() == UserTypeEnum.User.getCode()? View.VISIBLE: View.GONE);
        //个人信息
        llPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转页面
                Intent intent = new Intent(mActivity, PersonActivity.class);
                startActivity(intent);
            }
        });
        //账号安全
        llSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转页面
                Intent intent = new Intent(mActivity, PasswordActivity.class);
                startActivity(intent);
            }
        });
        //浏览记录
        llBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转页面
                Intent intent = new Intent(mActivity, BrowseActivity.class);
                startActivity(intent);
            }
        });
        //收藏夹
        llFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转页面
                Intent intent = new Intent(mActivity, CollectActivity.class);
                startActivity(intent);
            }
        });
        //退出登录
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.Instance.getMainActivity().finish();
                SPUtils.remove(mActivity,SPUtils.USER_ID);
                SPUtils.remove(mActivity,SPUtils.USER_TYPE);
                startActivity(new Intent(mActivity, LoginActivity.class));
            }
        });
    }
}
