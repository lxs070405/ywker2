package com.y.w.ywker.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MaterialDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A login screen that offers login via email/password.
 * 登陆
 */

public class LoginActivity extends SuperActivity {
    @Bind(R.id.login_user_name_icon)
    ImageView loginUserNameIcon;
    @Bind(R.id.login_edt_username)
    EditText loginEdtUsername;
    @Bind(R.id.login_user_pwd_icon)
    ImageView loginUserPwdIcon;
    @Bind(R.id.login_edt_userpwd)
    EditText loginEdtUserpwd;

    private YHttpManagerUtils httpManagerUtils;

    private InputMethodManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        hideKeyboard();
    }

    private boolean isClick = false;
    private String username;
    @OnClick({R.id.btn_login_in})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login_in:
                if (!isClick) {
                    isClick = true;
                    username = loginEdtUsername.getText().toString().trim();
                    String pwd = loginEdtUserpwd.getText().toString().trim();
                    if (!username.isEmpty() && !pwd.isEmpty()) {
                        login(username, pwd);
                    }else{
                        Toast.makeText(this,"账号或密码未填写",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }



    private MaterialDialog dialogTip;
    private Handler mHandler = new MyHandler(this);

    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity activitiy) {
            mFragmentReference = new WeakReference<AppCompatActivity>(activitiy);
        }

        @Override
        public void handleMessage(Message msg) {

            dismissLoading();

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null){
                switch (msg.what){
                    case ConstValues.MSG_FAILED:
                        LOG.e(getBaseContext(),"登录失败.");
                        isClick = false;
                        dialogTip = new MaterialDialog(LoginActivity.this)
                                .setTitle("提示")
                                .setMessage("登录失败,请检查用户名和密码是否正确")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(LoginActivity.this);
                                        String n = offlineDataManager.getLoginName();
                                        String p = offlineDataManager.getLoginPwd();
                                        if (!n.isEmpty() && !p.isEmpty()) {
                                            login(n, p);
                                        }
                                        dialogTip.dismiss();
                                    }
                                }).setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogTip.dismiss();
                                    }
                                });
                        dialogTip.show();
                        break;
                    case ConstValues.MSG_ERROR:
                        LOG.e(getBaseContext(),"登录失败.");
                        isClick = false;
                        dialogTip = new MaterialDialog(LoginActivity.this)
                                .setTitle("提示")
                                .setMessage("登录失败,请检查用户名和密码是否正确")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(LoginActivity.this);
                                        String n = offlineDataManager.getLoginName();
                                        String p = offlineDataManager.getLoginPwd();
                                        if (!n.isEmpty() && !p.isEmpty()) {
                                            login(n, p);
                                        }
                                        dialogTip.dismiss();
                                    }
                                }).setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogTip.dismiss();
                                    }
                                });
                        dialogTip.show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        LOG.e(getBaseContext(),"网络不可用.");
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        LOG.e(getBaseContext(),"Json 格式不正确.");
                        break;
                    case ConstValues.MSG_SUCESS:
                        String msgInfo = (String)msg.obj;
                        if (TextUtils.isEmpty(msgInfo)){
                            return;
                        }
                        parsData(msgInfo);
                        break;
                }
            }
        }
    }

    /**
     * 解析数据
     * @param msgInfo
     */
    private void parsData(String msgInfo) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<UserEntry>>(){}.getType();
        List<UserEntry> userEntries = gson.fromJson(msgInfo, listType);
        boolean isfirstlogin = true;
        if (userEntries != null && userEntries.size() > 0){
            UserEntry userEntry = userEntries.get(0);
            OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(LoginActivity.this);
            offlineDataManager.setMainID(userEntry.getMainID());
            offlineDataManager.setUserID(userEntry.getID());
            offlineDataManager.saveUser(gson.toJson(userEntry));
            Log.e("lxs", "登陆页: userInfo--->"+gson.toJson(userEntry) );
            offlineDataManager.saveObj("username",userEntry.getUserName());
           isfirstlogin = Boolean.parseBoolean(userEntry.getIsFirstLogin().toLowerCase()) ;
        }
//        isfirstlogin = true;
        if(isfirstlogin){

            Utils.start_Activity(LoginActivity.this,ActivityPSDSet.class,new YBasicNameValuePair[]{
                    new YBasicNameValuePair("PhoneNumber",username)
            });
            isClick = false;
        }else{
        Utils.start_Activity(LoginActivity.this, HomeActivity.class, new YBasicNameValuePair[]{});
        finish();
        }

    }

    private void login(String username,String pwd){

        showLoading();

        OfflineDataManager.getInstance(this).saveLoginName(username);
        OfflineDataManager.getInstance(this).saveLoginPwd(pwd);

        String url = String.format(ConstValues.LOGIN_URL,username,pwd);
        httpManagerUtils = new YHttpManagerUtils(LoginActivity.this, url,
                    mHandler, UserEntry.class.getName());
        httpManagerUtils.startRequest();
    }

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else{
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

