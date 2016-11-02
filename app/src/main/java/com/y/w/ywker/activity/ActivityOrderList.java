package com.y.w.ywker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tel.qudu.reader.stickyrecyclerviewlibrary.StickyRecyclerHeadersDecoration;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.OrderListItemAdapter;
import com.y.w.ywker.entry.OrderListEntry;
import com.y.w.ywker.entry.OrderListItemGroupEntry;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.interf.OnOrderListItemListener;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MaterialDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/20.
 * 工单列表详情页
 */

public class ActivityOrderList extends SuperActivity implements SwipeRefreshLayout.OnRefreshListener,OnOrderListItemListener {
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.order_list_recyclerview)
    RecyclerView orderListRecyclerview;

    @Bind(R.id.order_list_empty_view)
    View emptyView;
    private String title = "";
    private String ordertype = "";
    private YHttpManagerUtils httpManagerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);
        emptyView.setVisibility(View.GONE);
        title = getIntent().getStringExtra("title");
        ordertype = getIntent().getStringExtra("ordertype");
        layoutCommToolbarTitle.setText(title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderListRecyclerview.setLayoutManager(layoutManager);
//        showLoading();
//        String mainId = OfflineDataManager.getInstance(this).getMainID();
//        String userId = OfflineDataManager.getInstance(this).getUserID();
//        loadData(mainId, userId, ordertype);
    }

    @OnClick({R.id.order_search_btn})
    public void onSearch(){
        Utils.start_Activity(this,ActivityOrderSearch.class,new YBasicNameValuePair[]{
                new YBasicNameValuePair("ordertype",ordertype)
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        showLoading();
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        String userId = OfflineDataManager.getInstance(this).getUserID();
        loadData(mainId, userId, ordertype);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.order_list_back})
    public void onBack(View view) {
        finish();
    }

    /**
     * 加载数据
     */
    private void loadData(String mainID, String userID, String ordertype) {
        String url = String.format(ConstValues.WORK_ORDER_LIST_URL, mainID, userID, ordertype,"");
        httpManagerUtils = new YHttpManagerUtils(ActivityOrderList.this, url,
                mHandler, UserEntry.class.getName());
        httpManagerUtils.startRequest();
    }

    private MaterialDialog dialogTip;
    private Handler mHandler = new MyHandler(this);

    @Override
    public void onRefresh() {
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        String userId = OfflineDataManager.getInstance(this).getUserID();
        loadData(mainId,userId, ordertype);
        if (adapter != null)
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickOrder(String id,String orderTitle,String orderLevel,String orderStatus) {
        Utils.start_Activity(this,ActivityOrderReplayDetails.class,new YBasicNameValuePair[]{
                new YBasicNameValuePair("order_id",id),
                new YBasicNameValuePair("order_title",orderTitle),
                new YBasicNameValuePair("order_level",orderLevel),
                new YBasicNameValuePair("order_status",orderStatus)
        });
    }



    private StickyRecyclerHeadersDecoration headersDecor;

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
                        emptyView.setVisibility(View.VISIBLE);
                        dialogTip = new MaterialDialog(ActivityOrderList.this)
                                .setTitle("提示")
                                .setMessage("获取,是否重试")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
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
                        LOG.e(getBaseContext(), "网络不可用.");
                        break;
                    case ConstValues.MSG_ERROR:
                        emptyView.setVisibility(View.VISIBLE);
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        emptyView.setVisibility(View.VISIBLE);
                        LOG.e(getBaseContext(), "Json 格式不正确.");
                        break;
                    case ConstValues.MSG_SUCESS:
                        emptyView.setVisibility(View.GONE);
                        String json = (String) msg.obj;
                        parsData(json);
                        break;
                }
            }
        }
    }

    private void parsData(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderListEntry>>() {
        }.getType();
        List<OrderListEntry> orderListEntryList = gson.fromJson(json, listType);
        if (orderListEntryList != null && orderListEntryList.size() > 0) {
            List<OrderListItemGroupEntry> lists = new ArrayList<OrderListItemGroupEntry>();
            for (OrderListEntry entry : orderListEntryList) {
                String title = entry.getTypeName();
                int id = entry.getID();
                if (entry.getDetail() != null && entry.getDetail().size() > 0) {
                    /**
                     * 按时间排序
                     */
                    List<OrderListEntry.DetailBean> childDetails =  entry.getDetail();
                    Collections.sort(childDetails, new Comparator<OrderListEntry.DetailBean>() {
                        @Override
                        public int compare(OrderListEntry.DetailBean lhs, OrderListEntry.DetailBean rhs) {
                            long lTime = TimeUtils.getTime(TimeUtils.formatYwkerDate(lhs.getWriteTime()));
                            long rTime = TimeUtils.getTime(TimeUtils.formatYwkerDate(rhs.getWriteTime()));
                            return (int) (rTime - lTime);
                        }
                    });

                    for (OrderListEntry.DetailBean bean : childDetails) {
                        OrderListItemGroupEntry groupEntry = new OrderListItemGroupEntry(bean, title, id);
                        lists.add(groupEntry);
                    }
                }
            }
            if (lists != null && lists.size() > 0) {

                adapter = new OrderListItemAdapter(ActivityOrderList.this, lists);

                if (headersDecor != null){
                    orderListRecyclerview.removeItemDecoration(headersDecor);
                    headersDecor = null;
                }

                headersDecor = new StickyRecyclerHeadersDecoration(adapter);
                orderListRecyclerview.setAdapter(adapter);
                adapter.setItemClickListener(ActivityOrderList.this);
                orderListRecyclerview.addItemDecoration(headersDecor);
                adapter.notifyDataSetChanged();
            }
        }
    }

    OrderListItemAdapter adapter;
}
