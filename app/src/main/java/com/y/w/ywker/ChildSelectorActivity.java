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
import com.y.w.ywker.entry.ClientConnectEntry;
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
 * Created by lxs on 16/4/14.
 * 通用的二级选择页面
 */
public class ChildSelectorActivity extends BaseSwipeRefreshActivity implements OnCommAdapterItemClickListener {

    private YHttpManagerUtils httpManagerUtils;

    private List<ClientConnectEntry> clientEntryList;
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
        clientId = getIntent().getStringExtra("mainID");
        requestCode = getIntent().getIntExtra("requestcode", -1);
        result = getIntent().getStringExtra("result");
        orderId = getIntent().getStringExtra("orderId");
        olderMsg = getIntent().getStringExtra("oldermsg");
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * 根据requestcode判断是加载客户的联系人还是加载服务商的受理人
     *
     */
    @Override
    public void loadData() {
        String url = "";
        switch (requestCode){
            case ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN:
                    url = String.format(ConstValues.SERVICE_CONNTECT_URL,clientId);
                break;
            case ConstValues.RESULT_SEARCH_CLIENT:
            case ConstValues.RESULT_FOR_PICKER_CLIENT_CHILDREN:
                    url = String.format(ConstValues.CLIENT_CONNECTION_URL,clientId);
                break;
        }
        showLoading();
        /**
         * 构建相应的url访问网络获取数据
         */
        httpManagerUtils = new YHttpManagerUtils(this,url,mHandler,this.getClass().getName());
        layoutCommSwipeRefresh.setRefreshing(true);
        httpManagerUtils.startRequest();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void initTitleView() {
        layoutCommToolbarTitle.setText("选择联系人");
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
            case ConstValues.RESULT_SEARCH_CLIENT:
            case ConstValues.RESULT_FOR_PICKER_CLIENT_CHILDREN:

                ids = clientId + "," + clientEntryList.get(position).getID();
                ids += ",";
                ids += clientEntryList.get(position).getClientCode();
                result += ",";
                result += clientEntryList.get(position).getContactName();

                mRequestCode = ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT;

                break;
            case ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN:
                ids = clientId + "," + servicesConnectEntryList.get(position).getID();
                result += ",";
                result += servicesConnectEntryList.get(position).getUserName();
                mRequestCode = ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT;
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
                        if (orderId != null && !orderId.equals("")){
                            Toast.makeText(ChildSelectorActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        Toast.makeText(ChildSelectorActivity.this, "暂无客户联系人", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        if (orderId != null && !orderId.equals("")){
                            Toast.makeText(ChildSelectorActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        Toast.makeText(ChildSelectorActivity.this, "暂无客户联系人", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST){
                            LOG.e(ChildSelectorActivity.this," 修改值 =  " + (String)msg.obj);
                            Toast.makeText(ChildSelectorActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent();
                            i.putExtra("result",result);
                            i.putExtra("_ids", ids);
                            setResult(mRequestCode, i);
                            finish();
                            break;
                        }
                        switch (requestCode){
                            case ConstValues.RESULT_SEARCH_CLIENT:
                            case ConstValues.RESULT_FOR_PICKER_CLIENT_CHILDREN:
                                handleC((String)msg.obj);
                            break;
                            case ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN:
                                handleS((String)msg.obj);
                                break;
                        }
                        ParentItemAdapter adapter = new ParentItemAdapter(adapterList);
                        adapter.setListener(ChildSelectorActivity.this);
                        layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleC(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ClientConnectEntry>>() {}.getType();
        clientEntryList = gson.fromJson(json, type);
        if (clientEntryList != null && clientEntryList.size() > 0){
            adapterList = new ArrayList<String>();
            for (ClientConnectEntry clientEntry : clientEntryList){
                adapterList.add(clientEntry.getContactName());
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
        LOG.e(this,"修改后的结果 = " + msg);
        httpManagerUtils.setUrl(String.format(ConstValues.ORDER_MODIFY_URL));
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);

        HashMap<String,String> mapModify = new HashMap<String,String>();
        mapModify.put("ID",orderId);
        mapModify.put("MainID",offlineDataManager.getMainID());
        mapModify.put("UserID",offlineDataManager.getUserID());
        mapModify.put("UpdateDime", TimeUtils.getTime(System.currentTimeMillis()));
        mapModify.put("UpdateDetail",msg);

        if (requestCode == ConstValues.RESULT_FOR_PICKER_CLIENT_CHILDREN){
            mapModify.put("UpdateType", "UpdateClient");
            httpManagerUtils.startPostRequest(mapModify);
        }else if(requestCode == ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN){
            mapModify.put("UpdateType", "UpdateClient");
            httpManagerUtils.startPostRequest(mapModify);
        }

        for (String key : mapModify.keySet()){
            LOG.e(this,key + " : " + mapModify.get(key));
        }


    }
}
