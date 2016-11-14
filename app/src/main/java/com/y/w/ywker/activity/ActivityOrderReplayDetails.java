package com.y.w.ywker.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.service.LocationService;
import com.bumptech.glide.Glide;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.YwkerApplication;
import com.y.w.ywker.fragment.FragmentOrderDetails;
import com.y.w.ywker.fragment.FragmentOrderReplay;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MaterialDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/20.
 * 工单详情包括工单回复和工单查看
 */
public class ActivityOrderReplayDetails extends SuperActivity {
    @Bind(R.id.order_details_viewpager)
    ViewPager orderDetailsViewpager;
    @Bind(R.id.order_details_tab)
    SmartTabLayout orderDetailsTab;
    @Bind(R.id.order_detatls_back)
    ImageView orderDetatlsBack;
    @Bind(R.id.order_detatls_menu)
    ImageView orderDetatlsMenu;
    @Bind(R.id.order_handle_layout)
    RelativeLayout orderHandleLayout;
    @Bind(R.id.order_detail_replay_status_lv)
    ListView orderDetailReplayStatusLv;
    @Bind(R.id.order_cancle_view_tv)
    TextView orderCancleViewTv;
    @Bind(R.id.order_details_replay_show_img)
    ImageView orderDetailsReplayShowImg;
    @Bind(R.id.order_details_replay_show_img_layout)
    RelativeLayout orderDetailsReplayShowImgLayout;
    @Bind(R.id.tv_orderId)
    TextView tvOrderId;
    @Bind(R.id.tv_ordertype)
    TextView tvOrdertype;
    @Bind(R.id.tv_orderstate)
    TextView tvOrderstate;
    @Bind(R.id.tv_FaQiRen)
    TextView tvFaQiRen;

    /**
     * 工单详情Fragment
     */
    private FragmentOrderDetails detailsFragment;
    /**
     * 工单回复Fragment
     */
    private FragmentOrderReplay replayFragment;
    /**
     * 定位需要的权限
     */
    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;

    /**
     * 获取工单ID
     *
     * @return
     */
    public String getOrderid() {
        return orderid;
    }

    private String orderid = "";
    String order_title = "";
    String order_level = "";
    String order_status = "";

    /**
     * 工单修改后的当前状态
     */
    String orderCurrentStatusCN = "";

    /**
     * 获取当前fragment的索引,判断右上角menu是否显示
     *
     * @return
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    private int currentIndex = 0;

    /**
     * 状态
     */
    HashMap<String, Integer> mapStatusCode = new HashMap<String, Integer>();
    List<String> statusList = new ArrayList<String>();

