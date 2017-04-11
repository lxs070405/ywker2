package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.ParentItemAdapter;
import com.y.w.ywker.entry.ConnectEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 联系人页面
 */
public class ActivityContacts extends SuperActivity implements SwipeRefreshLayout.OnRefreshListener, OnCommAdapterItemClickListener {

    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;
    @Bind(R.id.layout_swipe_refresh_empty_view)
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);

        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        loadData();
//        if( userEntryList.isEmpty()){
//            emptyView.setVisibility(View.VISIBLE);
//        }
        XunJianBiaoJi = getIntent().getStringExtra("xunjian");
    }

    private String XunJianBiaoJi = "";
    private YHttpManagerUtils httpManagerUtils;

    private List<ConnectEntry> userEntryList;
    private List<String> adapterList;

    private void loadData() {
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.CONNTECTINON_URL, mainId), mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }

//    @OnClick({R.id.layout_comm_swipe_refresh_recyclerview, R.id.layout_comm_swipe_refresh})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.layout_comm_swipe_refresh_recyclerview:
//
//                break;
//            case R.id.layout_comm_swipe_refresh:
//                break;
//        }
//    }

    @Override
    public void onItemClick(int position) {

        if ("1".equals(XunJianBiaoJi)) {
            Intent intent = new Intent(this, ActivityXunJianOver.class);
            intent.putExtra("userId", userEntryList.get(position).getID());
            intent.putExtra("UserName", userEntryList.get(position).getUserName());
            setResult(ConstValues.RESULT_FOR_CONTACTS, intent);
            finish();
            return;
        }
        Intent i = new Intent();
        i.setClass(this, ActivityConnectDetails.class);
        Gson gson = new Gson();
        i.putExtra("connect_json", gson.toJson(userEntryList.get(position)));
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        if (httpManagerUtils != null) {
            httpManagerUtils.startRequest();
        }
    }

    private Handler mHandler = new MyHandler();

    @OnClick(R.id.order_list_back)
    public void onClick() {
        finish();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            layoutCommSwipeRefresh.setRefreshing(false);
            switch (msg.what) {
                case ConstValues.MSG_FAILED:
                    emptyView.setVisibility(View.VISIBLE);
                    break;
                case ConstValues.MSG_ERROR:
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(ActivityContacts.this, "服务器返回错误", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValues.MSG_NET_INAVIABLE:
                    Toast.makeText(ActivityContacts.this, "网络不可用", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValues.MSG_JSON_FORMAT_WRONG:
                    emptyView.setVisibility(View.VISIBLE);
                    break;
                case ConstValues.MSG_SUCESS:
                    emptyView.setVisibility(View.GONE);
                    handleData((String) msg.obj);
                    ParentItemAdapter adapter = new ParentItemAdapter(adapterList);
                    adapter.setListener(ActivityContacts.this);
                    layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
                    break;
            }
        }
    }

    private void handleData(String json) {
        if (adapterList != null) {
            adapterList.clear();
        } else {
            adapterList = new ArrayList<String>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ConnectEntry>>() {
        }.getType();
        userEntryList = gson.fromJson(json, type);
        if (userEntryList != null && userEntryList.size() > 0) {
            for (ConnectEntry userEntry : userEntryList) {
                adapterList.add(userEntry.getUserName());
            }
        }
    }

}
