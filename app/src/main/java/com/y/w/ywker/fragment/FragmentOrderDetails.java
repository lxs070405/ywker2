package com.y.w.ywker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.service.LocationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ActivityPickerTeam;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.YwkerApplication;
import com.y.w.ywker.activity.ActivityOrderReplayDetails;
import com.y.w.ywker.activity.ActivityZongJie;
import com.y.w.ywker.entry.OrderDetailsEntry;
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
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/20.
 * 工单详情页
 */
public class FragmentOrderDetails extends Fragment implements View.OnClickListener {

    @Bind(R.id.new_order_arrrow1_text2)
    TextView newOrderArrrow1Text2;
    @Bind(R.id.new_order_arrrow2_text2)
    TextView newOrderArrrow2Text2;
    @Bind(R.id.new_order_arrrow3_text2)
    TextView newOrderArrrow3Text2;
    @Bind(R.id.new_order_arrrow4_text2)
    TextView newOrderArrrow4Text2;
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
    @Bind(R.id.order_details_title_info)
    TextView titleinfo;
    @Bind(R.id.new_order_client_adress_text2)
    TextView newOrderClientAdressText2;
    @Bind(R.id.order_details_l_client_adress)
    LinearLayout orderDetailsLClientAdress;
    @Bind(R.id.new_order_client_connect_text2)
    TextView newOrderClientConnectText2;
    @Bind(R.id.order_details_l_client_connect)
    LinearLayout orderDetailsLClientConnect;
    @Bind(R.id.order_details_l_2)
    LinearLayout order_details_l_2;
    @Bind(R.id.new_order_arrrow1_text1)
    TextView newOrderArrrow1Text1;
    @Bind(R.id.tv_lianxiren)
    TextView tvLianxiren;
    @Bind(R.id.ll_lianxiren)
    LinearLayout llLianxiren;
    @Bind(R.id.tv_dianhua)
    TextView tvDianhua;
    @Bind(R.id.ll_dianhua)
    LinearLayout llDianhua;
    @Bind(R.id.new_order_arrrow4_text1)
    TextView newOrderArrrow4Text1;
    @Bind(R.id.order_details_l_4)
    LinearLayout orderDetailsL4;
    @Bind(R.id.new_order_arrrow5_text1)
    TextView newOrderArrrow5Text1;
    @Bind(R.id.order_details_l_5)
    LinearLayout orderDetailsL5;
    @Bind(R.id.order_details_l_6)
    LinearLayout orderDetailsL6;
    @Bind(R.id.order_details_l_7)
    LinearLayout orderDetailsL7;
    @Bind(R.id.order_details_l_8)
    LinearLayout orderDetailsL8;
    @Bind(R.id.order_details_l_9)
    LinearLayout orderDetailsL9;
    @Bind(R.id.tv_orderId)
    TextView tvOrderId;
    @Bind(R.id.order_details_l_1)
    LinearLayout orderDetailsL1;
    @Bind(R.id.new_order_arrrow3_text1)
    TextView newOrderArrrow3Text1;
    @Bind(R.id.order_details_l_3)
    LinearLayout orderDetailsL3;
    @Bind(R.id.new_order_arrrow2_text1)
    TextView newOrderArrrow2Text1;
    @Bind(R.id.tv_zongjie)
    TextView tvZongjie;
    @Bind(R.id.ll_zongjie)
    LinearLayout llZongjie;
    @Bind(R.id.btn_caozuo)
    Button btnCaozuo;
    private String orderid = "";
    private OrderDetailsEntry orderDetailsEntry;
    private UserEntry userEntry;

    public int getMyRole() {
        LOG.e(getContext(), "myRole = " + myRole);
        return myRole;
    }

    /**
     * 判断我的角色
     * 0  发起人  1  受理人  -1  其他  -2 - 关注人  3 - 可见范围的人
     */
    private int myRole = -1;