    /**
     * 默认初始化状态
     */
    private void initMapStatus() {

        mapStatusCode.clear();
        ;
        statusList.clear();

        mapStatusCode.put("未受理", 1);
        mapStatusCode.put("已受理", 2);
        mapStatusCode.put("处理中", 3);
        mapStatusCode.put("暂停", 4);
//        mapStatusCode.put("删除", 5);
        mapStatusCode.put("已完成", 6);

        //发起人
        if (detailsFragment.getMyRole() == 0) {
//            statusList.add("未受理");
//            statusList.add("已受理");
//            statusList.add("处理中");
//            statusList.add("暂停");
//            statusList.add("删除");
//            statusList.add("已完成");
        } else if (detailsFragment.getMyRole() == 1) {
            //受理人
            statusList.add("未受理");
            statusList.add("已受理");
            statusList.add("处理中");
            statusList.add("暂停");
            statusList.add("已完成");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        /**
         * 设置adapter
         */
        orderDetailsViewpager.setAdapter(new OrderFragmentPagerAdapter(getSupportFragmentManager()));
        orderDetailsTab.setViewPager(orderDetailsViewpager);


        orderDetailsViewpager.setCurrentItem(0);
        orderDetailsViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                if (replayFragment != null) {
                    replayFragment.reloadData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getPersimmions();

        /**
         * 获取传过来的工单信息
         */
        orderid = getIntent().getStringExtra("order_id");
        order_title = getIntent().getStringExtra("order_title");
        order_level = getIntent().getStringExtra("order_level");
        order_status = getIntent().getStringExtra("order_status");

        if (TextUtils.isEmpty(orderid)) {
            Log.e("TAG", "oncreate获取到的orderid为空");
        } else {
            Log.e("TAG", "order_id = " + orderid);
        }

        tvOrderId.setText(orderid);
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        mainId = offlineDataManager.getMainID();
    }

    public String getOrderId() {
        return this.orderid;
    }

    public void SetOrderPageData(String orderstate,String ordertype,String faqiren){
        tvFaQiRen.setText(faqiren);
        tvOrderstate.setText(orderstate);
        tvOrdertype.setText(ordertype);
    }
    public void setOrderState(String orderstate){
        tvOrderstate.setText(orderstate);
    }
    public String getOrderStatus(){
        return tvOrderstate.getText().toString();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 设置Fragment Adapter
     */
    public class OrderFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"工单", "回复"};

        public OrderFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("order_id", orderid);
            switch (position) {
                case 0:
                    if (detailsFragment == null) {
                        detailsFragment = new FragmentOrderDetails();
                    }
                    detailsFragment.setArguments(bundle);
                    return detailsFragment;
                case 1:
                    if (replayFragment == null) {
                        replayFragment = new FragmentOrderReplay();
                    }
                    bundle.putString("order_title", order_title);
                    bundle.putString("order_level", order_level);
                    bundle.putString("order_status", order_status);
                    replayFragment.setArguments(bundle);
                    return replayFragment;
                default:
                    if (replayFragment == null) {
                        replayFragment = new FragmentOrderReplay();
                    }
                    bundle.putString("order_title", order_title);
                    bundle.putString("order_level", order_level);
                    bundle.putString("order_status", order_status);
                    replayFragment.setArguments(bundle);
                    return replayFragment;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    ArrayAdapter adapter;
    /**
     * 初始化状态列表
     */
    List<String> list;
    MaterialDialog dialogTip;

    private void initStatusAdapter() {//String status
//        if (statusList.contains(status)) {//如果含有
//            int index = statusList.indexOf(status);
            list = new ArrayList<String>();
            list.add("转移分配");
            if (detailsFragment.getOrderStatus().equals("暂停")) {
                list.add("取消暂停");
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            if (detailsFragment.getOrderStatus().equals("处理中")) {
                list.add("暂停");
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            if (!list.isEmpty()) {

                adapter = new ArrayAdapter<String>(this, R.layout.order_status_item, list);
                orderDetailReplayStatusLv.setAdapter(adapter);
                orderDetailReplayStatusLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        statposition = position;
                        Log.e("lxs", "onItemClick:----> " + position);
                        if (list.get(position).equals("转移分配") || list.get(position).equals("转移工单")) {//工单转移
//                            if(detailsFragment.getOrderStatus().equals("暂停")){
//                                Toast.makeText(ActivityOrderReplayDetails.this,
//                                        "请先取消暂停才可以转移",Toast.LENGTH_SHORT  ).show();
//                                return;
//                            }
                            Log.e("lxs", "onItemClick:工单转移----> ");
                            detailsFragment.ShouLiRen();
                            detailsFragment.setOrderStatus("已受理");
                        }
                        if (list.get(position).equals("取消暂停")) {
                            modifyOrder("3", "0");
                            Log.e("lxs", "onItemClick:取消暂停----> " + detailsFragment.getOrderStatus());
                        }

                        if (list.get(position).equals("暂停")) {//暂停工单
                            dialogTip = new MaterialDialog(ActivityOrderReplayDetails.this)
                                    .setTitle("提示")
                                    .setMessage("确定暂停该工单?")
                                    .setPositiveButton("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            detailsFragment.setOrderStatus("暂停");
                                            modifyOrder("4", "0");
//                                            detailsFragment.handleStatu("4");
                                            detailsFragment.dismissBtn();
                                            dialogTip.dismiss();
                                        }
                                    }).setNegativeButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogTip.dismiss();
                                        }
                                    });
                            dialogTip.show();
                        }
                        orderHandleLayout.setVisibility(View.GONE);
                    }
                });
            }
//        }
    }

    private int statposition = 0;

    private final int ERROR_RESULIT = 888;
    private final int NO_RESULIT = 666;
    private final int BINGD_RESULIT = 999;
    private final int FAIL_RESULIT = 777;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ERROR_RESULIT:
                    ConstValues.GuZhangisFinish = true;
                    Toast.makeText(ActivityOrderReplayDetails.this, "请绑定设备", Toast.LENGTH_SHORT).show();
                    break;
                case BINGD_RESULIT:
//                    ConstValues.GuZhangisFinish = false;
//                    modifyOrder(mapStatusCode.get(list.get(statposition)) + "","0");
////                    if(detailsFragment != null){
//                        detailsFragment.handleStatu(mapStatusCode.get(list.get(statposition))+ "");
//                    }

                    break;
                case FAIL_RESULIT:
                    ConstValues.GuZhangisFinish = true;
                    Toast.makeText(ActivityOrderReplayDetails.this, "联网失败", Toast.LENGTH_SHORT).show();
                    break;
                case NO_RESULIT:
                    ConstValues.GuZhangisFinish = true;
                    Toast.makeText(ActivityOrderReplayDetails.this, "未知的错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private YHttpManagerUtils httpManagerUtils;

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
        locationService = ((YwkerApplication) (this.getApplication())).locationService;
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
            }
        }
    };

    @Override
    public void onDestroy() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onDestroy();
    }


    /**
     * 用来修改工单的状态值,在工单详情页点击更多进行修改
     */
    private Handler mHandler = new MyHandler(this);

    class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity fragment) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if (orderid != null) {
                            Toast.makeText(ActivityOrderReplayDetails.this, "修改失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case ConstValues.MSG_ERROR:
                        if (orderid != null) {
                            Toast.makeText(ActivityOrderReplayDetails.this, "修改失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(ActivityOrderReplayDetails.this, "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(ActivityOrderReplayDetails.this, "状态修改成功", Toast.LENGTH_SHORT).show();
                            list.clear();
                            adapter.notifyDataSetChanged();
                            detailsFragment.loadData();
                        }
                        break;
                }
            }
        }
    }

    String mainId;

    /**
     * 快捷修改工单状态
     */
    public void modifyOrder(String msg, String ShouLiRenId) {

        httpManagerUtils = new YHttpManagerUtils(this, ConstValues.ORDER_MODIFY_URL, mHandler, getClass().getSimpleName());
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        HashMap<String, String> mapModify = new HashMap<String, String>();
        String updataType = "UpdateState";
        if (ShouLiRenId.equals("0")) {
            ShouLiRenId = offlineDataManager.getUserID();
        }
//        else {
//            updataType = "UpdateStateAccept";
//        }
        mapModify.put("ID", orderid);
        mapModify.put("MainID", offlineDataManager.getMainID());
        mapModify.put("UserID", ShouLiRenId);
        mapModify.put("UpdateDime", TimeUtils.getTime(System.currentTimeMillis()));
        mapModify.put("UpdateDetail", msg);
        mapModify.put("UpdateType", updataType);
        mapModify.put("SendAdr", "");
        for (String key : mapModify.keySet()) {
            LOG.e(this, key + " : " + mapModify.get(key));
        }
        httpManagerUtils.startPostRequest(mapModify);
    }

    @OnClick({R.id.order_cancle_view_tv, R.id.order_detatls_menu, R.id.order_handle_layout, R.id.order_details_replay_show_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_detatls_menu:
                if (orderHandleLayout.getVisibility() == View.VISIBLE) {
                    orderHandleLayout.setVisibility(View.GONE);
                } else {
                    orderHandleLayout.setVisibility(View.VISIBLE);
//                    initMapStatus();
                    initStatusAdapter();//detailsFragment.getOrderStatus()
                }
                break;
            case R.id.order_handle_layout:
                orderHandleLayout.setVisibility(View.GONE);
                break;
            case R.id.order_cancle_view_tv:
                orderHandleLayout.setVisibility(View.GONE);
                break;
            case R.id.order_details_replay_show_img:
                orderDetailsReplayShowImgLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void setMenuVisiable(boolean isVisiable) {
        if (isVisiable) {
            LOG.e(this, "menu设置为可见");
            orderDetatlsMenu.setVisibility(View.VISIBLE);
        } else {
            orderDetatlsMenu.setVisibility(View.GONE);
            LOG.e(this, "menu设置为不可见");
        }

    }

    @OnClick({R.id.order_detatls_back})
    public void onBack(View view) {
        finish();
    }


    /**
     * 适配6.0及以上的系统,避免运行时权限导致崩溃
     */

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 全屏显示图片的大图
     *
     * @param img_url
     */
    public void showImage(String img_url) {
        if (orderDetailsReplayShowImgLayout.getVisibility() == View.GONE) {
            orderDetailsReplayShowImgLayout.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(img_url).placeholder(R.drawable.photo_default).error(R.drawable.photo_default).into(orderDetailsReplayShowImg);
    }

    @Override
    public void onBackPressed() {
        if (orderDetailsReplayShowImgLayout.getVisibility() == View.VISIBLE) {
            orderDetailsReplayShowImgLayout.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }
}
