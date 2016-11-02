package com.y.w.ywker.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lxs on 16/4/12.
 * 启动页
 */
public class SplashActivity extends SuperActivity {

    private YHttpManagerUtils httpManagerUtils;
    private Context ctx;
    OfflineDataManager offlineDataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ctx = this;
        httpManagerUtils = new YHttpManagerUtils(SplashActivity.this, ConstValues.GetNewVersionForApp,new MyHandler(this,2), UserEntry.class.getName());
        httpManagerUtils.startTextRequest();
        httpManagerUtils = new YHttpManagerUtils(SplashActivity.this, ConstValues.GetNewVersionPath,new MyHandler(this,3), UserEntry.class.getName());
        httpManagerUtils.startTextRequest();

           //执行登录,如果第一次登陆,就需要跳转到登录页
           offlineDataManager = OfflineDataManager.getInstance(this);
           String n = offlineDataManager.getLoginName();
           String p = offlineDataManager.getLoginPwd();
           if (!n.isEmpty() && !p.isEmpty()){
               autologin(n, p);
           }else{
               Timer timer = new Timer();
               timer.schedule(new TimerTask() {
                   @Override
                   public void run() {
                       finish();
                       Utils.start_Activity(SplashActivity.this, LoginActivity.class, new YBasicNameValuePair[]{});
                   }
               }, 1500);
           }
    }

    private MaterialDialog dialogTip;
    private Handler mHandler = new MyHandler(this,1);
    private  String serviceurl;
    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        int i ;
        public MyHandler(AppCompatActivity activitiy, int i) {
            mFragmentReference = new WeakReference<AppCompatActivity>(activitiy);
            this.i = i;
        }

        @Override
        public void handleMessage(Message msg) {

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null){
                switch (msg.what){
                    case ConstValues.MSG_FAILED:
                        if(i== 2||i ==3){
                            return;
                        }
                        Utils.start_Activity(SplashActivity.this,LoginActivity.class,new YBasicNameValuePair[]{});
                        break;
                    case ConstValues.MSG_ERROR:
                        if(i== 2||i ==3){
                            return;
                        }
                        Utils.start_Activity(SplashActivity.this,LoginActivity.class,new YBasicNameValuePair[]{});
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        if(i== 2||i ==3){
                            return;
                        }
                        Toast.makeText(SplashActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
                        Utils.start_Activity(SplashActivity.this,LoginActivity.class,new YBasicNameValuePair[]{});
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        if(i== 2||i ==3){
                            return;
                        }
                        Utils.start_Activity(SplashActivity.this,LoginActivity.class,new YBasicNameValuePair[]{});
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(i == 3){
                           String serviceurl = (String)msg.obj;
                            offlineDataManager.saveAppPath(serviceurl);
                            Log.e("lxs", "handleMessage: serviceurl"+serviceurl);
                        }
                        if(i== 2){
                            String  serviceversion = (String)msg.obj;
                            offlineDataManager.saveVersionCode( serviceversion);
                            Log.e("lxs", "handleMessage: serviceversion"+serviceversion);
                        }

                       if(i == 1){
                           Gson gson = new Gson();
                           Type listType = new TypeToken<List<UserEntry>>(){}.getType();

                           String msgInfo = (String)msg.obj;

                           if (TextUtils.isEmpty(msgInfo)){
                               Utils.start_Activity(SplashActivity.this,LoginActivity.class,new YBasicNameValuePair[]{});
                               finish();
                               return;
                           }
                           /**
                            * 数据太贱了,每次都是返回一个数组
                            */
                           List<UserEntry> userEntries = gson.fromJson(msgInfo, listType);
                           if (userEntries != null && userEntries.size() > 0){
                               UserEntry userEntry = userEntries.get(0);
                               /**
                                * 存储用户信息
                                */
                               OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(SplashActivity.this);
                               offlineDataManager.setMainID(userEntry.getMainID());
                               offlineDataManager.setUserID(userEntry.getID());
                               offlineDataManager.saveObj("username",userEntry.getUserName());
                               ConstValues.UserID = userEntry.getID();
                               try {
                                   /**
                                    * 将数据转化为JSON字符串保存
                                    */
                                   String userInfo = gson.toJson(userEntry);
                                   Log.e("lxs", "闪屏handleMessage: userInfo--->"+userInfo );
                                   if (!TextUtils.isEmpty(userInfo)){
                                       offlineDataManager.saveUser(userInfo);
                                   }
                               }catch (Exception e){
                                   LOG.e(SplashActivity.this,"USER TO JSON ERROR.");
                               }

                           }
                           Utils.start_Activity(SplashActivity.this, HomeActivity.class, new YBasicNameValuePair[]{});
                           finish();
                       }
                        break;
                }
            }
        }
    }

    /**
     * 执行自动登录
     * @param username
     * @param pwd
     */
    private void autologin(String username,String pwd){
        OfflineDataManager.getInstance(this).saveLoginName(username);
        OfflineDataManager.getInstance(this).saveLoginPwd(pwd);

        try {
            String url = String.format(ConstValues.LOGIN_URL, URLEncoder.encode(username,"utf-8"), URLEncoder.encode(pwd,"utf-8"));
            httpManagerUtils = new YHttpManagerUtils(SplashActivity.this, url,mHandler, UserEntry.class.getName());
            httpManagerUtils.startRequest();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

}
