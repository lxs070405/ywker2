package com.y.w.ywker.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.service.LocationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ActivityPickerTeam;
import com.y.w.ywker.ActivityWorkOrderStatusInfo;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.ParentSelectorActivity;
import com.y.w.ywker.R;
import com.y.w.ywker.YwkerApplication;
import com.y.w.ywker.entry.ConnectEntry;
import com.y.w.ywker.entry.SerializableMap;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MaterialDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/13.
 * 新建工单
 */

public class NewWorkOrderActivity extends SuperActivity implements View.OnClickListener {

    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.new_order_back)
    ImageView newOrderBack;
    @Bind(R.id.new_order_public)
    TextView newOrderPublic;
    @Bind(R.id.new_order_arrrow1_text1)
    TextView newOrderArrrow1Text1;
    @Bind(R.id.new_order_arrrow1_text2)
    TextView newOrderArrrow1Text2;
    @Bind(R.id.new_order_arrrow2_text1)
    TextView newOrderArrrow2Text1;
    @Bind(R.id.new_order_arrrow2_text2)
    TextView newOrderArrrow2Text2;
    @Bind(R.id.new_order_arrrow3_text1)
    TextView newOrderArrrow3Text1;
    @Bind(R.id.new_order_arrrow3_text2)
    TextView newOrderArrrow3Text2;
    @Bind(R.id.new_order_arrrow4_text1)
    TextView newOrderArrrow4Text1;
    @Bind(R.id.new_order_arrrow4_text2)
    TextView newOrderArrrow4Text2;
    @Bind(R.id.new_order_arrrow5_text1)
    TextView newOrderArrrow5Text1;
    @Bind(R.id.new_order_arrrow5_text2)
    TextView newOrderArrrow5Text2;
    @Bind(R.id.new_order_arrrow6_text2)
    TextView newOrderArrrow6Text2;
    @Bind(R.id.new_order_arrrow7_text2)
    TextView newOrderArrrow7Text2;
    @Bind(R.id.new_order_arrrow8_text2)
    TextView newOrderArrrow8Text2;
    @Bind(R.id.new_order_arrrow9_text2)
    TextView newOrderArrrow9Text2;
    @Bind(R.id.titledsc)
    TextView titledsc;
    String keyManId = "MainID";
    String keyClientConactId = "ContactID";
    String keyClientId = "ClientID";//：客户主键
    String keyWriteId = "WriteID";//：发布人主键，及登录人主键
    String keyWriteTime = "WriteTime";//：发布时间
    String keyWriteAddr = "WriteAdr";//：发布时所处地点
    String keySheetType = "SheetType";//：工单类型主键
    String keySheetTitle = "SheetTitle";//：主题
    String keySheetDetails = "SheetDetail";//：描述
    String keySheetLevel = "SheetPriority";//：优先级
    String keySheetState = "SheetState";//：当前状态
    String keyTeamId = "TeamID";//：服务组主键
    String keyTeamerId = "AcceptID";//：受理人主键
    String keyFollowId = "FollowID";//：关注人主键，以逗号隔开
    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.new_order_l_1)
    LinearLayout newOrderL1;
    @Bind(R.id.new_order_l_2)
    LinearLayout newOrderL2;
    @Bind(R.id.new_order_client_adress_text2)
    TextView newOrderClientAdressText2;
    @Bind(R.id.new_order_l_client_adress)
    LinearLayout newOrderLClientAdress;
    @Bind(R.id.new_order_client_connect_text2)
    TextView newOrderClientConnectText2;
    @Bind(R.id.new_order_l_client_connect)
    LinearLayout newOrderLClientConnect;
    @Bind(R.id.new_order_l_3)
    LinearLayout newOrderL3;
    @Bind(R.id.new_order_l_4)
    LinearLayout newOrderL4;
    @Bind(R.id.new_order_l_5)
    LinearLayout newOrderL5;
    @Bind(R.id.new_order_l_6)
    LinearLayout newOrderL6;
    @Bind(R.id.new_order_l_7)
    LinearLayout newOrderL7;
    @Bind(R.id.new_order_l_8)
    LinearLayout newOrderL8;
    @Bind(R.id.new_order_l_9)
    LinearLayout newOrderL9;
    @Bind(R.id.et_SuerName)
    EditText etSuerName;
    @Bind(R.id.et_SureConacts)
    EditText etSureConacts;
    @Bind(R.id.et_desc)
    EditText etDesc;

    /**
     * 定位服务
     */
    private LocationService locationService;
    private String locationArr = "";
    private String time = "";

    /**
     * 用于提交工单使用的HashMap
     */
    private HashMap<String, String> map = new HashMap<String, String>();
    private List<Integer> pickerList = new ArrayList<Integer>();
    private String ids ="";
    @OnClick({R.id.new_order_public})
    public void onPublic(View view) {
        map.put(keySheetDetails, etDesc.getText().toString().trim());


        String lianxiren = etSuerName.getText().toString();
        if (TextUtils.isEmpty(lianxiren)) {
            Toast.makeText(this, "请填写客户联系人", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = etSureConacts.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请填写联系人电话", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 判断描述
         */
        if (map.get(keySheetDetails).equals("")) {
            Toast.makeText(this, "请填写描述", Toast.LENGTH_SHORT).show();
            return;
        }
//        if(!CheckUtils.isPhoneNumberValid(phone)){
//            Toast.makeText(this,"请填写正确的电话号码",Toast.LENGTH_SHORT).show();
//            return;
//        }
        /**
         * 如果是工单状态未受理,有没有选择受理人
         */
        if (!newOrderArrrow4Text2.getText().toString().equals("未受理")) {
            if (map.get(keyTeamerId).equals("")) {
                Toast.makeText(getBaseContext(), "请选择受理人", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        /**
         * 判断客户
         */
        if (newOrderArrrow3Text2.getText().toString().isEmpty()) {
            Toast.makeText(this, "请填写客户信息", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 设置必要的时间和发单人
         * 时间格式:2016/4/20 17:26:17
         */
        long time = System.currentTimeMillis();
        map.put(keyWriteTime, TimeUtils.getTime(time));
        /**
         * 然后提交
         */
        map.put(keyWriteAddr, locationArr);

        map.put(keyWriteId, OfflineDataManager.getInstance(this).getUserID());

        map.put(keyManId, OfflineDataManager.getInstance(this).getMainID());
        map.put("LinkName", lianxiren);
        map.put("LinkTel", phone);
        map.put("DataType","1");
        for (String key : map.keySet()) {
            LOG.e(getBaseContext(), key + " : " + map.get(key));
        }
        Gson gson = new Gson();
        UserEntry entery = gson.fromJson(OfflineDataManager.getInstance(this).getUser(), UserEntry.class);
//        if(userEntryList != null &&userEntryList.size()>0){
//            for (int i = 0; i < userEntryList.size(); i++) {
////                if(userId.equals(userEntryList.get(i).getID())){
////                    continue;
////                }
//                ids += userEntryList.get(i).getID() ;
//                ids += ",";
//            }
//            map.put(keyFollowId, ids);
//        }
        String str = newOrderArrrow5Text2.getText().toString();
        String title = "";
//        if (str.equals("默认")) {
//            title = entery.getUserName() + "发布了" + "故障类型的工单("
//                    + TimeUtils.getPublicOrderTitleTime(time) + ")";
//            map.put(keySheetType, "3");
//        } else {
        title = entery.getUserName() + "发布了" + type + "类型的工单("
                + TimeUtils.getPublicOrderTitleTime(time) + ")";
//        }
        map.put(keySheetTitle, title);
        map.put("AssetID",AssetID);
        proDialog = new ProgressDialog(this);
        proDialog.setTitle("提示");
        proDialog.setMessage("正在发布");
        proDialog.show();
        if( proDialog.isShowing()){
            publicOrder(map);
        }
    }

    private ProgressDialog proDialog;

    /**
     * 发布工单
     */
    private void publicOrder(HashMap map) {
        YHttpManagerUtils managerUtils = new YHttpManagerUtils(NewWorkOrderActivity.this, ConstValues.NEW_WORK_ORDER_URL, mHandler, getClass().getName());
        managerUtils.startPostRequest(map);
    }

    /**
     * 获取定位信息
     */
    @Override
    public void onStart() {
        super.onStart();
        locationService = ((YwkerApplication) (getApplication())).locationService;
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    /**
     * 关闭定位
     */
    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    private String type;
    private String AssetID = "0";
    private YHttpManagerUtils httpManagerUtils;
    private  String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this);
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        userId =  OfflineDataManager.getInstance(this).getUserID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.CONNTECTINON_URL, mainId), new MyHandler(this,2), this.getClass().getName());
        httpManagerUtils.startRequest();
        type = getIntent().getStringExtra("Typename");

        String typeid = getIntent().getStringExtra("TypeId");
        if (typeid == null && type == null) {
            typeid = "03";
            type = "故障";
        }
        titledsc.append(" : " + type);
        //初始化map
        map.put(keyFollowId, String.valueOf(""));
        map.put(keyTeamId, String.valueOf(""));
        map.put(keyManId, String.valueOf(""));
        map.put(keyClientConactId, String.valueOf(""));
        map.put(keySheetTitle, String.valueOf(""));
        map.put(keySheetDetails, String.valueOf(""));
        map.put(keySheetLevel, "03");
        map.put(keySheetState, "1");
        map.put(keySheetType, typeid);
        map.put(keyTeamerId, String.valueOf(""));
        map.put(keyTeamId, String.valueOf(""));
        map.put(keyWriteAddr, String.valueOf(""));
        map.put(keyWriteId, String.valueOf(""));
        map.put(keyWriteAddr, String.valueOf(""));
        map.put(keyWriteTime, String.valueOf(""));

        /**
         * 默认情况下 类型 - 任务 ,优先级 -- 标准 ,状态 -- 未受理
         */

//        newOrderArrrow4Text2.setText("未受理");
//        newOrderArrrow5Text2.setText("默认");
//        newOrderArrrow6Text2.setText("标准");

        /**
         * 先将客户地址和客户联系人隐藏
         */

        newOrderLClientAdress.setVisibility(View.GONE);
        newOrderLClientConnect.setVisibility(View.GONE);

        /**
         * 将传过来的值赋值
         * 能传过来的值有几种情况
         * 1 从设备创建工单   客户,客户联系人
         * 2 从联系人创建工单 受理组,受理人,名称
         * 3 正常手段创建工单 null
         * 规定map的键值对
         * from -- devices
         * ClientID：客户主键
         * ContactID：客户联系人主键
         * ClientName
         * ClientAdress
         * ContactName
         * from -- conntects
         * TeamID：服务组主键
         * AcceptID：受理人主键
         * TeamName
         * TeamerName
         */

        SerializableMap outMap = (SerializableMap) getIntent().getSerializableExtra("new_order_map");
        if (outMap != null) {
             _outMap = outMap.getMap();
            if (_outMap.get("from").equals("conntects")) {//来自联系人入口发布工单
                map.put(keyTeamId, (String) _outMap.get("TeamID"));
                map.put(keyTeamerId, (String) _outMap.get("AcceptID"));
                newOrderArrrow7Text2.setText((String) _outMap.get("TeamName"));
                newOrderArrrow8Text2.setText((String) _outMap.get("TeamerName"));
                newOrderArrrow4Text2.setText("已受理");
            } else if (_outMap.get("from").equals("devices")) {//来自设备入口发布工单
                map.put(keyClientId, (String) _outMap.get("ClientID"));
                AssetID = getIntent().getStringExtra("AssetID");
                Log.e("lxs", "onCreate: "+ AssetID );
                map.put(keyClientConactId, (String) _outMap.get("ContactID"));
                newOrderArrrow3Text2.setText((String) _outMap.get("ClientName"));
            }
        }
    }
    Map _outMap = null;
    private Handler mHandler = new MyHandler(this,1);

    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        private int i;
        public MyHandler(AppCompatActivity fragment,int i) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.i=i;
        }

        @Override
        public void handleMessage(Message msg) {

            if (proDialog != null) {
                proDialog.dismiss();
            }

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if(i == 2){
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        break;
                    case ConstValues.MSG_ERROR:
                        if(i == 2){
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        Toast.makeText(getBaseContext(), "工单提交失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        if(i == 2){
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        if(i == 2){
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(i == 2){
                            handleData((String) msg.obj);
                            return;
                        }
                        Toast.makeText(getBaseContext(), "工单提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            }
        }
    }
    private List<ConnectEntry> userEntryList;
    private void handleData(String json) {
        dismissLoading();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ConnectEntry>>() {
        }.getType();
        userEntryList = gson.fromJson(json, type);
    }

    @OnClick({R.id.new_order_back})
    public void onBack() {
       TiShi();
    }
    private MaterialDialog dialogTip;
    /**
     * 提示是否放弃发布
     */
    private void TiShi() {
        dialogTip = new MaterialDialog(this)
                .setTitle("提示")
                .setMessage("是否取消发布")
                .setPositiveButton("是", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        dialogTip.dismiss();
                    }
                }).setNegativeButton("否", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogTip.dismiss();
                    }
                });
        dialogTip.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
          TiShi();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({ R.id.new_order_l_3,//R.id.new_order_l_1,R.id.new_order_l_2,
            R.id.new_order_l_5, R.id.new_order_l_6,// R.id.new_order_l_4,
            R.id.new_order_l_7})// ,R.id.new_order_l_8, R.id.new_order_l_9, R.id.new_order_l_client_connect
    @Override
    public void onClick(View v) {
        switch (v.getId()) {//填写标题
//            case R.id.new_order_l_1:
//                Utils.start_ActivityResult(this, ActivityCommEdt.class, ConstValues.RESULT_FOR_PICKER_TITLE_EDT,
//                        new YBasicNameValuePair[]{new YBasicNameValuePair("edttitle", "标题"),
//                                new YBasicNameValuePair("edtmsg", newOrderArrrow1Text2.getText().toString())});
//                break;
//            case R.id.new_order_l_2://填写描述
//
//                Utils.start_ActivityResult(this, ActivityCommEdt.class, ConstValues.RESULT_FOR_PICKER_DES_EDT,
//                        new YBasicNameValuePair[]{new YBasicNameValuePair("edttitle", "描述"),
//                                new YBasicNameValuePair("edtmsg", newOrderArrrow2Text2.getText().toString())});
//                break;
            case R.id.new_order_l_3://选择客户
                if(_outMap != null &&_outMap.get("from").equals("devices")){
                    return;
                }
                Utils.start_ActivityResult(this, ParentSelectorActivity.class, ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "选择客户")});
                break;
            case R.id.new_order_l_5://选择工单类型
                Utils.start_ActivityResult(this, ActivityWorkOrderStatusInfo.class, ConstValues.RESULT_FOR_PICKER_ORDER_TYPES,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "工单类型")});
                break;
            case R.id.new_order_l_6://选择工单优先级
                Utils.start_ActivityResult(this, ActivityWorkOrderStatusInfo.class, ConstValues.RESULT_FOR_PICKER_ORDER_LEVEL,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "工单优先级")});
                break;
            case R.id.new_order_l_7://选择服务组
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Utils.start_ActivityResult(this, ActivityPickerTeam.class, ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "选择服务组")
                        });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        String msg = data.getStringExtra("edtmsg");
        String info = data.getStringExtra("info");
        String result = data.getStringExtra("result");
        String ids = data.getStringExtra("_ids");

        switch (resultCode) {
            case ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT://客户
                Log.e("lxs", "onActivityResult:clientIds "+result+",ids--"+ids );
                if (result != null) {
                    newOrderArrrow3Text2.setText(result);
                }
                if (ids != null && ids.contains(",")) {
                    String clientIds[] = ids.split(",");
                    Log.e("lxs", "onActivityResult:clientIds "+clientIds[0] );
                    map.put(keyClientId, clientIds[0]);
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT://受理服务组


                if (ids != null && !ids.equals("")) {
                    String[] idssplit = ids.split(",");
                    map.put(keyTeamId, idssplit[0]);
                    map.put(keyTeamerId,  idssplit[1]);
                    map.put(keyFollowId, idssplit[1]);
                }
                if (result != null) {
                    newOrderArrrow7Text2.setText(result);
                    if (newOrderArrrow4Text2.getText().equals("未受理")) {
                        newOrderArrrow4Text2.setText("已受理");
                    }
                }
                break;
        }
    }

    /**
     * 百度的定位服务
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            locationArr = "";
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                }
                LOG.e(NewWorkOrderActivity.this, locationArr);
            }
        }
    };
}