    private String orderStatus = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //    public String  getOrderData(){
//       String str =  tvOrderId.getText().toString()+","+
//        newOrderArrrow5Text2.getText().toString()+","+//工单类型
//        newOrderArrrow4Text2 .getText().toString() ;//工单状态
//        return str;
//    }
    private YHttpManagerUtils httpManagerUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_details, container, false);
        orderid = getArguments().getString("order_id");
        loadData();
        ButterKnife.bind(this, v);
        tvOrderId.setText(orderid);
        Log.e("lxs","getOrderStatus()"+getOrderStatus());
        return v;
    }

    public void loadData() {
        if (TextUtils.isEmpty(orderid)) {
            orderid = ((ActivityOrderReplayDetails) getActivity()).getOrderid();
        }
        Gson gson = new Gson();
        userEntry = gson.fromJson(OfflineDataManager.getInstance(getContext()).getUser(), UserEntry.class);
        Log.e("lxs", "onCreateView: 登录人名称" + userEntry.getUserName());
        if (orderid != null && !orderid.equals("")) {
            httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.GET_ORDER_BYID, orderid),new MyHandler(this,1), this.getClass().getName());
            httpManagerUtils.startRequest();
        }
    }

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.cancle();
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils = null;
        }
        super.onDestroy();
    }


    public void ShouLiRen(){
        Utils.start_ActivityResult(this, ActivityPickerTeam.class, ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT,
                new YBasicNameValuePair[]{
                        new YBasicNameValuePair("title", "选择服务组"),
                        new YBasicNameValuePair("orderId", orderid),
                        new YBasicNameValuePair("oldermsg", orderDetailsEntry.getTeamName())
                });

    }
    @Override
    public void onDestroyView() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    String msgstatus = "";
    private MaterialDialog dialogTip;
    /**
     * 提示是否放弃发布
     */
    private void TiShi(final String phone) {
        dialogTip = new MaterialDialog(getContext())
                .setTitle("提示")
                .setMessage("是否拨打该电话号码?")
                .setPositiveButton("是", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogTip.dismiss();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
                        startActivity(intent);

                    }
                }).setNegativeButton("否", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogTip.dismiss();
                    }
                });
        dialogTip.show();
    }
    // R.id.order_details_l_4, R.id.order_details_l_5,R.id.order_details_l_1, R.id.order_details_l_2, R.id.order_details_l_3,
