package com.y.w.ywker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.activity.SuperActivity;
import com.y.w.ywker.adapters.ShouLiRenListAdapter;
import com.y.w.ywker.entry.PersonEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
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
 * Created by lxs on 16/5/6.
 * 选择受理组  针对新建工单和修改工单的公用界面
 */
public class ActivityPickerTeam extends SuperActivity implements OnCommAdapterItemClickListener {

    @Bind(R.id.btn_back_devices_info)
    ImageView btnBackDevicesInfo;
    @Bind(R.id.expandablelistview)
//    ExpandableListView expandablelistview;
     IphoneTreeView expandablelistview;
    private YHttpManagerUtils httpManagerUtils;
    private int requestCode = -1;
    private String orderId = "";
    private String olderMsg = "";
    private ShouLiRenListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestCode = getIntent().getIntExtra("requestcode", -1);
        orderId = getIntent().getStringExtra("orderId");
        /**
         * 获取修改订单传过来的原始信息
         */
        olderMsg = getIntent().getStringExtra("oldermsg");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_list_item);
        ButterKnife.bind(this);
        loadData();
    }

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
                String string = parentdata.get(groupPosition);
                List<String> list = map.get(string);
                String s = list.get(childPosition);
//                Toast.makeText(ActivityPickerTeam.this, s+"被点击了！！！", Toast.LENGTH_SHORT).show();
                String[] split = s.split(",");
                clientId = split[2]+","+split[3];
                result =split[0]+","+split[1];
                if (orderId != null && !orderId.equals("")) {
                    /**
                     * 修改受理组
                     */
                    modify(clientId);

                } else {
                    setForResult(clientId, result);
                }
//                setForResult(clientId, result);
                return true;
            }
        });

        /**
         * 用当可折叠列表里的组(group)被点击的时候被调用的回调方法。
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

                System.out.println(ActivityPickerTeam.this.parentdata.get(groupPosition)+"被点击了");
                return false;
            }
        });

        /**
         *  每当收缩当前可伸缩列表中的某个组时，就调用该方法。
         参数
         groupPosition 组位置，也就是收缩的那个组的位置。
         */
        expandablelistview.setOnGroupCollapseListener(new android.widget.ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                String s = ActivityPickerTeam.this.parentdata.get(groupPosition);
//                Toast.makeText(ActivityPickerTeam.this, s+"被收缩！！！",Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * 每当展开当前可伸缩列表中的某个组时，就调用该方法。
         　　　　		参数
         groupPosition 组位置，也就是展开的那个组的位置
         */
        expandablelistview.setOnGroupExpandListener(new android.widget.ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                String s = ActivityPickerTeam.this.parentdata.get(groupPosition);
//                Toast.makeText(ActivityPickerTeam.this, s+"被展开！！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 加载数据
     */

    public void loadData() {
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        String url = "";
//        if (requestCode == ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT) {
            url = String.format(ConstValues.SHOULIREN_URL, mainId);
//        }
        showLoading();
        httpManagerUtils = new YHttpManagerUtils(this, url, mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
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

    private String clientId = "";
    private String result = "";

    @Override
    public void onItemClick(int position) {

        switch (requestCode) {
            case ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT:
//                clientId = servicesEntryList.get(position).getID();
//                result = servicesEntryList.get(position).getTeamName();
                break;
        }

        if (orderId != null && !orderId.equals("")) {
            /**
             * 修改受理组
             */
            modify(clientId);

        } else {
            setForResult(clientId, result);
        }
    }


    /**
     * 修改工单
     *
     * @param msg 修改的信息
     */
    private void modify(String msg) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ID", orderId);
        map.put("MainID", OfflineDataManager.getInstance(this).getMainID());
        map.put("UserID", OfflineDataManager.getInstance(this).getUserID());
        map.put("UpdateDime", TimeUtils.getTime(System.currentTimeMillis()));
        map.put("UpdateDetail", msg);
        map.put("UpdateType", "UpdateTeam");

        httpManagerUtils.setUrl(ConstValues.ORDER_MODIFY_URL);
        httpManagerUtils.startPostRequest(map);
    }

    private void setForResult(String ids, String result) {
        Intent intent = new Intent();
        intent.putExtra("_ids", ids);
        intent.putExtra("result", result);
        setResult(requestCode, intent);
        finish();
    }

    @OnClick(R.id.btn_back_devices_info)
    public void onClick() {
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
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ActivityPickerTeam.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:

                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(ActivityPickerTeam.this, "修改成功", Toast.LENGTH_SHORT).show();
                            setForResult(clientId, result);
                        } else {
//                            if (requestCode == ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT) {
                                handleServices((String) msg.obj);
//                            }
                            initlistener();
                            if(adapter != null)
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        }
    }

    private List<PersonEntry> servicesEntryList;
    private List<String> parentdata = new ArrayList<>();
    private HashMap<String, List<String>> map = new HashMap<String, List<String>>();;

    private void handleServices(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<PersonEntry>>() {
        }.getType();
        servicesEntryList = gson.fromJson(json, type);
        if (servicesEntryList != null && servicesEntryList.size() > 0) {
            for (PersonEntry clientEntry : servicesEntryList) {
                String teamName = clientEntry.getTeamName();
                parentdata.add(teamName);
                List<String> list = new ArrayList<String>();
                List<PersonEntry.MemeberBean> memeber = clientEntry.getMemeber();
                for (PersonEntry.MemeberBean bean : memeber) {
                    list.add(bean.getTeamName()+","+bean.getUserName() + "," + bean.getTeamID() + "," + bean.getUserID());
                }
                map.put(teamName,list);
            }
        }
        Log.e("lxs", "handleServices: "+map.size()+parentdata.size() );
        adapter = new ShouLiRenListAdapter(ActivityPickerTeam.this,servicesEntryList,expandablelistview);
        expandablelistview.setAdapter(adapter);
    }
}
