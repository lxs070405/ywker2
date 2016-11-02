package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/4.
 *
 * 修改手机和姓名页面
 */

public class ActivityModifyNameAndPhone extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.modify_name_back)
    ImageView modifyNameBack;
    @Bind(R.id.modify_name_phone_sure)
    TextView modifyNamePhoneSure;
    @Bind(R.id.edt_phone_name_sure_pwd)
    EditText edtPhoneNameSurePwd;

    /**
     * 0 修改姓名  1 修改手机
     */
    int reqestCode = -1;
    private UserEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone_name);
        ButterKnife.bind(this);
        reqestCode = getIntent().getIntExtra("requestCode", -1);
        if (reqestCode == ConstValues.RESULT_FOR_MODIFY_NAME){
            layoutCommToolbarTitle.setText("修改名字");
            edtPhoneNameSurePwd.setHint("修改名字");
        }else if(reqestCode == ConstValues.RESULT_FOR_MODIFY_PHONE){
            layoutCommToolbarTitle.setText("修改手机号码");
            edtPhoneNameSurePwd.setHint("修改手机号码");
        }

        Gson gson = new Gson();
        entry = gson.fromJson(OfflineDataManager.getInstance(this).getUser(), UserEntry.class);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.modify_name_phone_sure,R.id.modify_name_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_name_phone_sure:
                String newStr = edtPhoneNameSurePwd.getText().toString();
                if (newStr.equals("")) {
                    Toast.makeText(ActivityModifyNameAndPhone.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (reqestCode == ConstValues.RESULT_FOR_MODIFY_PHONE){
                    //判断输入的号码
                    if (isPhoneNum(newStr)){
                        summit(newStr);
                    }else{
                        Toast.makeText(this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                summit(newStr);
                break;
            case R.id.modify_name_back:
                finish();
                break;
        }
    }


    private boolean isPhoneNum(String num){
            Pattern pattern = Pattern
                    .compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))\\d{8}$");
            Matcher matcher = pattern.matcher(num);
            return matcher.matches();
    }

    private void summit(String newStr) {

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("UserID", OfflineDataManager.getInstance(this).getUserID());
        if (reqestCode == ConstValues.RESULT_FOR_MODIFY_NAME){
            map.put("Type","UserName");
        }else if(reqestCode == ConstValues.RESULT_FOR_MODIFY_PHONE){
            map.put("Type","UserTel");
        }
        map.put("SendDetail",newStr);
        for (String key : map.keySet()){
            Log.e("tag",key + " : " + map.get(key));
        }
        showLoading();
        httpManagerUtils = new YHttpManagerUtils(this, ConstValues.MODIFY_NAME_PHONE_URL,
                mHandler, UserEntry.class.getName());

        httpManagerUtils.startPostRequest(map);
    }

    private YHttpManagerUtils httpManagerUtils;

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
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ActivityModifyNameAndPhone.this,"修改失败",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        LOG.e(getBaseContext(),"网络不可用.");
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        LOG.e(getBaseContext(),"Json 格式不正确.");
                        break;
                    case ConstValues.MSG_SUCESS:
                        String result = edtPhoneNameSurePwd.getText().toString();
                        if (reqestCode == ConstValues.RESULT_FOR_MODIFY_NAME){
                            if (entry != null){
                                entry.setUserName(result);
                            }
                        }else if(reqestCode == ConstValues.RESULT_FOR_MODIFY_PHONE){
                            if (entry != null){
                                entry.setUserTel(result);
                            }
                        }
                        Gson gson = new Gson();
                        OfflineDataManager.getInstance(ActivityModifyNameAndPhone.this).saveUser(gson.toJson(entry));
                        Intent i = new Intent();
                        i.putExtra("result",result);
                        setResult(reqestCode, i);
                        finish();
                        break;
                }
            }
        }
    }

}
