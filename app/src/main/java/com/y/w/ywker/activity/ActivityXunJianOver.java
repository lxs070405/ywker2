package com.y.w.ywker.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.JiHuaEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.ActivityManager;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.DateTimePickerDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityXunJianOver extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;

    @Bind(R.id.et_SuerName)
    EditText etSuerName;
    @Bind(R.id.et_SureConacts)
    EditText etSureConacts;
    @Bind(R.id.tv_XunJianNum)
    TextView tvXunJianNum;
    @Bind(R.id.et_zongjie)
    EditText etZongjie;
    @Bind(R.id.ll_time)
    LinearLayout llTime;
    @Bind(R.id.ll_ZhiXingRen)
    LinearLayout llZhiXingRen;
    @Bind(R.id.btn_over)
    Button btnOver;
    @Bind(R.id.tv_ZhiXingTime)
    TextView tvZhiXingTime;
    @Bind(R.id.tv_ZhiXingRen)
    TextView tvZhiXingRen;
    String PatrolID;
    String recordId;
    private HashMap<String, String> map = new HashMap<String, String>();
    /**
     * 客户确认人
     */
   private String user;
    /**
     * 联系方式
     */
    private String lianxi;
    private String xunjianNum = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xunianover);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
       xunjianNum = getIntent().getStringExtra("Number");
        tvXunJianNum.setText(xunjianNum);
        PatrolID = getIntent().getStringExtra("PatrolID");//巡检计划ID
        showLoading();
        httpManagerUtils = new YHttpManagerUtils(this,String.format(ConstValues.GET_JIHUA_URL, PatrolID),
                new MyHandler(this,2), this.getClass().getName());
        httpManagerUtils.startRequest();
        recordId = getIntent().getStringExtra("recordId");
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        hideKeyboard();
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        tvZhiXingRen.setText(offlineDataManager.getObj("username"));
        map.put("NextInspectPerson", offlineDataManager.getUserID());
        map.put("PatrolID", PatrolID);
        map.put("recordId", recordId);
        map.put("InspectSummary",etZongjie.getText().toString());
    }

    public void showDialog() {
        DateTimePickerDialog dialog = new DateTimePickerDialog(this, System.currentTimeMillis());
        dialog.setOnDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener() {
            @Override
            public void OnDateTimeSet(AlertDialog dialog, long date) {
                tvZhiXingTime.setText(getStringDate(date));
            }
        });
        dialog.show();
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//HH:mm:ss
        String dateString = formatter.format(date);
        return dateString;
    }

    @OnClick({R.id.btn_back_devices_info, R.id.ll_time, R.id.ll_ZhiXingRen, R.id.btn_over})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_devices_info:
                finish();
                break;
            case R.id.ll_time:
                showDialog();
                break;
            case R.id.ll_ZhiXingRen:// map.put("NextInspectPerson", offlineDataManager.getUserID());
                getUserData();
                break;
            case R.id.btn_over://确认完成
                user = etSuerName.getText().toString().trim();
                lianxi = etSureConacts.getText().toString().trim();
                if(TextUtils.isEmpty(user)){
                    Toast.makeText(this,"请填写客户确认人",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(lianxi)){
                    Toast.makeText(this,"请填写联系方式",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(!CheckUtils.isPhoneNumberValid(lianxi)){
//                    Toast.makeText(this,"请填写正确的电话号码",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                proDialog = new ProgressDialog(this);
                proDialog.setTitle("提示");
                proDialog.setMessage("正在提交");
                proDialog.show();
                postData();
                break;
        }
    }
    private YHttpManagerUtils httpManagerUtils;
    /**
     * 获取人员列表信息
     */
    private void getUserData() {
        Utils.start_ActivityResult(this,ActivityContacts.class,ConstValues.RESULT_FOR_CONTACTS, new YBasicNameValuePair[]{
                        new YBasicNameValuePair("xunjian", "1")});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstValues.RESULT_FOR_CONTACTS && data != null) {
            String userId = data.getStringExtra("userId");
            String UserName = data.getStringExtra("UserName");
            map.put("NextInspectPerson", userId);
            tvZhiXingRen.setText(UserName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 提交数据
     */
    private void postData() {
        map.put("ClientConfirmedUser",user);
        map.put("ClientTel",lianxi);
        map.put("NextInspectTime",tvZhiXingTime.getText().toString());
        YHttpManagerUtils managerUtils = new YHttpManagerUtils(this, ConstValues.POST_OVER_XUNJIAN, mHandler, getClass().getName());
        managerUtils.startPostRequest(map);
    }
    private Handler mHandler = new MyHandler(this,1);
    private InputMethodManager manager;
    private ProgressDialog proDialog;
    class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mFragmentReference;
        private int type;
        public MyHandler(AppCompatActivity fragment,int type) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.type = type;
        }
        @Override
        public void handleMessage(Message msg) {
            dismissLoading();
            if (proDialog != null) {
                proDialog.dismiss();
            }
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        if(type == 2){
                            return;
                        }
                        Toast.makeText(getBaseContext(), "提交失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(type == 2){
                           String str = (String)msg.obj;
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<JiHuaEntry>>() {
                            }.getType();
                            List<JiHuaEntry> list = gson.fromJson(str,listType);
                            if(list != null && list.size()> 0){
                                String time = list.get(0).getNextInspectTime();
                                time =  time.substring(0,time.indexOf(" "));
                                time = time.replace("/","-");
                                tvZhiXingTime.setText(time);
                                tvZhiXingRen.setText(list.get(0).getNextInspectPersonName());
                                map.put("NextInspectPerson",list.get(0).getNextInspectPersonId());
                            }
                            return;
                        }
                        Toast.makeText(getBaseContext(), "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                        ActivityManager.getInstance().finshActivities(ActivityXunJianLook.class,ActivityXunJian.class,
                                ActivityLookedXunJianAseet.class);
                        Utils.start_Activity(ActivityXunJianOver.this,ActivityXunJian.class);
                        break;
                }
            }
        }
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

    @Override
    protected void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }
}
