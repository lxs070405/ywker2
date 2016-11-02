package com.y.w.ywker.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.AdapterPickerDevices;
import com.y.w.ywker.entry.DevicesInfoCommEntry;
import com.y.w.ywker.entry.DevicesNameEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/4.
 * 绑定设备页面
 */
public class ActivityPickerDevices extends SuperActivity implements SwipeRefreshLayout.OnRefreshListener, OnCommAdapterItemClickListener {
    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.devices_search_btn)
    ImageView devicesSearchBtn;

    private YHttpManagerUtils httpManagerUtils;
    //获取的类型  型号,品牌
    private String decives_type = "";
    //客户ID
    private String clientId = "";
    //客户联系人ID
    private String connectId = "";

    private String pinpaiId = "";

    private String xinghaoId = "";

    //startActivityForResult code
    private int requestCode = -1;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_devies);
        ButterKnife.bind(this);
        requestCode = getIntent().getIntExtra("picker_devices", -1);
        decives_type = getIntent().getStringExtra("devices_type");
        clientId = getIntent().getStringExtra("client_id");
        connectId = getIntent().getStringExtra("connect_id");
        pinpaiId = getIntent().getStringExtra("pinpai_id");
        xinghaoId = getIntent().getStringExtra("xinghao_id");
        SharedPreferences sp = getSharedPreferences("lxs", Activity.MODE_PRIVATE);
        edit = sp.edit();
        edit.putString("ContactID", connectId);
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

        /**
         * 加载品牌型号
         */
        if (requestCode == ConstValues.RESULT_FOR_DEVICES_PINPAI ||
                requestCode == ConstValues.RESULT_FOR_DEVICES_XINGHAO) {
            devicesSearchBtn.setVisibility(View.GONE);
            loadDataPinPaiXingHao();
        } else {
            devicesSearchBtn.setVisibility(View.VISIBLE);
            //加载设备名称
            loadDataDevicesNames();
        }
    }

    @OnClick({R.id.btn_picker_devices_bakc})
    public void onBtnBack() {
        finish();
    }

    /**
     * 加载品牌型号
     */
    public void loadDataPinPaiXingHao() {
        if (TextUtils.isEmpty(connectId) || TextUtils.isEmpty(clientId) ||
                TextUtils.isEmpty(decives_type)) {
            Toast.makeText(this, "无法获取设备信息,请检测是否选择了客户和联系人等信息", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(
                ConstValues.GET_DEVICES_INFO_URL, mainId, clientId, connectId, decives_type),
                mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }


    /**
     * 加载设备名称
     */
    public void loadDataDevicesNames() {
        if (TextUtils.isEmpty(connectId) || TextUtils.isEmpty(clientId)) {
            Toast.makeText(this, "无法获取设备信息,请检测是否选择了客户和联系人等信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pinpaiId)) {
            pinpaiId = "0";
        }
        if (TextUtils.isEmpty(xinghaoId)) {
            xinghaoId = "0";
        }
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(
                ConstValues.GET_DEVICES_NAME_URL, mainId, clientId, connectId, pinpaiId, xinghaoId, ""),
                mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    /**
     * 开始搜索
     */
    @OnClick({R.id.devices_search_btn})
    public void onSearch() {
        startActivitySearch(ConstValues.RESULT_SEARCH_DEVIDES, clientId, connectId, pinpaiId, xinghaoId);
    }

    private void startActivitySearch(int requestCode, String clientId, String connectId,
                                     String pinpaiid, String xinghaoid) {

        if (TextUtils.isEmpty(pinpaiId)) {
            pinpaiId = "0";
        }

        if (TextUtils.isEmpty(xinghaoId)) {
            xinghaoId = "0";
        }

        Intent i = new Intent(this, ActivitySearch.class);
        i.putExtra("requestcode", requestCode);
        i.putExtra("client_id", clientId);
        i.putExtra("connect_id", connectId);
        i.putExtra("pinpai_id", pinpaiid);
        i.putExtra("xinghao_id", xinghaoid);
        startActivityForResult(i, requestCode);
    }

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

    private  String XingHao = "";
    private  String PinPai = "";
    private  String AseetName = "";
    @Override
    public void onItemClick(int position) {
        String ids = "";
        String result = "";
        switch (requestCode) {
            case ConstValues.RESULT_FOR_DEVICES_XINGHAO:
                ids = entryLists.get(position).getID() + "";
                result = entryLists.get(position).getName();
                XingHao = result;
                break;
            case ConstValues.RESULT_FOR_DEVICES_PINPAI:
                ids = entryLists.get(position).getID() + "";
                result = entryLists.get(position).getName();
                PinPai = result;
                break;
            case ConstValues.RESULT_FOR_DEVICES_NAME:
                ids = namesList.get(position).getID() + "";
                result = namesList.get(position).getAssetName();
                AseetName = result;
                break;
        }

        Intent i = new Intent();
        i.putExtra("_ids", ids);
        i.putExtra("result", result);
        setResult(requestCode, i);
        finish();
    }

    @OnClick(R.id.btn_chuangjian)
    public void onClick() {//创建
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        Intent intent = new Intent(this,ActivityNewAseet.class);
        intent.putExtra("XingHao",XingHao);
        intent.putExtra("PinPai",PinPai);
        intent.putExtra("AseetName",AseetName);
        intent.putExtra("ContactID",connectId);
        startActivity(intent);
        finish();
    }

    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity fragment) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            dismissLoading();
            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null) {
                layoutCommSwipeRefresh.setRefreshing(false);
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        Toast.makeText(getBaseContext(), "暂无设备", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(getBaseContext(), "暂无设备", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(getBaseContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        switch (requestCode) {
                            case ConstValues.RESULT_FOR_DEVICES_PINPAI:
                            case ConstValues.RESULT_FOR_DEVICES_XINGHAO:
                                handleData((String) msg.obj);
                                break;
                            case ConstValues.RESULT_FOR_DEVICES_NAME:
                                handleNamesData((String) msg.obj);
                                break;
                        }
                        break;
                }
            }
        }
    }

    private List<DevicesInfoCommEntry> entryLists;
    private List<DevicesNameEntry> namesList;

    /**
     * 处理设备型号和厂商
     * @param json
     */
    private void handleData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DevicesInfoCommEntry>>() {
        }.getType();
        entryLists = gson.fromJson(json, type);
        //设置adapter
        if (entryLists != null) {
            List<String> _list = new ArrayList<String>();
            for (DevicesInfoCommEntry entry : entryLists) {
                _list.add(entry.getName());
            }
            AdapterPickerDevices adapter = new AdapterPickerDevices(_list);
            adapter.setListener(this);
            layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
        }
    }

    /**
     * 处理设备名称
     *
     * @param json
     */
    private void handleNamesData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DevicesNameEntry>>() {
        }.getType();
        namesList = gson.fromJson(json, type);
        //设置adapter
        if (namesList != null) {
            List<String> _list = new ArrayList<String>();
            for (DevicesNameEntry entry : namesList) {
                _list.add(entry.getAssetName());
            }
            AdapterPickerDevices adapter = new AdapterPickerDevices(_list);
            adapter.setListener(this);
            layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            setResult(resultCode, data);
            finish();
        }
    }
}
