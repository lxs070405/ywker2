package com.y.w.ywker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.MsgListItemAdapter;
import com.y.w.ywker.entry.MsgEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.interf.OnOrderListItemListener;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/5.
 *
 * 消息列表页面
 */
public class ActivityMsgList extends SuperActivity implements OnOrderListItemListener{
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.msg_list_back)
    ImageView msgListBack;
    @Bind(R.id.msg_list_recyclerview)
    RecyclerView msgListRecyclerview;
    @Bind(R.id.msg_empty_view)
    View emptyView;

    YHttpManagerUtils httpManagerUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_list);
        ButterKnife.bind(this);

        emptyView.setVisibility(View.GONE);
        layoutCommToolbarTitle.setText("消息列表");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        msgListRecyclerview.setLayoutManager(layoutManager);
    }
    @Override
    protected void onResume() {
        super.onResume();
        showLoading();
        String userid = OfflineDataManager.getInstance(this).getUserID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_MSG_LIST_URL,
                userid), new MyHandler(this), this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    @OnClick(R.id.msg_list_back)
    public void onBack(){
        finish();
    }

    /**
     * @param id 消息ID
     * @param title 用户名
     * @param level
     * @param status
     */
    @Override
    public void onClickOrder(String id, String title, String level, String status) {
        Utils.start_Activity(this,ActivityMsgReplay.class,new YBasicNameValuePair[]{
                new YBasicNameValuePair("send_id",id),
                new YBasicNameValuePair("send_name",title)
        });
    }

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
                        LOG.e(getBaseContext(), "获取失败.");
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(ActivityMsgList.this,"获取失败",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        LOG.e(getBaseContext(), "获取失败.");
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(ActivityMsgList.this,"获取失败",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        LOG.e(getBaseContext(), "网络不可用.");
                        Toast.makeText(ActivityMsgList.this,"网络不可用",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        emptyView.setVisibility(View.VISIBLE);
                        LOG.e(getBaseContext(), "Json 格式不正确.");
                        break;
                    case ConstValues.MSG_SUCESS:
                        LOG.e(ActivityMsgList.this,"成功...");
                        emptyView.setVisibility(View.GONE);
                        handleData((String) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private List<MsgEntry> msgList;
    private MsgListItemAdapter adapter;
    private void handleData(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<MsgEntry>>(){}.getType();
        msgList = gson.fromJson(json,type);
        if (msgList != null){
            adapter = new MsgListItemAdapter(this,msgList);
            adapter.setItemClickListener(this);
            msgListRecyclerview.setAdapter(adapter);
        }else{
            LOG.e(this,"解析失败");
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

}
