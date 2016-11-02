package com.y.w.ywker.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityPSDSet extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.edt_num)
    EditText edtNum;
    @Bind(R.id.btn_checked)
    Button mBtn;
    @Bind(R.id.edt_new_pwd)
    EditText edtNewPwd;

    private Timer mTimer;
    private final static int COUNT = 1;
    private int count = 60;
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case COUNT:
                    if (count <= 0) {
                        if(count == 0){
                            mBtn.setText("获取手机验证码");
                            mBtn.setBackgroundColor(Color.parseColor("#ff3c78d8"));
                        }
                        mBtn.setEnabled(true);
                        mTimer.cancel();
                        count = 60;
                        return;
                    }
                    mBtn.setBackgroundColor(Color.GRAY);
                    mBtn.setText(count-- + "s 重新发送");
                    mBtn.setEnabled(false);
                    break;
            }
        }
    };
    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psdset);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
       str = getIntent().getStringExtra("PhoneNumber");
        tvPhone.setText(str);
    }

    @OnClick({R.id.btn_checked, R.id.btn_login_in})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_checked:
                mTimer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = mHandle.obtainMessage();
                        msg.what = COUNT;
                        mHandle.sendMessage(msg);
                    }
                };
                //定时器 延迟0秒，每隔一秒就执行一次
                mTimer.schedule(task, 0, 1000);
                String url = String.format(ConstValues.GET_Reset_PWD_URL_GET,str);
                httpManagerUtils = new YHttpManagerUtils(this, url,
                        new MyHandler(this,2),ActivityPSDSet.class.getName());
                httpManagerUtils.startRequest();
                break;
            case R.id.btn_login_in://完成
                postData();
                break;
        }
    }
    private ProgressDialog proDialog;
    private String YanZhengMaData;
    private YHttpManagerUtils httpManagerUtils;
    /**
     * 提交数据
     */
    private void postData() {
        String YanZhengMa = edtNum.getText().toString();
        if(TextUtils.isEmpty(YanZhengMa)){
            Utils.showToast(this,"请填写验证码");
            return;
        }
        if(!YanZhengMa.equals(YanZhengMaData)){
            Utils.showToast(this,"请填写正确的验证码");
            return;
        }
        String MiMa = edtNewPwd.getText().toString();
        if(TextUtils.isEmpty(MiMa)){
            Utils.showToast(this,"请填写密码");
            return;
        }
        if(!Utils.isCanst(MiMa)){
            Utils.showToast(this,"密码格式不正确,密码长度为6到12");
            return;
        }
        proDialog = new ProgressDialog(this);
        proDialog.setTitle("提示");
        proDialog.setMessage("正在提交");
        proDialog.show();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phone", str);
        map.put("code",YanZhengMa);
        map.put("pwd",MiMa);
        httpManagerUtils = new YHttpManagerUtils(this, ConstValues.Reset_PWD_URL,
                mHandler, UserEntry.class.getName());
        httpManagerUtils.startPostRequest(map);
    }
    private Handler mHandler = new MyHandler(this,1);

    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        private int i;
        public MyHandler(AppCompatActivity activitiy, int i) {
            this.i = i;
            mFragmentReference = new WeakReference<AppCompatActivity>(activitiy);
        }

        @Override
        public void handleMessage(Message msg) {
            if(proDialog != null){
                proDialog.dismiss();
            }

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null){
                switch (msg.what){
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        if(i == 1){
                            Toast.makeText(ActivityPSDSet.this,"修改失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(i == 2){
                            mBtn.setEnabled(true);
                            mTimer.cancel();
                            count = 60;
                            mBtn.setText("获取手机验证码");
                            mBtn.setBackgroundColor(Color.parseColor("#ff3c78d8"));
                            Toast.makeText(ActivityPSDSet.this,"获取验证码失败,请联系管理员",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        LOG.e(getBaseContext(), "网络不可用.");
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        LOG.e(getBaseContext(),"Json 格式不正确.");
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(i == 2){
                            YanZhengMaData = (String) msg.obj;
                            return;
                        }
                        Toast.makeText(ActivityPSDSet.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                        /**
                         * 将密码保存
                         */
                        Utils.start_Activity(ActivityPSDSet.this,LoginActivity.class,new YBasicNameValuePair[]{});
                        finish();
                        break;
                }
            }
        }
    }
}
