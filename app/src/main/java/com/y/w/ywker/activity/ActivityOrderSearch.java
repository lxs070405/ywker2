package com.y.w.ywker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MaterialDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/11.
 * 工单搜索
 */

public class ActivityOrderSearch extends SuperActivity implements SwipeRefreshLayout.OnRefreshListener, OnOrderListItemListener {
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.order_search_list_recyclerview)
    RecyclerView orderListRecyclerview;
    @Bind(R.id.activity_order_search_edt)
    EditText activityOrderSearchEdt;

    @Bind(R.id.order_search_empty_view)
    View emptyView;
    private String title = "工单搜索";
    private String ordertype = "";
    private YHttpManagerUtils httpManagerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_search);
        ButterKnife.bind(this);
        emptyView.setVisibility(View.GONE);
        ordertype = getIntent().getStringExtra("ordertype");
        layoutCommToolbarTitle.setText(title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderListRecyclerview.setLayoutManager(layoutManager);


        activityOrderSearchEdt.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            String msg = activityOrderSearchEdt.getText().toString();
                            if (msg != null && !msg.equals("")) {
                                startSearch(msg);
                                return true;
                            }
                        }
                        return false;
                    }
                });

        activityOrderSearchEdt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String msg = activityOrderSearchEdt.getText().toString();
                    if (msg != null && !msg.equals("")) {
                        startSearch(msg);
                        return true;
                    }
                }
                return false;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.order_search_btn_start, R.id.order_search_list_back})
    public void onBack(View view) {
        switch (view.getId()) {
            case R.id.order_search_btn_start:

                    String text = activityOrderSearchEdt.getText().toString();
                    if (text != null && !text.isEmpty()){
                        startSearch(text);
                    }else{
                        Toast.makeText(this,"请输入工单信息",Toast.LENGTH_SHORT).show();
                    }
                    break;
            case R.id.order_search_list_back:
                finish();
                break;
        }

    }

    /**
     * 搜索函数
     * @param orderInfo 工单ID
     */
    private void startSearch(String orderInfo) {

        if (TextUtils.isEmpty(ordertype)){
            Toast.makeText(this,"工单类型为空",Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 显示加载进度条
         */
        showLoading();

        String mainID = OfflineDataManager.getInstance(this).getMainID();
        String userID = OfflineDataManager.getInstance(this).getUserID();

        String url = String.format(ConstValues.WORK_ORDER_SEARCH_URL, mainID, userID, ordertype,orderInfo);

        /**
         * 请求执行网络搜索
         */

        httpManagerUtils = new YHttpManagerUtils(ActivityOrderSearch.this, url,
                mHandler, UserEntry.class.getName());
        httpManagerUtils.startRequest();
    }

    private MaterialDialog dialogTip;
    private Handler mHandler = new MyHandler(this);

    @Override
    public void onRefresh() {

    }


    /**
     * 进入工单详情页
     * @param id
     * @param orderTitle
     * @param orderLevel
     * @param orderStatus
     */
    @Override
    public void onClickOrder(String id, String orderTitle, String orderLevel, String orderStatus) {
        Utils.start_Activity(this, ActivityOrderReplayDetails.class, new YBasicNameValuePair[]{
                new YBasicNameValuePair("order_id", id),
                new YBasicNameValuePair("order_title", orderTitle),
                new YBasicNameValuePair("order_level", orderLevel),
                new YBasicNameValuePair("order_status", orderStatus)
        });
    }


    /**
     * 悬停header的装饰类
     */
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
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        LOG.e(getBaseContext(), "网络不可用.");
                        emptyView.setVisibility(View.VISIBLE);
                        break;
                    case ConstValues.MSG_ERROR:
                        Log.e("TAG","未搜索到工单结果");
                        emptyView.setVisibility(View.VISIBLE);
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        LOG.e(getBaseContext(), "Json 格式不正确.");
                        emptyView.setVisibility(View.VISIBLE);
                        break;
                    case ConstValues.MSG_SUCESS:

                        emptyView.setVisibility(View.GONE);
                        Log.e("TAG","设置为不可见状态");


                        String json = (String) msg.obj;
                        parseDataJson(json);

                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void parseDataJson(String json) {
        Log.e("TAG",json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderListEntry>>() {
        }.getType();

        /**
         * 解析数据
         */

        List<OrderListEntry> orderListEntryList = gson.fromJson(json, listType);

        if (orderListEntryList != null && orderListEntryList.size() > 0) {

            /**
             * 对数据进行整理并排序,将含有相同标题头的归为一类
             */
            List<OrderListItemGroupEntry> lists = new ArrayList<OrderListItemGroupEntry>();
            for (OrderListEntry entry : orderListEntryList) {
                String title = entry.getTypeName();
                int id = entry.getID();
                if (entry.getDetail() != null && entry.getDetail().size() > 0) {
                    for (OrderListEntry.DetailBean bean : entry.getDetail()) {
                        OrderListItemGroupEntry groupEntry = new OrderListItemGroupEntry(bean, title, id);
                        lists.add(groupEntry);
                    }
                }
            }
            if (lists != null && lists.size() > 0) {
                /**
                 * 按时间排序
                 */
//                Collections.sort(lists, new Comparator<OrderListItemGroupEntry>() {
//                    @Override
//                    public int compare(OrderListItemGroupEntry lhs, OrderListItemGroupEntry rhs) {
//                        long lTime = TimeUtils.getTime(TimeUtils.formatYwkerDate(lhs.getWriteTime()));
//                        long rTime = TimeUtils.getTime(TimeUtils.formatYwkerDate(rhs.getWriteTime()));
//                        return (int) (rTime - lTime);
//                    }
//                });

                /**
                 * 创建工单Adapter
                 */
                OrderListItemAdapter adapter = new OrderListItemAdapter(ActivityOrderSearch.this, lists);

                /**
                 * 避免添加多个headerItemDec
                 */
                if (headersDecor != null) {
                    orderListRecyclerview.removeItemDecoration(headersDecor);
                    headersDecor = null;
                }
                /**
                 * 多Recyclerview添加itemDec
                 */
                headersDecor = new StickyRecyclerHeadersDecoration(adapter);
                orderListRecyclerview.setAdapter(adapter);
                adapter.setItemClickListener(ActivityOrderSearch.this);
                orderListRecyclerview.addItemDecoration(headersDecor);
            }else{
                Log.e("TAG","设置为可见");
                emptyView.setVisibility(View.VISIBLE);
            }
        }else{
            Log.e("TAG","设置为可见");
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}