//    R.id.order_details_l_6,  R.id.order_details_l_7, R.id.order_details_l_8,
//    R.id.order_details_l_9, R.id.order_details_l_client_connect
    @OnClick({R.id.ll_zongjie, R.id.btn_caozuo,R.id.ll_dianhua})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dianhua:
                String phone = tvDianhua.getText().toString().trim();
                TiShi(phone);
                break;
            case R.id.ll_zongjie://总结
                Utils.start_ActivityResult(this, ActivityZongJie.class, ConstValues.GONGZUOJONGJIE,
                        new YBasicNameValuePair[]{
                                new YBasicNameValuePair("orderId", orderid),
                                new YBasicNameValuePair("myRole", myRole+"")
                        });
                break;
            case R.id.btn_caozuo://操作

                String status =  getOrderStatus();
                onStart();
                if(status.equals("已受理")){
                    btnCaozuo.setClickable(false);
                    msgstatus = "3";
//                    llZongjie.setVisibility(View.VISIBLE);
                }
                if(status.equals("处理中") && myRole == 1){
//                    Utils.start_ActivityResult(this, ActivityZongJie.class, ConstValues.GONGZUOJONGJIE,
//                            new YBasicNameValuePair[]{
//                                    new YBasicNameValuePair("orderId", orderid),
//                                    new YBasicNameValuePair("myRole", myRole+""),
//                                    new YBasicNameValuePair("isFinish", "0")
//                            });
                    if(entries.get(0).getSheetSummary().isEmpty()||entries.get(0).getSheetSummary().equals("null")){
                        Toast.makeText(getContext(),"请填写工作总结才可以完成",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    msgstatus = "6";
                    btnCaozuo.setClickable(false);
                }
                handleStatu(msgstatus);//处理
                Log.e("lxs", "onClick: msgstatus----->"+msgstatus );
                break;
        }
    }



    public void handleStatu(String msg) {

        if(msg.isEmpty()){
            return;
        }
        httpManagerUtils = new YHttpManagerUtils(getContext(), ConstValues.ORDER_MODIFY_URL, new MyHandler(this,2), getClass().getSimpleName());
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(getContext());
        HashMap<String, String> mapModify = new HashMap<String, String>();
        mapModify.put("ID", orderid);
        map.put("SheetID",orderid);
        map.put("Type","ADR");
        map.put("FileLength","0");
        map.put("SendID",offlineDataManager.getUserID());
        map.put("MainID",offlineDataManager.getMainID());
        mapModify.put("MainID", offlineDataManager.getMainID());
        mapModify.put("UserID", offlineDataManager.getUserID());
        mapModify.put("UpdateDime", TimeUtils.getTime(System.currentTimeMillis()));
        mapModify.put("UpdateDetail", msg);
        mapModify.put("UpdateType", "UpdateState");
        mapModify.put("SendAdr", "");
        for (String key : mapModify.keySet()) {
            LOG.e(getContext(), key + " : " + mapModify.get(key));
        }
        httpManagerUtils.startPostRequest(mapModify);
    }



    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private HashMap<String, String> map = new HashMap<String, String>();

    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;
        int i ;
        public MyHandler(Fragment fragment,int i) {
            mFragmentReference = new WeakReference<Fragment>(fragment);
            this.i = i;
        }

        @Override
        public void handleMessage(Message msg) {

            Fragment fragment = mFragmentReference.get();

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if(btnCaozuo != null){
                            btnCaozuo.setClickable(true);
                        }
                        if(i == 1)
                            break;
                    case ConstValues.MSG_ERROR:
                        if(btnCaozuo != null){
                            btnCaozuo.setClickable(true);
                        }
                        if(i == 1)
                            Toast.makeText(getContext(), "获取工单信息失败", Toast.LENGTH_SHORT).show();
                        //使用默认值
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        if(btnCaozuo != null){
                            btnCaozuo.setClickable(true);
                        }
                        if(i == 1)
                            Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        if(btnCaozuo != null){
                            btnCaozuo.setClickable(true);
                        }
                        if(i == 1)
                            Toast.makeText(getContext(), "获取工单信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(i == 1){
                           String data =  (String) msg.obj;
                            if(data.contains("{")){
                                handleData(data);
                                controlMenuBtn();
                            }
                        }
                        if(i == 2){
//                            boolean isReplay = false;
                            if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                                Toast.makeText(getContext(), "操作成功", Toast.LENGTH_SHORT).show();
                                btnCaozuo.setClickable(true);
                                loadData();
//                                if(getOrderStatus().equals("未受理")){
//                                    loadData();
//                                    orderDetailsL7.setVisibility(View.VISIBLE);
//                                    setOrderStatus("已受理");
//                                    return;
//                                }
//                                if(getOrderStatus().equals("已受理")){
//                                    setOrderStatus("处理中");
//                                    btnCaozuo.setText("工单完成");
////                                    isReplay = false;
//                                }else
//                                if(getOrderStatus().equals("处理中")){
//                                    setOrderStatus("已完成");
//                                    btnCaozuo.setVisibility(View.GONE);
////                                    isReplay = true;
//                                }
                                // else
//                                 if(getOrderStatus().equals("暂停")){
//                                    setOrderStatus("处理中");
//                                    btnCaozuo.setVisibility(View.VISIBLE);
//                                   isReplay = true;
//                                }

                                if( !locationArr.isEmpty()){//isReplay &&
                                    map.put("Detail",locationArr);
                                    httpManagerUtils.setUrl(ConstValues.REPLAY_ORDERS_URL);
                                    httpManagerUtils.startPostRequest(map);
                                }
                                break;
                            }
                        }
                }
            }
        }
    }
    /**
     * 定位服务
     */
    private LocationService locationService;
    /**
     * 开启定位
     */
    @Override
    public void onStart() {
        super.onStart();
        locationService = ((YwkerApplication) (getActivity().getApplication())).locationService;
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }
    String locationArr = "";
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    locationArr = location.getAddrStr();
                }
                Log.e("lxs", "onReceiveLocation:  locationArr---->" +locationArr );
