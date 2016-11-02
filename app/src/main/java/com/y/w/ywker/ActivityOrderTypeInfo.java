package com.y.w.ywker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.activity.NewWorkOrderActivity;
import com.y.w.ywker.adapters.ParentItemAdapter;
import com.y.w.ywker.entry.OrderTypeEntry;
import com.y.w.ywker.entry.SerializableMap;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单类型
 */
public class ActivityOrderTypeInfo extends BaseSwipeRefreshActivity implements OnCommAdapterItemClickListener {

    String title;
    SerializableMap outMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = getIntent().getStringExtra("title");
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_type_info);
        layoutCommSwipeRefresh.setRefreshing(false);
        outMap = (SerializableMap) getIntent().getSerializableExtra("new_order_map");
    }

    @Override
    public void loadData() {
        loadFromNet(String.format(ConstValues.GET_ORDER_TYPE, OfflineDataManager.getInstance(this).getMainID()));
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void initTitleView() {
        layoutCommToolbarTitle.setText(title);
    }

    @Override
    public void onRefresh() {

    }

    YHttpManagerUtils httpManagerUtils;

    private void loadFromNet(String url) {
        httpManagerUtils = new YHttpManagerUtils(this, url,
                mHandler, getClass().getName());
        httpManagerUtils.startRequest();
    }

    private Handler mHandler = new MyHandler(this);

    @Override
    public void onItemClick(int position) {
        if (orderTypeEntryList != null && position < orderTypeEntryList.size()) {
           String  name = orderTypeEntryList.get(position).getTypeName();
           String  _ids = orderTypeEntryList.get(position).getID();
            Intent intent = new Intent(this,NewWorkOrderActivity.class);
            intent.putExtra("Typename", name);
            intent.putExtra("TypeId",_ids);
            intent.putExtra("new_order_map",outMap);
            startActivity(intent);
            finish();
        }
    }

    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity fragment) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null) {
                layoutCommSwipeRefresh.setRefreshing(false);
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:

                        handleData((String) msg.obj);
                        ParentItemAdapter adapter = new ParentItemAdapter(lists);
                        adapter.setListener(ActivityOrderTypeInfo.this);
                        layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
                }
            }


        }
    }

    /**
     * 处理返回信息
     * @param json
     */
    List<String> lists = new ArrayList<>();
    List<OrderTypeEntry> orderTypeEntryList;
    private void handleData(String json) {
        Log.e("TAG", "状态数据 = " + json);
        Gson gson = new Gson();

                Type type0 = new TypeToken<ArrayList<OrderTypeEntry>>() {
                }.getType();
                orderTypeEntryList = gson.fromJson(json, type0);
                if (orderTypeEntryList != null && orderTypeEntryList.size() > 0) {
                    for (OrderTypeEntry entry : orderTypeEntryList) {
                        lists.add(entry.getTypeName());
                    }
                }


    }
}

