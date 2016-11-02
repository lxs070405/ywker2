package com.y.w.ywker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/22.
 *
 * 修改密码页面
 */
public class ActivityModifyPwd extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;

    @Bind(R.id.edt_old_pwd)
    EditText edtOldPwd;
    @Bind(R.id.edt_new_pwd)
    EditText edtNewPwd;
    @Bind(R.id.edt_new_sure_pwd)
    EditText edtNewSurePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);
        layoutCommToolbarTitle.setText("修改密码");
    }

    @OnClick({R.id.modify_btn_sure,R.id.modify_pwd_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_btn_sure:
                String oldStr = edtOldPwd.getText().toString();
                String newStr = edtNewPwd.getText().toString();
                String newSure = edtNewSurePwd.getText().toString();

                if (oldStr == null || oldStr.equals("")) {
                    Toast.makeText(ActivityModifyPwd.this, "请填写旧密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newStr == null || newStr.equals("")) {
                    Toast.makeText(ActivityModifyPwd.this, "请填写新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newSure == null || newSure.equals("")) {
                    Toast.makeText(ActivityModifyPwd.this, "请填确认新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newSure.equals(newStr)) {
                    Toast.makeText(ActivityModifyPwd.this, "两次新密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newStr.equals(oldStr)) {
                    Toast.makeText(ActivityModifyPwd.this, "您输入的新密码和旧密码一样", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!oldStr.equals(OfflineDataManager.getInstance(this).getLoginPwd())) {
                    Toast.makeText(ActivityModifyPwd.this, "旧密码输入不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                summitPwd(oldStr,newStr);
                break;
            case R.id.modify_pwd_back:
                finish();
                break;
        }
    }

    private void summitPwd(String old, String newPwd) {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("UserID", OfflineDataManager.getInstance(this).getUserID());
        map.put("PWDOld",old);
        map.put("PWDNew",newPwd);

        showLoading();

        httpManagerUtils = new YHttpManagerUtils(this, ConstValues.MODIFY_PWD_URL,
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
                        Toast.makeText(ActivityModifyPwd.this,"修改失败",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        LOG.e(getBaseContext(), "网络不可用.");
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        LOG.e(getBaseContext(),"Json 格式不正确.");
                        break;
                    case ConstValues.MSG_SUCESS:
                        Toast.makeText(ActivityModifyPwd.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                        /**
                         * 将密码保存
                         */
                        OfflineDataManager.getInstance(ActivityModifyPwd.this).saveLoginPwd(edtNewPwd.getText().toString());
                        Utils.start_Activity(ActivityModifyPwd.this,LoginActivity.class,new YBasicNameValuePair[]{});
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
