package com.y.w.ywker.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.WeiXiuOrderListAdapter;
import com.y.w.ywker.adapters.WeixiuOrderListItemAdapter;
import com.y.w.ywker.entry.AllWeiXiuListEntry;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.WeiXiuListEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.interf.OnOrderListItemListener;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.IphoneTreeView;
import com.y.w.ywker.views.MaterialDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 维修工单列表
 */
public class ActivityWeiXiuOrderListPage extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.order_list_back)
    ImageView orderListBack;
    @Bind(R.id.order_search_btn)
    ImageView orderSearchBtn;
    @Bind(R.id.order_list_recyclerview)
    RecyclerView orderListRecyclerview;
    @Bind(R.id.order_list_empty_view)
    View emptyView;
    @Bind(R.id.expandablelistview)
    IphoneTreeView expandablelistview;
    private String title = "";//工单列表名
    private String ordertype = "";//工单所属列表类型
    private String TAG = "lxs";
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixiuorderlistpage);
        ButterKnife.bind(this);
        ctx = this;
        showLoading();
        initView();
        loadData();
    }

    private void initView() {
        emptyView.setVisibility(View.GONE);
        title = getIntent().getStringExtra("title");
        if(title.equals("所有维修单")){
            orderListRecyclerview.setVisibility(View.GONE);
            expandablelistview.setVisibility(View.VISIBLE);
            Log.e(TAG, "initView: expandablelistview.setVisibility(View.VISIBLE)" );
        }
        ordertype = getIntent().getStringExtra("ordertype");
        layoutCommToolbarTitle.setText(title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderListRecyclerview.setLayoutManager(layoutManager);
    }
    private YHttpManagerUtils httpManagerUtils;
    private void loadData() {
        showLoading();
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        String userId = OfflineDataManager.getInstance(this).getUserID();
        String url = String.format(ConstValues.WEIXIU_ORDER_LIST_URL, mainId, userId, ordertype);//,""
        httpManagerUtils = new YHttpManagerUtils(this, url,
                mHandler, UserEntry.class.getName());
        httpManagerUtils.startRequest();
    }
    private Handler mHandler = new MyHandler(this);
    private MaterialDialog dialogTip;
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
                        dialogTip = new MaterialDialog(ctx)
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
    WeiXiuOrderListAdapter adapter;
    private void parsData(String json) {
        Gson gson = new Gson();
        Log.e(TAG, "parsData: json"+json );
        if(title.equals("所有维修单")){
            Type listType = new TypeToken<List<AllWeiXiuListEntry>>() {
            }.getType();
            final List<AllWeiXiuListEntry> list = gson.fromJson(json,listType);
            if(list != null && list.size() > 0){
                adapter = new WeiXiuOrderListAdapter(ctx,list,expandablelistview);
                expandablelistview.setAdapter(adapter);

                /**
                 * 用当可折叠列表里的子元素(child)被点击的时候被调用的回调方法。
                 参数
                 　　		parent                    发生点击动作的ExpandableListView
                 v                    在expandable list/ListView中被点击的视图(View)
                 groupPosition                   包含被点击子元素的组(group)在ExpandableListView中的位置(索引)
                 childPosition                   被点击子元素(child)在组(group)中的位置
                 id                  被点击子元素(child)的行ID(索引)
                 当点击事件被处理时返回true
                 */
                expandablelistview.setOnChildClickListener(new android.widget.ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {


                       AllWeiXiuListEntry entry =  list.get(groupPosition);
                        String taskId = entry.getNowAcceptTask().get(childPosition).getID()+"";

                        Utils.start_Activity(ActivityWeiXiuOrderListPage.this,WeiXiuOrderDetailActivity.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("taskId",taskId),
                        });
                        finish();
                        return true;
                    }
                });

            }

        }else {
            Type listType = new TypeToken<List<WeiXiuListEntry>>() {
            }.getType();
            List<WeiXiuListEntry> listdata = gson.fromJson(json,listType);
            if(listdata != null && listdata.size() > 0){

                WeixiuOrderListItemAdapter adapter = new WeixiuOrderListItemAdapter(ctx,listdata);
                orderListRecyclerview.setAdapter(adapter);
                adapter.setItemClickListener(new OnOrderListItemListener() {
                    @Override
                    public void onClickOrder(String id, String title, String level, String status) {
                        Utils.start_Activity(ActivityWeiXiuOrderListPage.this,WeiXiuOrderDetailActivity.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("taskId",id),
                        });
                        finish();
                    }
                });
            }
        }
    }

    @OnClick({R.id.order_list_back, R.id.order_search_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_list_back:
                finish();
                break;
            case R.id.order_search_btn:
//                Toast.makeText(this,"该功能暂时未开发",Toast.LENGTH_SHORT).show();
                Utils.start_Activity(this, ActivityOrderSearch.class, new YBasicNameValuePair[]{
                        new YBasicNameValuePair("ordertype", ordertype)
                });
                break;
        }
    }
}