//                locationService.stop();
            }
        }
    };
    private List<OrderDetailsEntry> entries;
    private void handleData(String json) {
        Log.e("TAG", "工单详情:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<OrderDetailsEntry>>() {
        }.getType();
        entries = gson.fromJson(json, listType);
        if (entries != null && entries.size() > 0) {
            //设置信息
            orderDetailsEntry = entries.get(0);
            /**
             * 判断下是否是受理人或者发起人
             */
            myRole = -1;
            if (userEntry != null) {
                Log.e("lxs", "orderDetailsEntry.getUserName(): " + orderDetailsEntry.getUserName());
                if (orderDetailsEntry.getWriterName().equals(userEntry.getUserName())) {
                    //发起人
                    myRole = 0;
                }
                if (orderDetailsEntry.getUserName().equals(userEntry.getUserName())) {
                    //受理人
                    myRole = 1;
                } else if (orderDetailsEntry.getSheetStateCN().equals("未受理")) {//未受理状态
                    /**
                     * 判断是不是关注人 --- 模拟测试使用
                     */
                    if (!orderDetailsEntry.getFollow().equals("")) {//关注人
                        //判断个人ID是否包含在内
                        String follow = orderDetailsEntry.getFollowID();
                        Log.e("tag", "follow id = " + follow);
                        if (follow.contains(",")) {
                            String follows[] = follow.split(",");
                            for (String f : follows) {
                                if (f.equals(userEntry.getID())) {
                                    myRole = -2;
                                    break;
                                }
                            }
                        } else if (follow.equals(userEntry.getID())) {
                            myRole = -2;
                        } else {
                            myRole = 3;
                        }
                    } else {//其他人
                        myRole = 3;
                    }
                }
            } else {
                Log.e("TAG", "userentry == null,没有进行判断角色");
            }
            /**
             * 获取工单状态
             */
            orderStatus = orderDetailsEntry.getSheetStateCN();
//           发起人
//            titleinfo.setText(String.format("%s  创建于  %s ", orderDetailsEntry.getWriterName(), orderDetailsEntry.getWriteTime()));
            String faqiren = String.format("%s  创建于  %s ", orderDetailsEntry.getWriterName(), orderDetailsEntry.getWriteTime());
            newOrderArrrow1Text2.setText(orderDetailsEntry.getSheetTitle());
            newOrderArrrow2Text2.setText(orderDetailsEntry.getSheetDetail());
            if (newOrderArrrow2Text2.getText().length() == 0) {//如果获得到的描述信息为空 则不显示
                order_details_l_2.setVisibility(View.GONE);
            }
            if (orderDetailsEntry.getLianxiren().equals("")) {
                llLianxiren.setVisibility(View.GONE);
            } else {
                tvLianxiren.setText(orderDetailsEntry.getLianxiren());
            }
            if (orderDetailsEntry.getDianhua().equals("")) {
                llDianhua.setVisibility(View.GONE);
            } else {
                tvDianhua.setText(orderDetailsEntry.getDianhua());
            }
            if (orderDetailsEntry.getClientName().equals("")) {
                orderDetailsLClientAdress.setVisibility(View.GONE);
                orderDetailsLClientConnect.setVisibility(View.GONE);
            } else {
                orderDetailsLClientAdress.setVisibility(View.VISIBLE);
                orderDetailsLClientConnect.setVisibility(View.GONE);
                newOrderArrrow3Text2.setText(orderDetailsEntry.getClientName());
                String adr = orderDetailsEntry.getClientAdr();
                if (adr.equals("")) {//如果客户地址为空则隐藏条目栏
                    orderDetailsLClientAdress.setVisibility(View.GONE);
                }
                newOrderClientAdressText2.setText(adr.equals("") ? "无" : adr);
                String con = orderDetailsEntry.getLianxiren();
                newOrderClientConnectText2.setText(con.equals("") ? "无" : con);
            }
            String orderstate = orderDetailsEntry.getSheetStateCN();
            String ordertype =  orderDetailsEntry.getTypeName();
            newOrderArrrow4Text2.setText(orderDetailsEntry.getSheetStateCN());//工单状态
            newOrderArrrow5Text2.setText(orderDetailsEntry.getTypeName());//类型
//            newOrderArrrow6Text2.setText(orderDetailsEntry.getPriority());//优先级
            ((ActivityOrderReplayDetails) getActivity()).SetOrderPageData(orderstate,ordertype,faqiren);
            String team = orderDetailsEntry.getTeamName();
            String teamer = orderDetailsEntry.getUserName();
            newOrderArrrow7Text2.setText(team + "," + teamer);//受理人

            if(getOrderStatus().equals("未受理")){
                orderDetailsL7.setVisibility(View.GONE);
            }

            if(getOrderStatus().equals("未受理")||getOrderStatus().equals("已受理")){//||getOrderStatus().equals("处理中")
                llZongjie.setVisibility(View.GONE);
            }else {
                llZongjie.setVisibility(View.VISIBLE);
            }
//            newOrderArrrow8Text2.setText(teamer.equals("") ? "无" : teamer);
//            String follow = orderDetailsEntry.getFollow();
//            newOrderArrrow9Text2.setText(follow.equals("") ? "无" : follow);
        }
    }

    /**
     * 修改工单
     */

    public enum MODIFY_TYPE {
        ORDER_TITLE,
        ORDER_DETAILS
    }

    private String ShouLiRenId;
    public String getShouLiRenId(){
        return ShouLiRenId;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        String msg = data.getStringExtra("edtmsg");
        String info = data.getStringExtra("info");
        String result = data.getStringExtra("result");
        String ids = data.getStringExtra("_ids");

        switch (resultCode) {
//            case ConstValues.GONGZUOJONGJIE:
//                Log.e("lxs","sfadsfsdfasd----");
//                llZongjie.setVisibility(View.VISIBLE);
//                msgstatus = "6";
//                updataOderstate(msgstatus);//处理
//                onStart();
//                break;
            case ConstValues.RESULT_FOR_PICKER_TITLE_EDT:
                if (msg != null) {
                    newOrderArrrow1Text2.setText(msg);
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_DES_EDT:
                if (msg != null) {
                    newOrderArrrow2Text2.setText(msg);
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_LEVEL:
                if (info != null) {
                    newOrderArrrow6Text2.setText(info);
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_STATUS:
                if (info != null) {
                    newOrderArrrow4Text2.setText(info);
                    if (info.equals("未受理")) {
                        newOrderArrrow7Text2.setText("");
                        newOrderArrrow8Text2.setText("");
                    } else {
                        if (newOrderArrrow8Text2.getText().toString().equals("")) {
                            Toast.makeText(getContext(), "请选择受理人", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_ORDER_TYPES:
                if (info != null) {
                    newOrderArrrow5Text2.setText(info);
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT:
                if (result != null) {
                    String results[] = result.split(",");
                    if (results != null && results.length == 3) {
                        orderDetailsEntry.setClientName(results[0]);
                        newOrderArrrow3Text2.setText(results[0]);
                        orderDetailsLClientAdress.setVisibility(View.VISIBLE);
//                        orderDetailsLClientConnect.setVisibility(View.VISIBLE);
                        newOrderClientAdressText2.setText(results[1]);
                        newOrderClientConnectText2.setText(results[2]);
                    } else {
                        orderDetailsEntry.setClientName(result);
                        newOrderArrrow3Text2.setText(result);
                        orderDetailsLClientAdress.setVisibility(View.GONE);
                        orderDetailsLClientConnect.setVisibility(View.GONE);
                    }
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT:

                Log.e("lxs","受理人返回结果 ids--->"+ids+",resul--->"+result);
                if (ids != null && !ids.equals("")) {
                    orderDetailsEntry.setTeamID(ids);
                    orderDetailsEntry.setAcceptID("");
                    ShouLiRenId = ids.substring(ids.indexOf(",")+1,ids.length());
                    ((ActivityOrderReplayDetails) getActivity()).modifyOrder("2",ShouLiRenId);
                }
                if (result != null) {
                    newOrderArrrow7Text2.setText(result);
                    newOrderArrrow8Text2.setText("");
                    if (newOrderArrrow4Text2.getText().equals("未受理")) {
                        newOrderArrrow4Text2.setText("已受理");
                    }
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_SERVICES_CHILDREN:
                if (result != null) {
                    newOrderArrrow8Text2.setText(result);
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_WATCH_FOR:
                if (result != null) {
                    newOrderArrrow9Text2.setText(result);
                }
                break;
        }
    }

    public  void  showBtn(String btnmsg){
        btnCaozuo.setText(btnmsg);
        btnCaozuo.setVisibility(View.VISIBLE);
    }
    public void dismissBtn(){
        btnCaozuo.setVisibility(View.GONE);
    }

    public void controlMenuBtn() {
        if(orderStatus.equals("未受理")){
            myRole = 1;
            ((ActivityOrderReplayDetails) getActivity()).setMenuVisiable(true);
            return;
        }
        if(getOrderStatus().equals("已完成")){
            btnCaozuo.setVisibility(View.GONE);
            ((ActivityOrderReplayDetails) getActivity()).setMenuVisiable(false);
            return;
        }
        map.put("SendTime", TimeUtils.getTime(System.currentTimeMillis()));
        Log.e("lxs", "controlMenuBtn: myRole" + getMyRole());
        if (getMyRole() >= 1) {
            ((ActivityOrderReplayDetails) getActivity()).setMenuVisiable(true);
            btnCaozuo.setVisibility(View.VISIBLE);
            if(orderStatus.equals("已受理")){
                btnCaozuo.setText("开始处理");
            }else  if(orderStatus.equals("处理中")){
                btnCaozuo.setText("工单完成");
            }
//            else if(orderStatus.equals("暂停")){
//                btnCaozuo.setText("工单完成");
//            }
            else {
                btnCaozuo.setVisibility(View.GONE);
            }
        } else {
            btnCaozuo.setVisibility(View.GONE);
            ((ActivityOrderReplayDetails) getActivity()).setMenuVisiable(false);
            if(orderStatus.equals("已完成")){
                llZongjie.setVisibility(View.VISIBLE);
            }
            else {
                llZongjie.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取工单状态
     *
     * @return
     */
    public String getOrderStatus() {
        return  ((ActivityOrderReplayDetails) getActivity()).getOrderStatus();
    }

    /**
     * 设置工单状态
     */
    public void setOrderStatus(String status) {
//        newOrderArrrow4Text2.setText(status);
        ((ActivityOrderReplayDetails) getActivity()).setOrderState(status);
    }


}
