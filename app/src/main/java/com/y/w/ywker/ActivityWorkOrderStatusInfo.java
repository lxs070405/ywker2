package com.y.w.ywker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.adapters.ParentItemAdapter;
import com.y.w.ywker.entry.OrderStatusLevelEntry;
import com.y.w.ywker.entry.OrderTypeEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lxs on 16/4/19.
 *
 * 工单状态详情页,针对新建工单和修改状态  -- 选择工单状态,优先级等
 * 保存选中回调  和  选中修改  两个操作判断
 */
public class ActivityWorkOrderStatusInfo extends BaseSwipeRefreshActivity implements OnCommAdapterItemClickListener {
    String title = "";
    List<String> lists;

    int requestCode = -1;

    YHttpManagerUtils httpManagerUtils;

    List<OrderStatusLevelEntry> orderStatusLevelEntryList;
    List<OrderTypeEntry> orderTypeEntryList;

    String orderId = "";
    String olderMsg = "";
    /**
     * 用于存储最后的结果值,回调给上个页面
     */
    String result = "";
    String teamerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = getIntent().getStringExtra("title");

        /**
         * 根据requestcode判断进行操作
         */

        requestCode = getIntent().getIntExtra("requestcode", -1);
        olderMsg = getIntent().getStringExtra("oldermsg");
        orderId = getIntent().getStringExtra("orderId");
        teamerId = getIntent().getStringExtra("teamerid");

        super.onCreate(savedInstanceState);
        layoutCommSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void loadData() {
        if (requestCode == ConstValues.RESULT_FOR_PICKER_ORDER_LEVEL) {
            loadFromNet(String.format(ConstValues.GET_ORDER_STATUS, "0003"));
        } else if (requestCode == ConstValues.RESULT_FOR_PICKER_ORDER_STATUS) {
            loadFromNet(String.format(ConstValues.GET_ORDER_STATUS, "0001"));
        } else if (requestCode == ConstValues.RESULT_FOR_PICKER_ORDER_TYPES) {
            loadFromNet(String.format(ConstValues.GET_ORDER_TYPE, OfflineDataManager.getInstance(this).getMainID()));
        }
        mHandler.sendEmptyMessage(ConstValues.MSG_SUCESS);
    }

    private void loadFromNet(String url) {
        httpManagerUtils = new YHttpManagerUtils(ActivityWorkOrderStatusInfo.this, url,
                mHandler, getClass().getName());
        httpManagerUtils.startRequest();
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
        layoutCommSwipeRefresh.setRefreshing(false);
    }

    private Handler mHandler = new MyHandler(this);

    @Override
    public void onItemClick(int position) {
        result = lists.get(position);
        String _ids = "";

        switch (requestCode) {
            case ConstValues.RESULT_FOR_PICKER_ORDER_LEVEL://优先级
                if (orderStatusLevelEntryList != null && position < orderStatusLevelEntryList.size()) {
                    _ids = orderStatusLevelEntryList.get(position).getDicCode();
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_STATUS://状态
                if (orderStatusLevelEntryList != null && position < orderStatusLevelEntryList.size()) {
                    for(OrderStatusLevelEntry e : orderStatusLevelEntryList){
                        if (e.getDicName().equals(result)){
                            _ids = e.getDicCode();
                            break;
                        }
                    }
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_TYPES://类型
                if (orderTypeEntryList != null && position < orderTypeEntryList.size()) {
                    _ids = orderTypeEntryList.get(position).getID();
                }
                break;
        }

        if (orderId != null && !orderId.equals("")) {//修改页面

            if (requestCode == ConstValues.RESULT_FOR_PICKER_ORDER_STATUS) {
                if (!result.equals("未受理")) {//非未受理状态
                    if (teamerId == null || teamerId.equals("") || teamerId.equals("无")) {
                        teamerId = "";
                        Toast.makeText(this, "请选择受理人", Toast.LENGTH_SHORT).show();
                    } else {
                        modify(_ids);

                    }
                }else{
                    modify(_ids);
                }
            }else{//非状态选项
                modify(_ids);
            }
        } else {
            setForResult(result, _ids);
        }

    }

    private void setForResult(String result, String ids) {
        Intent i = new Intent();
        i.putExtra("info", result);
        i.putExtra("_ids", ids);
        setResult(requestCode, i);
        finish();
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
                        Toast.makeText(ActivityWorkOrderStatusInfo.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ActivityWorkOrderStatusInfo.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(ActivityWorkOrderStatusInfo.this, "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        Toast.makeText(ActivityWorkOrderStatusInfo.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_SUCESS:

                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(ActivityWorkOrderStatusInfo.this, "修改成功", Toast.LENGTH_SHORT).show();
                            setForResult(result, "");
                        } else {
                            if (lists == null) {
                                lists = new ArrayList<String>();
                            } else {
                                lists.clear();
                            }
                            handleData((String) msg.obj);

                            if (lists != null && !lists.isEmpty()) {
                                if (lists.contains("删除")) {
                                    lists.remove("删除");
                                }
                                if (lists.contains("暂停")) {
                                    lists.remove("暂停");
                                }
                                ParentItemAdapter adapter = new ParentItemAdapter(lists);
                                adapter.setListener(ActivityWorkOrderStatusInfo.this);
                                layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 处理返回信息
     *
     * @param json
     */

    private void handleData(String json) {
        Log.e("TAG", "状态数据 = " + json);
        Gson gson = new Gson();
        switch (requestCode) {
            case ConstValues.RESULT_FOR_PICKER_ORDER_LEVEL:
            case ConstValues.RESULT_FOR_PICKER_ORDER_STATUS:
                Type type = new TypeToken<ArrayList<OrderStatusLevelEntry>>() {
                }.getType();
                orderStatusLevelEntryList = gson.fromJson(json, type);
                if (orderStatusLevelEntryList != null && orderStatusLevelEntryList.size() > 0) {
                    for (OrderStatusLevelEntry entry : orderStatusLevelEntryList) {

                    }
                    for (OrderStatusLevelEntry entry : orderStatusLevelEntryList) {
                        lists.add(entry.getDicName());
                    }
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_TYPES:
                Type type0 = new TypeToken<ArrayList<OrderTypeEntry>>() {
                }.getType();
                orderTypeEntryList = gson.fromJson(json, type0);
                if (orderTypeEntryList != null && orderTypeEntryList.size() > 0) {
                    for (OrderTypeEntry entry : orderTypeEntryList) {
                        lists.add(entry.getTypeName());
                    }
                }
                break;
        }
    }

    /**
     * 修改工单
     * @param msg 修改的信息
     */
    private void modify(String msg) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ID", orderId);
        map.put("MainID", OfflineDataManager.getInstance(this).getMainID());
        map.put("UserID", OfflineDataManager.getInstance(this).getUserID());
        map.put("UpdateDime", TimeUtils.getTime(System.currentTimeMillis()));
        map.put("UpdateDetail", msg);

        switch (requestCode) {
            case ConstValues.RESULT_FOR_PICKER_ORDER_LEVEL://修改优先级
                map.put("UpdateType", "UpdatePriority");
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_STATUS://修改状态
                map.put("UpdateType", "UpdateState");
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_TYPES://修改类型
                map.put("UpdateType", "UpdateSheetType");
                break;
        }

        httpManagerUtils.setUrl(ConstValues.ORDER_MODIFY_URL);
        httpManagerUtils.startPostRequest(map);
    }


}
