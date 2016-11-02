package com.y.w.ywker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/4.
 * 意见反馈页面
 */
public class ActivityFeedBack extends SuperActivity {


    /**
     * 0 修改姓名  1 修改手机
     */
    int reqestCode = -1;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.feed_back_btn_sure)
    TextView feedBackBtnSure;
    @Bind(R.id.edt_feed_back)
    EditText edtFeedBack;
    private UserEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        layoutCommToolbarTitle.setText("意见反馈");
    }

    @OnClick({R.id.feed_back_btn_back,R.id.feed_back_btn_sure})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_back_btn_sure:
                String newStr = edtFeedBack.getText().toString();
                if (newStr.equals("")) {
                    Toast.makeText(ActivityFeedBack.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                summit(newStr);
                break;
            case R.id.feed_back_btn_back:
                finish();
                break;
        }
    }


    /**
     * 提交意见反馈
     * @param newStr
     */
    private void summit(String newStr) {

        showLoading();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", OfflineDataManager.getInstance(this).getUserID());
        map.put("SendDetail",newStr);
        httpManagerUtils = new YHttpManagerUtils(this, ConstValues.FEED_BACK_URL,
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

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        Toast.makeText(ActivityFeedBack.this,"反馈失败",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ActivityFeedBack.this,"反馈失败",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        LOG.e(getBaseContext(), "网络不可用.");
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        LOG.e(getBaseContext(), "Json 格式不正确.");
                        break;
                    case ConstValues.MSG_SUCESS:
                        Toast.makeText(ActivityFeedBack.this,"反馈成功",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }

}