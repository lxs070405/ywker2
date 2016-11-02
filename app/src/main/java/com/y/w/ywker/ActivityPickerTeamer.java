package com.y.w.ywker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.adapters.ParentItemAdapter;
import com.y.w.ywker.entry.ServicesConnectEntry;
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

/**
 * Created by lxs on 16/5/6.
 * 选择受理人 针对新建工单和修改工单的公用页面
 */
public class ActivityPickerTeamer extends BaseSwipeRefreshActivity implements OnCommAdapterItemClickListener {

    private YHttpManagerUtils httpManagerUtils;

    private List<ServicesConnectEntry> servicesConnectEntryList;
    private List<String> adapterList;

    String clientId = "";
    private int requestCode = -1;
    private String result = "";
    private String ids = "";
    private String orderId = "";
    private String olderMsg = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 初始化接收到的数据
         */
        clientId = getIntent().getStringExtra("mainID");
        requestCode = getIntent().getIntExtra("requestcode", -1);
        result = getIntent().getStringExtra("result");
        orderId = getIntent().getStringExtra("orderId");
        olderMsg = getIntent().getStringExtra("oldermsg");
        super.onCreate(savedInstanceState);
    }

    /**
     * 加载受理人数据
     */
    @Override
    public void loadData() {
        String url = "";
        switch (requestCode){
            case ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN:
                url = String.format(ConstValues.SERVICE_CONNTECT_URL,clientId);
                break;
        }
        showLoading();
        httpManagerUtils = new YHttpManagerUtils(this,url,mHandler,this.getClass().getName());
        layoutCommSwipeRefresh.setRefreshing(true);
        httpManagerUtils.startRequest();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void initTitleView() {
        layoutCommToolbarTitle.setText("选择受理人");
    }

    @Override
    public void onRefresh() {
        if (httpManagerUtils != null){
            httpManagerUtils.startRequest();
        }
    }

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null){
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    private Handler mHandler = new MyHandler(this);
    private int mRequestCode = -1;

    @Override
    public void onItemClick(int position) {
        /**
         * 选择数据
         */
        ids = "";
        switch (requestCode){
            case ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN:
                ids = servicesConnectEntryList.get(position).getUserID();
                result = servicesConnectEntryList.get(position).getUserName();
                mRequestCode = ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN;
                break;
        }

        if (orderId != null && olderMsg != null && !result.equals(olderMsg)){
            //进行修改
            LOG.e(this, "oldermsg = " + olderMsg + ",result = " + result);
            modifyOrder(ids);
        }else{
            Intent i = new Intent();
            i.putExtra("result",result);
            i.putExtra("_ids", ids);
            setResult(mRequestCode, i);
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

            if (fragment != null){
                layoutCommSwipeRefresh.setRefreshing(false);
                switch (msg.what){
                    case ConstValues.MSG_FAILED:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST){
                            if (orderId != null){
                                Toast.makeText(ActivityPickerTeamer.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }else{
                            Toast.makeText(ActivityPickerTeamer.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case ConstValues.MSG_ERROR:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST){
                            if (orderId != null){
                                Toast.makeText(ActivityPickerTeamer.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }else{
                            Toast.makeText(ActivityPickerTeamer.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(ActivityPickerTeamer.this, "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST){
                            Toast.makeText(ActivityPickerTeamer.this,"修改成功",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent();
                            i.putExtra("result",result);
                            i.putExtra("_ids", ids);
                            setResult(mRequestCode, i);
                            finish();
                            break;
                        }else{
                            switch (requestCode){
                                case ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN:
                                    handleS((String)msg.obj);
                                    break;
                            }
                            /**
                             * 设置受理人adapter
                             */
                            ParentItemAdapter adapter = new ParentItemAdapter(adapterList);
                            adapter.setListener(ActivityPickerTeamer.this);
                            layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
                        }

                        break;
                    default:
                        break;
                }
            }
        }
    }


    private void handleS(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ServicesConnectEntry>>() {}.getType();
        servicesConnectEntryList = gson.fromJson(json, type);
        if (servicesConnectEntryList != null && servicesConnectEntryList.size() > 0){
            adapterList = new ArrayList<String>();
            for (ServicesConnectEntry clientEntry : servicesConnectEntryList){
                adapterList.add(clientEntry.getUserName());
            }
        }
    }

    private void modifyOrder(String msg){
        httpManagerUtils.setUrl(String.format(ConstValues.ORDER_MODIFY_URL));
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);

        HashMap<String,String> mapModify = new HashMap<String,String>();
        mapModify.put("ID",orderId);
        mapModify.put("MainID",offlineDataManager.getMainID());
        mapModify.put("UserID",offlineDataManager.getUserID());
        mapModify.put("UpdateDime", TimeUtils.getTime(System.currentTimeMillis()));
        mapModify.put("UpdateDetail",msg);
        mapModify.put("UpdateType","UpdateAccept");

        for (String key : mapModify.keySet()){
            LOG.e(this,key + " : " + mapModify.get(key));
        }

        httpManagerUtils.startPostRequest(mapModify);

    }

}
