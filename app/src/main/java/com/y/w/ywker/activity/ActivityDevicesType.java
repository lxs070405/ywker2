package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.DevicesTypeListAdapter;
import com.y.w.ywker.entry.DevicesTypeEntry;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.IphoneTreeView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by LXS on 2016/8/26.
 * 设备类型页面
 */
public class ActivityDevicesType extends SuperActivity {


    @Bind(R.id.expandablelistview)
    IphoneTreeView expandablelistview;
    private String isweixiu = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicestype);
        ButterKnife.bind(this);
        isweixiu = getIntent().getStringExtra("weixiu");
        showLoading();
         initData();
         initlistener();
    }



    private YHttpManagerUtils httpManagerUtils;
    private void initData() {
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        String mainId = offlineDataManager.getMainID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_DEVICETYPE_URL,mainId), new MyHandler(this,1), this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    @OnClick(R.id.btn_back_devices_info)
    public void onClick() {
        finish();
    }

//    private Handler mHandler = new MyHandler(this);
    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        int type = 0;
        public MyHandler(AppCompatActivity fragment,int type) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.type = type;
        }

        @Override
        public void handleMessage(Message msg) {
            dismissLoading();
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ActivityDevicesType.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(msg.obj != null){
                            if(type == 1){
                                String data = (String)msg.obj;
                                parseData(data);
                            }

                        }
                        break;
                }
            }
        }
    }
    private List<DevicesTypeEntry> servicesEntryList;
    private List<String> parentList = new ArrayList<>();
//    private HashMap<String,Integer> parentdata = new HashMap<>();
    private HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();;
    private void parseData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DevicesTypeEntry>>() {
        }.getType();
        servicesEntryList = gson.fromJson(json, type);
        if (servicesEntryList != null && servicesEntryList.size() > 0) {
            for (DevicesTypeEntry clientEntry : servicesEntryList) {
                String teamName = clientEntry.getTypeName();
                Integer teamID = clientEntry.getID();
                parentList.add(teamName);
//                parentdata.put(teamName,teamID);
                ArrayList<String> list = new ArrayList<String>();
                List<DevicesTypeEntry.ChildNodeBean> memeber = clientEntry.getChildNode();
                for (DevicesTypeEntry.ChildNodeBean bean : memeber) {
                    list.add(clientEntry.getTypeName()+","+bean.getTypeName() + "," + clientEntry.getID() + "," + bean.getID());
                }
                map.put(teamName,list);
            }
        }
//        Log.e("lxs", "handleServices: "+map.size()+parentdata.size() );
        adapter = new DevicesTypeListAdapter(ActivityDevicesType.this,servicesEntryList,expandablelistview);
        expandablelistview.setAdapter(adapter);
    }
    private DevicesTypeListAdapter adapter;
    private void initlistener() {


        /**
         * 用当可折叠列表里的子元素(child)被点击的时候被调用的回调方法。
         参数
         　　		parent                    发生点击动作的ExpandableListView
         v                    在expandable list/ListView中被点击的视图(View)
         groupPosition                   包含被点击子元素的组(group)在ExpandableListView中的位置(索引)
         childPosition                   被点击子元素(child)在组(group)中的位置
         id                  被点击子元素(child)的行ID(索引)
         　　　　		返回值
         当点击事件被处理时返回true
         */
        expandablelistview.setOnChildClickListener(new android.widget.ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String string = parentList.get(groupPosition);
                List<String> list = map.get(string);
                String s = list.get(childPosition);
                Intent intent = new Intent(ActivityDevicesType.this,DevicesInfoActivity.class);
                String[] str = s.split(",");
                intent.putExtra("_ids",str[str.length-1]);
                intent.putExtra("result",str[1]);
                Log.e("lxs", "设备类型页面onChild:id--->"+str[str.length-1]+",name--->"+str[1]  );
                setResult(ConstValues.RESULT_FOR_DEVICES_TYPE,intent);
                finish();
                return true;
            }
        });

        /**
         * 当可折叠列表里的组(group)被点击的时候被调用的回调方法。
         参数
         　　　　　　parent            发生点击事件的ExpandableListConnector
         　　　　　　v                    在expandable list/ListView中被点击的视图(View)
         　　　　　　groupPosition  被点击的组(group)在ExpandableListConnector中的位置(索引)
         　　　　　　id                   被点击的组(group)的行ID(索引)
         　　　　     返回值
         　　　　　　当点击事件被处理的时候返回true
         */
        expandablelistview.setOnGroupClickListener(new android.widget.ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if("lxs".equals(isweixiu)){
                    String s =ActivityDevicesType.this.parentList.get(groupPosition);
                    ArrayList<String> childdate = map.get(s);
                        Intent intent = new Intent(ActivityDevicesType.this,DevicesInfoActivity.class);
                        intent.putExtra("_ids",servicesEntryList.get(groupPosition).getID()+"");
                        intent.putExtra("result",s);
                        setResult(ConstValues.RESULT_FOR_DEVICES_TYPE,intent);
                        finish();
                        return true;
                }else {
                    String s =ActivityDevicesType.this.parentList.get(groupPosition);
                    ArrayList<String> childdate = map.get(s);
                    if(childdate.size() == 1||childdate.size() == 0){
                        Intent intent = new Intent(ActivityDevicesType.this,DevicesInfoActivity.class);
                        intent.putExtra("_ids",servicesEntryList.get(groupPosition).getID()+"");
                        intent.putExtra("result",s);
                        setResult(ConstValues.RESULT_FOR_DEVICES_TYPE,intent);
                        finish();
                        return true;
                    }
                    return false;
                }

            }
        });
        /**
         *  每当收缩当前可伸缩列表中的某个组时，就调用该方法。
         groupPosition 组位置，也就是收缩的那个组的位置。
         */
        expandablelistview.setOnGroupCollapseListener(new android.widget.ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                String s = ActivityDevicesType.this.parentList.get(groupPosition);
            }
        });
        /**
         * 每当展开当前可伸缩列表中的某个组时，就调用该方法。
         groupPosition 组位置，也就是展开的那个组的位置
         */
        expandablelistview.setOnGroupExpandListener(new android.widget.ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                String s =ActivityDevicesType.this.parentList.get(groupPosition);
            }
        });
    }
}
