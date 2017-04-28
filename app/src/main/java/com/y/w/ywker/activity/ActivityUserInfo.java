package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.ActivityManager;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.views.GlideCircleTransform;
import com.y.w.ywker.views.MaterialDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/22.
 * 我的 个人信息页面
 */
public class ActivityUserInfo extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.user_info_back)
    ImageView userInfoBack;
    @Bind(R.id.user_info_modify_pwd)
    Button userInfoModifyPwd;
    @Bind(R.id.user_info_logout)
    Button userInfoLogout;
    @Bind(R.id.user_info_header)
    ImageView userInfoHeader;
    @Bind(R.id.user_info_account)
    TextView userInfoAccount;
    @Bind(R.id.user_info_name)
    TextView userInfoName;
    @Bind(R.id.user_info_phone)
    TextView userInfoPhone;
    @Bind(R.id.user_info_main)
    TextView userInfoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        layoutCommToolbarTitle.setText("个人信息");
        loadData();
    }

    private void loadData() {
        Gson gson = new Gson();
        UserEntry entery = gson.fromJson(OfflineDataManager.getInstance(this).getUser(), UserEntry.class);
        userInfoAccount.setText(entery.getUserCode());
        userInfoName.setText(entery.getUserName());
        userInfoPhone.setText(entery.getUserTel());
        userInfoMain.setText(entery.getMainName());
        Glide.with(this).load(ConstValues.BASE_URL + entery.getImgUrl()).placeholder(R.drawable.edit_hd).transform(new GlideCircleTransform(this)).into(userInfoHeader);
    }

    private MaterialDialog dialog;

    @OnClick({R.id.user_info_logout, R.id.user_info_modify_pwd, R.id.user_info_back, R.id.user_info_name_layout, R.id.user_info_phone_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_info_back:
                finish();
                break;
            case R.id.user_info_modify_pwd:
                Utils.start_Activity(ActivityUserInfo.this, ActivityModifyPwd.class, new YBasicNameValuePair[]{});
                break;
            case R.id.user_info_logout:
                dialog = new MaterialDialog(this)
                        .setTitle("提示")
                        .setMessage("确定要注销?")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(ActivityUserInfo.this);
                                offlineDataManager.setMainID("");
                                offlineDataManager.setUserID("");
                                dialog.dismiss();
                                OfflineDataManager.getInstance(ActivityUserInfo.this).removeLoginData();
                                ActivityManager.getInstance().finshAllActivities();
                                Utils.start_Activity(ActivityUserInfo.this, LoginActivity.class, new YBasicNameValuePair[]{});
                                finish();
                            }
                        }).setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
                break;
            case R.id.user_info_name_layout:
                startActivityF(ConstValues.RESULT_FOR_MODIFY_NAME, 0);
                break;
            case R.id.user_info_phone_layout:
                startActivityF(ConstValues.RESULT_FOR_MODIFY_PHONE, 1);
                break;
        }
    }

    private void startActivityF(int requestCode, int modify_type) {
        Intent i = new Intent(this, ActivityModifyNameAndPhone.class);
        i.putExtra("modify_type", modify_type);
        i.putExtra("requestCode", requestCode);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String result = data.getStringExtra("result");
        switch (requestCode) {
            case ConstValues.RESULT_FOR_MODIFY_PHONE:
                userInfoPhone.setText(result);
                break;
            case ConstValues.RESULT_FOR_MODIFY_NAME:
                userInfoName.setText(result);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
