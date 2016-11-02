package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.PickerConactsAdapter;
import com.y.w.ywker.entry.ConnectEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/26.
 * 选择联系人界面
 */

public class ActivityPickerConacts extends SuperActivity implements SwipeRefreshLayout.OnRefreshListener, OnCommAdapterItemClickListener {
    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.all_selected_picker_cbx)
    CheckBox allSelectedPickerCbx;

    private YHttpManagerUtils httpManagerUtils;

    private List<ConnectEntry> userEntryList;

    private List<Integer> pickerList = new ArrayList<Integer>();

    /**
     * 工单id
     */
    private String orderId = "";

    private PickerConactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_picker);
        ButterKnife.bind(this);

        orderId = getIntent().getStringExtra("orderId");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);

        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        String title = getIntent().getStringExtra("title");
        layoutCommToolbarTitle.setText(title);
        loadData();
        pickerList.clear();

        allSelectedPickerCbx.setChecked(false);
        allSelectedPickerCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){//全选
                    pickerList.clear();

                    for (int i = 0; i< userEntryList.size();i++){
                        pickerList.add(i);
                    }

                    adapter.selecteAll();

                }else{//清除全选
                    pickerList.clear();
                    adapter.unSelecteAll();
                }
            }
        });

    }

    @OnClick({R.id.btn_picker_conact_bakc})
    public void onBtnBack() {
        finish();
    }

    private String ids = "";
    private String result = "";

    /**
     * 完成选择联系人
     */
    @OnClick({R.id.comm_btn_picker_done})
    public void onPickerDone() {

        for (Integer index : pickerList) {
            ids += userEntryList.get(index).getID();
            ids += ",";
            result += userEntryList.get(index).getUserName();
            result += ",";
        }

        if (!ids.equals("")) {
            ids = ids.substring(0, ids.length() - 1);
        }
        if (!result.equals("")) {
            result = result.substring(0, result.length() - 1);
        }

        if (orderId != null && !orderId.equals("")) {
            modifyOrder(ids);
        } else {
            setForResult(ids, result);
        }
    }

    /**
     * 修改工单修改联系人,选择联系人后点击进行修改
     * @param ids
     */
    private void modifyOrder(String ids) {
        LOG.e(getBaseContext(), "执行修改");
        httpManagerUtils.setUrl(String.format(ConstValues.ORDER_MODIFY_URL));
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);

        /**
         * 构造提交数据的MAP
         */
        HashMap<String, String> mapModify = new HashMap<String, String>();
        mapModify.put("ID", orderId);
        mapModify.put("MainID", offlineDataManager.getMainID());
        mapModify.put("UserID", offlineDataManager.getUserID());
        mapModify.put("UpdateDime", TimeUtils.getTime(System.currentTimeMillis()));
        mapModify.put("UpdateDetail", ids);
        mapModify.put("UpdateType", "UpdateFollow");

        for (String key : mapModify.keySet()) {
            LOG.e(this, key + " : " + mapModify.get(key));
        }

        httpManagerUtils.startPostRequest(mapModify);
    }

    /**
     * 向上一级回调选中的联系人
     * @param ids
     * @param result
     */
    private void setForResult(String ids, String result) {
        Intent i = new Intent();
        i.putExtra("_ids", ids);
        i.putExtra("result", result);
        setResult(ConstValues.RESULT_FOR_PICKER_WATCH_FOR, i);
        finish();
    }

    /**
     * 加载联系人
     */
    public void loadData() {
        showLoading();
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.CONNTECTINON_URL, mainId), mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    /**
     * 重新加载联系人
     */
    @Override
    public void onRefresh() {
        if (httpManagerUtils != null) {
            httpManagerUtils.startRequest();
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

    private Handler mHandler = new MyHandler(this);

    /**
     * 判断是否选中联系人,主要用于选择工单的关注者
     * @param position
     */
    @Override
    public void onItemClick(int position) {

        Integer integer = new Integer(position);
        if (pickerList.contains(integer)) {
            pickerList.remove(integer);
        } else {
            pickerList.add(integer);
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
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(getBaseContext(), "修改数据失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ConstValues.MSG_ERROR:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(getBaseContext(), "修改数据失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(getBaseContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            setForResult(ids, result);
                        } else {
                            handleData((String) msg.obj);
                            if (userEntryList != null && userEntryList.size() > 0) {
                                adapter = new PickerConactsAdapter(userEntryList);
                                adapter.setListener(ActivityPickerConacts.this);
                                layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
                            }
                        }
                        break;
                }
            }
        }
    }

    private void handleData(String json) {
        dismissLoading();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ConnectEntry>>() {
        }.getType();
        userEntryList = gson.fromJson(json, type);
    }

}
