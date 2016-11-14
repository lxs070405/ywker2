package com.y.w.ywker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.activity.ActivitySearch;
import com.y.w.ywker.adapters.ParentItemAdapter;
import com.y.w.ywker.entry.ClientEntry;
import com.y.w.ywker.entry.ServicesEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by lxs on 16/4/14.
 * 通用一级选择页面
 * 主要是客户,服务组这两个页面的复用
 */

public class ParentSelectorActivity extends BaseSwipeRefreshActivity implements OnCommAdapterItemClickListener {

    private YHttpManagerUtils httpManagerUtils;

    private List<ClientEntry> clientEntryList;
    private List<ServicesEntry> servicesEntryList;

    private List<String> adapterList;

    private int requestCode = -1;
    private String title = "";
    private String orderId = "";
    private String olderMsg = "";
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestCode = getIntent().getIntExtra("requestcode", -1);
        title = getIntent().getStringExtra("title");
        orderId = getIntent().getStringExtra("orderId");
        edit =  getSharedPreferences("lxs", Activity.MODE_PRIVATE).edit();
        /**
         * 获取修改订单传过来的原始信息
         */
        olderMsg = getIntent().getStringExtra("oldermsg");

        super.onCreate(savedInstanceState);

        /**
         * 将搜索按钮设置为可见
         */
        commSearchBtn.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.comm_search_btn})
    public void onSearch(){
        /**
         * 跳转到搜索页面
         */
        Utils.start_ActivityResult(this, ActivitySearch.class, ConstValues.RESULT_SEARCH_CLIENT,
                new YBasicNameValuePair[]{
                        new YBasicNameValuePair("orderId",orderId),
                        new YBasicNameValuePair("oldermsg",olderMsg)
                });
    }

    @Override
    public void loadData() {
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        String url = "";
        if (requestCode == ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT){
            url = String.format(ConstValues.SERVICE_GROUP_URL,mainId);
        }else if(requestCode == ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT){
            url = String.format(ConstValues.CLIENT_GROUP_URL,mainId);
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
        layoutCommToolbarTitle.setText(title);
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

    @Override
    public void onItemClick(int position) {

        /**
         * 判断修改页面还是
         */

        String clientId = "";
        String result = "";
        int mRequestCode = -1;
        switch (requestCode){
            case ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT:
                clientId = clientEntryList.get(position).getID();
                result = clientEntryList.get(position).getClientName();
                edit.putString("ClientCode", clientEntryList.get(position).getClientCode());
                edit.commit();
                result += ",";
                result += clientEntryList.get(position).getClientAdr();
                mRequestCode = ConstValues.RESULT_FOR_PICKER_CLIENT_CHILDREN;
                break;
            case ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT:
                clientId = servicesEntryList.get(position).getID();
                result = servicesEntryList.get(position).getTeamName();
                edit.putString("ClientCode1", clientEntryList.get(position).getClientCode());
                edit.commit();
                mRequestCode = ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN;
                break;
        }
//        if(ConstValues.isnew){
        Intent i = new Intent();
        Log.e("lxs", "onItemClick:一级菜单结果 "+result );
        result = result.split(",")[0];
        i.putExtra("result",result);
        i.putExtra("_ids", clientId + "," + clientEntryList.get(position).getID()+","+clientEntryList.get(position).getClientCode());
        setResult(ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT, i);
        finish();
//        }
//        else {
//            Utils.start_ActivityResult(ParentSelectorActivity.this, ChildSelectorActivity.class, mRequestCode,
//                    new YBasicNameValuePair[]{
//                            new YBasicNameValuePair("mainID", clientId),
//                            new YBasicNameValuePair("result", result),
//                            new YBasicNameValuePair("orderId",orderId),
//                            new YBasicNameValuePair("oldermsg",olderMsg)
//                    });
//        }



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

                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ParentSelectorActivity.this, "服务器返回错误", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:

                        if (requestCode == ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT){
                            handleClient((String)msg.obj);
                        }else if(requestCode == ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT) {
                            handleServices((String) msg.obj);
                        }

                        ParentItemAdapter adapter = new ParentItemAdapter(adapterList);
                        adapter.setListener(ParentSelectorActivity.this);
                        layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
                        if (adapterList != null && olderMsg != null && olderMsg.contains(",")){
                            LOG.e(ParentSelectorActivity.this, "olderMsg = " + olderMsg);
                            String []msgs = olderMsg.split(",");
                            if (msgs != null && msgs.length > 0 && !msgs[0].equals("")){
                                int index = Arrays.binarySearch(adapterList.toArray(),msgs[0]);
                                if (index >=0 ){
                                    adapter.setCurrentSelected(index);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        setResult(resultCode,data);
        finish();
    }

    private void handleClient(String json){
        if (adapterList!=null){
            adapterList.clear();
        }else{
            adapterList = new ArrayList<String>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ClientEntry>>() {}.getType();
        clientEntryList = gson.fromJson(json, type);
        if (clientEntryList != null && clientEntryList.size() > 0){
            for (ClientEntry clientEntry : clientEntryList){
                adapterList.add(clientEntry.getClientName());
            }
        }
    }

    private void handleServices(String json){
        if (adapterList!=null){
            adapterList.clear();
        }else{
            adapterList = new ArrayList<String>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ServicesEntry>>() {}.getType();
        servicesEntryList = gson.fromJson(json, type);
        if (servicesEntryList != null && servicesEntryList.size() > 0){
            for (ServicesEntry clientEntry : servicesEntryList){
                adapterList.add(clientEntry.getTeamName());
            }
        }
    }
}
