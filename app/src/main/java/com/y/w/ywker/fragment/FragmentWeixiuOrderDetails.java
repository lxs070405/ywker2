package com.y.w.ywker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.y.w.ywker.activity.ActivityWeXiuZongJie;
import com.y.w.ywker.activity.BindDevicesActivity;
import com.y.w.ywker.activity.WeiXiuOrderDetailActivity;
import com.y.w.ywker.adapters.AdapterDelete;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.WeiXiuOrderDetailEntry;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by lxs on 16/4/20.
 * 工单详情页
 */
public class FragmentWeixiuOrderDetails extends Fragment implements View.OnClickListener, AdapterDelete.IonSlidingViewClickListener {

    @Bind(R.id.tv_KeHuName)
    TextView tvKeHuName;
    @Bind(R.id.tv_lianxiren)
    TextView tvLianxiren;
    @Bind(R.id.tv_dianhua)
    TextView tvDianhua;
    @Bind(R.id.tv_ShouLiRen)
    TextView tvShouLiRen;
    @Bind(R.id.new_order_arrrow2_text1)
    TextView newOrderArrrow2Text1;
    @Bind(R.id.tv_OrderDesc)
    TextView tvOrderDesc;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.btn_addAseet)
    Button btnAddAseet;
    @Bind(R.id.btn_caozuo)
    Button btnCaozuo;
    private String orderid = "";

    public int getMyRole() {
        LOG.e(getContext(), "myRole = " + myRole);
        return myRole;
    }

    /**
     * 判断我的角色
     * 0  发起人  1  受理人  -1  其他  -2 - 关注人  3 - 可见范围的人
     */
    private int myRole = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private YHttpManagerUtils httpManagerUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weixiuorderdetails, container, false);
