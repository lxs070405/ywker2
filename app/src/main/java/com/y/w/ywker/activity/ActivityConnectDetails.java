package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ActivityOrderTypeInfo;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.ConnectEntry;
import com.y.w.ywker.entry.SerializableMap;
import com.y.w.ywker.entry.UserDetailsEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/5.
 */

public class ActivityConnectDetails extends SuperActivity {
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.connect_details_btn_back)
    ImageView connectDetailsBtnBack;
    @Bind(R.id.connect_details_icon)
    ImageView connectDetailsIcon;
    @Bind(R.id.connect_details_name)
    TextView connectDetailsName;
    @Bind(R.id.connect_details_order_num)
    TextView connectDetailsOrderNum;
    @Bind(R.id.connect_details_role)
    TextView connectDetailsRole;
    @Bind(R.id.connect_details_group)
    TextView connectDetailsGroup;
    @Bind(R.id.connect_details_tel)
    TextView connectDetailsTel;
    @Bind(R.id.connect_details_create_time)
    TextView connectDetailsCreateTime;
    @Bind(R.id.connect_details_send_msg)
    Button connectDetailsSendMsg;
    @Bind(R.id.connect_details_new_order)
    Button connectDetailsNewOrder;

    ConnectEntry entry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connacet_details);
        ButterKnife.bind(this);

        layoutCommToolbarTitle.setText("联系人详情");
        String connect_gson = getIntent().getStringExtra("connect_json");
        if (connect_gson!=null && !connect_gson.equals("")){
            Gson gson = new Gson();
            entry = gson.fromJson(connect_gson, ConnectEntry.class);
        }
        if (entry != null){
            //初始化数据
            loadData();
        }else{
            Toast.makeText(this,"人员信息不存在",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.connect_details_btn_back
    })
    public void onBack(){
        finish();
    }

    @OnClick({R.id.connect_details_send_msg,R.id.connect_details_new_order})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.connect_details_send_msg:
                sendMsg();

                break;
            case R.id.connect_details_new_order:
                createOrder();
                break;
        }
    }

    private void createOrder(){

        if (entry != null){
            SerializableMap sMap = new SerializableMap();
            HashMap<String,String> map = new HashMap<String,String>();
            /**
             * 填充数据
             */
            map.put("from","conntects");
            map.put("TeamID",entry1.getSheetTeamID());
            map.put("AcceptID",entry1.getID() + "");
            map.put("TeamName",entry1.getSheetTeamName());
            map.put("TeamerName", entry1.getUserName());
            sMap.setMap(map);

            Intent i = new Intent(this,ActivityOrderTypeInfo.class);
            i.putExtra("new_order_map",sMap);
            startActivity(i);
        }
    }

    private void sendMsg(){
        if (entry1 == null){
            Toast.makeText(this,"该用户不存在",Toast.LENGTH_SHORT).show();
            return;
        }else{
            Utils.start_Activity(this,ActivityMsgReplay.class,new YBasicNameValuePair[]{
                    new YBasicNameValuePair("send_id",entry1.getID()+""),
                    new YBasicNameValuePair("send_name",entry1.getUserName())
            });
        }

    }

    UserDetailsEntry entry1 = null;

    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity fragment) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null){
                switch (msg.what){
                    case ConstValues.MSG_FAILED:

                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ActivityConnectDetails.this, "获取数据错误", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        handleData((String)msg.obj);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleData(String obj) {
        Type type = new TypeToken<List<UserDetailsEntry>>(){}.getType();
        Gson gson = new Gson();
        List<UserDetailsEntry> _list = gson.fromJson(obj,type);
        if (_list != null && _list.size() > 0){
            entry1 = _list.get(0);
            connectDetailsName.setText(entry1.getUserName());
            connectDetailsOrderNum.setText(entry1.getSheetCount() + "");
            connectDetailsRole.setText(entry1.getUserRole());
            connectDetailsGroup.setText(entry1.getUserTeam());
            connectDetailsTel.setText(entry1.getTel());
            String time = entry1.getCreatTime();
            if (time.contains("Date")){
//                connectDetailsCreateTime.setText(TimeUtils.formatYwkerDate(time));
                connectDetailsCreateTime.setText(TimeUtils.setTime(time));
            }else{
                connectDetailsCreateTime.setText(time);
            }
        }

    }

    private void loadData(){
        httpManagerUtils = new YHttpManagerUtils(this,String.format(ConstValues.GET_CONNECT_DETAILS_URL,entry.getID()),mHandler,this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    private YHttpManagerUtils httpManagerUtils;

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null){
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    private Handler mHandler = new MyHandler(this);
}
