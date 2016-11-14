package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.ParentItemAdapter;
import com.y.w.ywker.entry.ClientEntry;
import com.y.w.ywker.entry.DevicesNameEntry;
import com.y.w.ywker.entry.OrderListEntry;
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
 * Created by lxs on 16/5/9.
 * 设备搜素
 * 客户搜索
 */
public class ActivitySearch extends SuperActivity implements OnCommAdapterItemClickListener {

    /**
     * 设备列表
     */
    List<DevicesNameEntry> devicesList;

    /**
     * 工单列表
     */
    List<OrderListEntry> orderList;

    /**
     * 客户列表
     */
    List<ClientEntry> clientList;

    @Bind(R.id.activity_search_edt)
    EditText activitySearchEdt;
    @Bind(R.id.search_recyclerview)
    RecyclerView searchRecyclerview;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.btn_search_back)
    ImageView btnSearchBack;

    @Bind(R.id.comm_search_empty_view)
    View emptyView;

    private int requestCode = -1;

    /**
     * 常规文本adapter
     */
    private ParentItemAdapter commAdapter;

    /**
     * 搜索设备
     */
    private String pinpaiId = "";
    private String xinghaoId = "";
    private String clientId = "";
    private String connectId = "";
    private String mainId = "";

    /**
     *
     * 搜索客户,可能是修改客户信息
     */

    String orderId = "";

    @OnClick({R.id.btn_search_back,R.id.search_btn_start})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.search_btn_start:
                String text = activitySearchEdt.getText().toString();
                if (TextUtils.isEmpty(text)){
                    Toast.makeText(getBaseContext(),"请输入查询的内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                startSearch(text);
                break;
            case R.id.btn_search_back:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        emptyView.setVisibility(View.GONE);
        /**
         * 搜索类型
         */

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecyclerview.setLayoutManager(layoutManager);

        requestCode = getIntent().getIntExtra("requestcode", -1);

        if (requestCode == ConstValues.RESULT_SEARCH_ORDER) {//工单
            activitySearchEdt.setHint("请输入工单名称");
            Toast.makeText(this, "工单搜索暂不能使用", Toast.LENGTH_SHORT).show();
            layoutCommToolbarTitle.setText("搜索工单");
        } else if (requestCode == ConstValues.RESULT_SEARCH_CLIENT) {//客户
            activitySearchEdt.setHint("请输入客户或客户联系人信息");
            layoutCommToolbarTitle.setText("搜索客户");
        } else if (requestCode == ConstValues.RESULT_SEARCH_DEVIDES) {//设备
            activitySearchEdt.setHint("请输入设备品牌或型号或名称信息");
            layoutCommToolbarTitle.setText("搜索设备");
        }
        mainId = OfflineDataManager.getInstance(this).getMainID();

        /**
         * 处理来自客户选择 两种情况 1- 新建工单  2 - 修改工单
         */

        orderId = getIntent().getStringExtra("orderId");

        /**
         * 处理搜索设备
         */
        clientId = getIntent().getStringExtra("client_id");
        connectId = getIntent().getStringExtra("connect_id");
        pinpaiId = getIntent().getStringExtra("pinpai_id");
        xinghaoId = getIntent().getStringExtra("xinghao_id");


        if (TextUtils.isEmpty(pinpaiId)){
            pinpaiId = "";
        }

        if (TextUtils.isEmpty(xinghaoId)){
            xinghaoId = "";
        }

        activitySearchEdt.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            String msg = activitySearchEdt.getText().toString();
                            if (msg != null && !msg.equals("")) {
                                startSearch(msg);
                                return true;
                            }
                        }
                        return false;
                    }
                });

        activitySearchEdt.addTextChangedListener(watcher);
        activitySearchEdt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String msg = activitySearchEdt.getText().toString();
                    if (msg != null && !msg.equals("")) {
                        startSearch(msg);
                        return true;
                    }
                }
                return false;
            }
        });

    }
    //响应键盘内容
    public TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    /**
     * 开始搜索
     */
    private void startSearch(String searchMsg) {

        String url = "";

        if (requestCode == ConstValues.RESULT_SEARCH_ORDER) {//工单
            return;
        } else if (requestCode == ConstValues.RESULT_SEARCH_CLIENT) {//客户
            url = String.format(ConstValues.CLIENT_SEARCH_URL, mainId,searchMsg);
        } else if (requestCode == ConstValues.RESULT_SEARCH_DEVIDES) {//设备
            url = String.format(ConstValues.GET_DEVICES_NAME_URL,mainId,clientId,connectId,pinpaiId,xinghaoId,searchMsg);
        }
        showLoading();
        httpManagerUtils = new YHttpManagerUtils(this, url, mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    @Override
    public void onItemClick(int position) {
        if (requestCode == ConstValues.RESULT_SEARCH_ORDER) {//工单
            //直接跳转
        } else if (requestCode == ConstValues.RESULT_SEARCH_CLIENT) {//客户
            /**
             * 获取客户主键
             */
            ClientEntry entry = clientList.get(position);
            clientId = entry.getID();
            Intent i = new Intent();
            i.putExtra("_ids",clientId+",");
            i.putExtra("result",entry.getClientName());
            setResult(1, i);
            finish();
//            Utils.start_ActivityResult(this, ChildSelectorActivity.class, requestCode,
//                    new YBasicNameValuePair[]{
//                            new YBasicNameValuePair("mainID", clientId),
//                            new YBasicNameValuePair("orderId", orderId),
//                            new YBasicNameValuePair("result",entry.getClientName() + "," + entry.getClientAdr())
//                    });
        } else if (requestCode == ConstValues.RESULT_SEARCH_DEVIDES) {//设备
            //需要有返回值
            String ids = devicesList.get(position).getID() + "";
            String result = devicesList.get(position).getAssetName();

            Intent i = new Intent();
            i.putExtra("_ids",ids);
            i.putExtra("result",result);
            setResult(requestCode, i);
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

            dismissLoading();
            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(ActivitySearch.this, "未查找到结果", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(ActivitySearch.this, "未查找到结果", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(ActivitySearch.this,"网络不可用",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        emptyView.setVisibility(View.VISIBLE);
                        break;
                    case ConstValues.MSG_SUCESS:
                        emptyView.setVisibility(View.GONE);
                        if (requestCode == ConstValues.RESULT_SEARCH_ORDER){//工单
//                            handleOrderData((String)msg.obj);
                        }else if (requestCode == ConstValues.RESULT_SEARCH_CLIENT){//客户
                            handleClientData((String)msg.obj);
                        }else if(requestCode == ConstValues.RESULT_SEARCH_DEVIDES){//设备
                            handleDevicesData((String) msg.obj);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 处理搜索设备的数据
     * @param obj
     */
    private void handleDevicesData(String obj) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DevicesNameEntry>>() {
        }.getType();

        devicesList = gson.fromJson(obj, type);
        //设置adapter

        if (devicesList != null){
            List<String> _list = new ArrayList<String>();
            for (DevicesNameEntry entry : devicesList){
                _list.add(entry.getAssetName());
            }
            commAdapter = new ParentItemAdapter(_list);
            commAdapter.setListener(this);
            searchRecyclerview.setAdapter(commAdapter);
        }
    }

    /**
     * 处理搜索客户的数据
     * @param obj
     */
    private void handleClientData(String obj) {
        Type type = new TypeToken<List<ClientEntry>>() {
        }.getType();
        Gson gson = new Gson();
        clientList = gson.fromJson(obj,type);
        if (clientList != null && clientList.size() > 0){
            List<String> _list = new ArrayList<String>();
            for (ClientEntry se : clientList){
                _list.add(se.getClientName());
            }
            commAdapter = new ParentItemAdapter(_list);
            commAdapter.setListener(this);
            searchRecyclerview.setAdapter(commAdapter);
        }else{
            Toast.makeText(ActivitySearch.this, "未查找到结果", Toast.LENGTH_SHORT).show();
        }

    }

    private YHttpManagerUtils httpManagerUtils;

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private Handler mHandler = new MyHandler(this);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        setResult(resultCode,data);
        finish();
    }
}