//        loadData();
        ButterKnife.bind(this, v);
        offlineDataManager = OfflineDataManager.getInstance(getContext());
        Gson gson = new Gson();
        userName =gson.fromJson(offlineDataManager.getUser(), UserEntry.class).getUserName();
        initView();
        onStart();
        return v;
    }
    String userName;
    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager((WeiXiuOrderDetailActivity) getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        mAdapter = new AdapterDelete(getContext(), devicedata);
        mAdapter.setDeleteBtnClickListener(this);
        recyclerview.setAdapter(mAdapter);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

    }

    public void loadData() {

        userId = offlineDataManager.getUserID();

        orderid = ((WeiXiuOrderDetailActivity) getActivity()).gettaskId();
        if (orderid != null && !orderid.equals("")) {
            httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.WEIXIU_ORDERDETAIL_URL, orderid), new MyHandler(this, 1), this.getClass().getName());
            httpManagerUtils.startRequest();
        }
    }
    public void reLodeData(){
        if (orderid != null && !orderid.equals("")) {
            httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.WEIXIU_ORDERDETAIL_URL, orderid), new MyHandler(this, 1), this.getClass().getName());
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


    public void ShouLiRen() {
        Utils.start_ActivityResult(this, ActivityPickerTeam.class, ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT,
                new YBasicNameValuePair[]{
                        new YBasicNameValuePair("title", "选择服务组"),
                        new YBasicNameValuePair("orderId", orderid),
//                        new YBasicNameValuePair("oldermsg", orderDetailsEntry.getTeamName())
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

    OfflineDataManager offlineDataManager;
    String userId = "";
    /**
     * btn事件处理 修改工单状态
     */
    public void updataOderstate(String msg, String UpdateWriter) {

        if (msg.isEmpty()) {
            return;
        }
        httpManagerUtils = new YHttpManagerUtils(getContext(), ConstValues.UPDATAWEIXIUORDER_STATUE_URL, new MyHandler(this, 2), getClass().getSimpleName());
        HashMap<String, String> mapModify = new HashMap<String, String>();
        mapModify.put("TaskId", orderid);
        mapModify.put("UpdateWriter", UpdateWriter);
        mapModify.put("TaskState", msg);
        mapModify.put("WriteAdr", locationArr);
        for (String key : mapModify.keySet()) {
            LOG.e(getContext(), key + " : " + mapModify.get(key));
        }
        httpManagerUtils.startPostRequest(mapModify);
        if(msg.equals("5")){
            btnCaozuo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private MaterialDialog dialogTip;
    private void TiShiAddAseet(String msg, final String lftbtn) {
        String str = "";
        if(lftbtn.equals("增加设备")){
            str = "取消";
        }
         dialogTip = new MaterialDialog(getContext())
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton(lftbtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(lftbtn.equals("增加设备")){
                            AddAseet();
                        }

                        dialogTip.dismiss();
                    }
                }).setNegativeButton(str, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogTip.dismiss();
                    }
                });
        dialogTip.show();
    }
    boolean isfinish = false;
    private HashMap<String, String> map = new HashMap<String, String>();

    @OnClick({R.id.btn_addAseet, R.id.btn_caozuo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_addAseet:
               AddAseet();
                break;
            case R.id.btn_caozuo:
                if(assetList == null ||assetList.size() == 0){
                    String msg = "请添加维修的设备然后开始处理";
                    TiShiAddAseet(msg,"增加设备");
                    return;
                }
                if (states.equals("已受理")) {
                    msgstatus = "3";
                }
                if (states.equals("处理中")) {
                    if(!isfinish){
                        TiShiAddAseet("请确认维修进度是否已完成，维修信息是否完整","确定");
                        return;
                    }
                    msgstatus = "5";
                }
                updataOderstate(msgstatus,offlineDataManager.getUserID());//处理
                btnCaozuo.setClickable(false);
                break;
        }
    }

    private void AddAseet() {
        if (role  != 1) {
            Toast.makeText(getContext(), "您没有操作权限", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), BindDevicesActivity.class);
        intent.putExtra("fromSource", "6");
        startActivityForResult(intent, ConstValues.ADD_SHEBEI);
    }

    @Override
    public void onItemClick(View view, int position) {
        String aseetName = devicedata.get(position).split(",")[0];
        String id = devicedata.get(position).split(",")[2];
        WeiXiuOrderDetailEntry.AssetListEntity listEntity = shebeiDatamap.get(id);
        Intent intent = new Intent(getActivity(), ActivityWeXiuZongJie.class);
        intent.putExtra("TaskID",orderid);
        intent.putExtra("myRole",role);
        intent.putExtra("aseetId",id);
        intent.putExtra("aseetName",aseetName);
        intent.putExtra("sourceId",listEntity.getID());
        intent.putExtra("zongjie",listEntity.getRepairSummary());
        intent.putExtra("RepairSchedule",listEntity.getRepairSchedule());
        intent.putExtra("SpdName",listEntity.getSpdName());
        Log.e("lxs", "onItemClick: sourceId--->"+listEntity.getID() );
        startActivity(intent);
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {//删除按钮操作
        if (!userName.equals(entry.getAcceptName())) {
            Toast.makeText(getContext(), "无权修改", Toast.LENGTH_SHORT).show();
            mAdapter.closeMenu();
            return;
        }
        if (entry.getTaskStatus().equals("已完成")) {
            Toast.makeText(getContext(), "该工单已完成,无法删除", Toast.LENGTH_SHORT).show();
            mAdapter.closeMenu();
            return;
        }
        String id = devicedata.get(position).split(",")[2];
        WeiXiuOrderDetailEntry.AssetListEntity listEntity = shebeiDatamap.get(id);
        String taskAssetId = listEntity.getID()+"";
        RemoveSheBeiData(taskAssetId);
        mAdapter.removeData(position);
    }

    private void RemoveSheBeiData(String taskAssetId) {
        httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.WEIXIU_ORDERDDeleteSheBei_URL, taskAssetId), new MyHandler(this,4), this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;
        int i;

        public MyHandler(Fragment fragment, int i) {
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

                    case ConstValues.MSG_ERROR:
                        if(btnCaozuo != null){
                            btnCaozuo.setClickable(true);
                        }
                        if (i == 1)
                            Toast.makeText(getContext(), "获取工单信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        if(btnCaozuo != null){
                            btnCaozuo.setClickable(true);
                        }
                        if (i == 1)
                            Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        if(btnCaozuo != null){
                             btnCaozuo.setClickable(true);
                        }
                        if (i == 1)
                            Toast.makeText(getContext(), "获取工单信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(i == 5){
                            Toast.makeText(getContext(), "更改受理人成功", Toast.LENGTH_SHORT).show();
                            reLodeData();
                        }
                        if(i == 4){
                            Toast.makeText(getContext(), "删除设备成功", Toast.LENGTH_SHORT).show();
                            reLodeData();
                        }
                        if(i == 3){
                            reLodeData();
                            Toast.makeText(getContext(), "增加设备成功", Toast.LENGTH_SHORT).show();
                        }
                        if (i == 1) {
                            handleData((String) msg.obj);
                            controlMenuBtn();
                        }
                        if (i == 2) {
                            if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                                Toast.makeText(getContext(), "操作成功", Toast.LENGTH_SHORT).show();
                                btnCaozuo.setClickable(true);
                                reLodeData();
                                if(entry != null && entry.getTaskStatus().equals("已完成")){
                                    btnCaozuo.setVisibility(View.GONE);
                                }
                                break;
                            }
                        }
                        break;
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
                Log.e("lxs", "onReceiveLocation:  locationArr---->" + locationArr);
//                locationService.stop();
            }
        }
    };

    /**
     * 维修详情实体
     */
    WeiXiuOrderDetailEntry entry = null;
    private HashMap<String, WeiXiuOrderDetailEntry.AssetListEntity> shebeiDatamap = new HashMap<>();
    List<WeiXiuOrderDetailEntry.AssetListEntity> assetList;
    private void handleData(String json) {
        if(devicedata.size() > 0){
            devicedata.clear();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<WeiXiuOrderDetailEntry>>() {
        }.getType();
        List<WeiXiuOrderDetailEntry> list = gson.fromJson(json, listType);

        if (list != null && list.size() > 0) {
            entry = list.get(0);
            ((WeiXiuOrderDetailActivity) getActivity()).
                    SetViewData(entry.getID()+"", entry.getTypeName(), entry.getTaskStatus(),
                            entry.getUseTime());
            tvKeHuName.setText(entry.getClientName());
            tvShouLiRen.setText(entry.getAcceptName());
            tvLianxiren.setText(entry.getLinkName());
            tvDianhua.setText(entry.getLinkTel());
            tvOrderDesc.setText(entry.getTaskDetail());
             assetList = entry.getAssetList();
            if(assetList != null){
                Log.e(TAG, "handleData: 后台数据assetList.size()---->" + assetList.size());
                for (int i = 0; i < assetList.size(); i++) {
                    WeiXiuOrderDetailEntry.AssetListEntity entity = assetList.get(i);
                    String AssetID = entity.getAssetID() + "";
                    String AseetName = entity.getAssetName();
                    devicedata.add(AseetName + "," + entity.getSpdName() + "," + AssetID);
                    shebeiDatamap.put(AssetID, entity);
                    if(!entity.getSpdName().contains("完成")|| entity.getRepairSummary().equals("")){
                        isfinish = false;
                    }else {
                        isfinish = true;
                    }
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
            /**
             * 获取工单状态
             */
            orderStatus = entry.getTaskStatus();
        if (!userName.equals(entry.getAcceptName())) {
            btnAddAseet.setVisibility(View.GONE);
        }
    }
    private String orderStatus;
    private String ShouLiRenId;

    public String getShouLiRenId() {
        return ShouLiRenId;
    }

    /**
     * 设备信息
     */
    private List<String> devicedata = new ArrayList<>();
    private AdapterDelete mAdapter;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) {
            return;
        }
        String result = intent.getStringExtra("result");
        String ids = intent.getStringExtra("_ids");
        switch (resultCode) {
            case ConstValues.ADD_SHEBEI://扫描二维码页面返回
                String AseetId = intent.getStringExtra("AseetId");
                Log.e(TAG, "维修详情页onActivityResult: AseetId" + AseetId);
                for (String s : devicedata) {
                    if(s.contains(AseetId)){
                        Toast.makeText(getContext(),"该设备已增加",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                postAddSheBeiData(AseetId);
                break;
            case ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT:
                Log.e("lxs","受理人返回结果 ids--->"+ids+",resul--->"+result);
                if (ids != null && !ids.equals("")) {
                    ShouLiRenId = ids.replace(",","$");
                    updateShouLiRen(ShouLiRenId);
                }
                if (result != null) {
                    tvShouLiRen.setText(result);
                }

                break;
        }
    }

    /**
     * 更改受理人
     * @param shouLiRenId
     */
    public void updateShouLiRen(String shouLiRenId) {
        httpManagerUtils = new YHttpManagerUtils(getContext(), ConstValues.UPDATAWEIXIUORDER_shouliren_URL, new MyHandler(this, 5), getClass().getSimpleName());
        HashMap<String, String> mapModify = new HashMap<String, String>();
        mapModify.put("TaskId", orderid);
        mapModify.put("UpdateType", "Accept");
        mapModify.put("Values", shouLiRenId);
        if(!shouLiRenId.equals(userId)){
            btnCaozuo.setVisibility(View.GONE);
        }
        mapModify.put("ChgeUserId", userId);
        for (String key : mapModify.keySet()) {
            LOG.e(getContext(), key + " : " + mapModify.get(key));
        }
        httpManagerUtils.startPostRequest(mapModify);

    }

    HashMap<String,String> hashmap = new HashMap<>();
    /**
     * 向后台增加设备
     * @param assetId
     */
    private void postAddSheBeiData(String assetId) {
        httpManagerUtils = new YHttpManagerUtils(getContext(), ConstValues.WEIXIU_ADDSHEBEI_URL, new MyHandler(this, 3), getClass().getSimpleName());
        hashmap.put("writeId", userId);
        hashmap.put("assetId",assetId);
        hashmap.put("taskId",orderid);
        httpManagerUtils.startPostRequest(hashmap);
    }


    public void dismissBtn() {
        btnCaozuo.setVisibility(View.GONE);
    }
    int role ;
    String states;
    /**
     * 控制显示处理工单btn
     */
    private void controlMenuBtn() {
         role = ((WeiXiuOrderDetailActivity) getActivity()).getMyRole();
        states = ((WeiXiuOrderDetailActivity) getActivity()).getorderstates();

        if (states.equals("未受理")) {
            ((WeiXiuOrderDetailActivity) getActivity()).setMenuVisiable(true);
            return;
        }
        if (states.equals("已完成")) {
            ((WeiXiuOrderDetailActivity) getActivity()).setMenuVisiable(false);
            btnAddAseet.setVisibility(View.GONE);
            return;
        }
        map.put("SendTime", TimeUtils.getTime(System.currentTimeMillis()));
        Log.e("lxs", "controlMenuBtn: myRole btn处理显示" + role+"当前工单状态"+states
               +"userName--"+ userName+":--*"+entry.getAcceptName()
        );

        if (userName.equals(entry.getAcceptName())) {
            ((WeiXiuOrderDetailActivity) getActivity()).setMenuVisiable(true);
            btnCaozuo.setVisibility(View.VISIBLE);
            if (states.equals("已受理")) {
                btnCaozuo.setText("开始维修");
            } else if (states.equals("处理中")) {
                btnCaozuo.setText("维修完成");
            } else {
                btnCaozuo.setVisibility(View.GONE);
            }
        } else {
            btnCaozuo.setVisibility(View.GONE);
            ((WeiXiuOrderDetailActivity) getActivity()).setMenuVisiable(false);
        }
    }


}
