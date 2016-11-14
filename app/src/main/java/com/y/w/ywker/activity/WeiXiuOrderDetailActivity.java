package com.y.w.ywker.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.WeiXiuOrderDetailEntry;
import com.y.w.ywker.fragment.FragmentWeiXiuOrderReplay;
import com.y.w.ywker.fragment.FragmentWeixiuOrderDetails;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
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

public class WeiXiuOrderDetailActivity extends SuperActivity {
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
    @Bind(R.id.tv_taskId)
    TextView tvTaskId;
    @Bind(R.id.tv_leiXing)
    TextView tvLeiXing;
    @Bind(R.id.tv_orderstate)
    TextView tvOrderstate;
    @Bind(R.id.tv_OrderTitle)
    TextView tvOrderTitle;
    @Bind(R.id.tv_haoshi)
    TextView tvHaoshi;

    /**
     * 工单详情Fragment
     */
    private FragmentWeixiuOrderDetails detailsFragment;
    /**
     * 工单回复Fragment
     */
    private FragmentWeiXiuOrderReplay replayFragment;
    /**
     * 定位需要的权限
     */
    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;

    /**
     * 获取工单ID
     * @return
     */
    public String gettaskId() {
        return taskId;
    }

    private String taskId = "";


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
        statusList.clear();
        mapStatusCode.put("未受理", 1);
        mapStatusCode.put("已受理", 2);
        mapStatusCode.put("处理中", 3);
        mapStatusCode.put("暂停", 4);
        mapStatusCode.put("已完成", 5);

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

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xunjianorderdetail);
        ButterKnife.bind(this);
        ctx = this;
        Gson gson = new Gson();
        userEntry = gson.fromJson(OfflineDataManager.getInstance(this).getUser(), UserEntry.class);
        initView();
    }
    private UserEntry userEntry;
    private void initView() {
        /**
         * 获取传过来的工单信息
         */
        taskId = getIntent().getStringExtra("taskId");

        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        mainId = offlineDataManager.getMainID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.WEIXIU_ORDERDETAIL_URL, taskId), new MyHandler(this,1), this.getClass().getName());
        httpManagerUtils.startRequest();

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


    }

    /**
     * 设置Fragment Adapter
     */
    public class OrderFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"维修", "回复"};

        public OrderFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (detailsFragment == null) {
                        detailsFragment = new FragmentWeixiuOrderDetails();
                    }
                    return detailsFragment;
                case 1:
                    if (replayFragment == null) {
                        replayFragment = new FragmentWeiXiuOrderReplay();
                    }
                    return replayFragment;
                default:
                    if (replayFragment == null) {
                        replayFragment = new FragmentWeiXiuOrderReplay();
                    }
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


    private YHttpManagerUtils httpManagerUtils;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    ArrayAdapter adapter;
    /**
     * 初始化状态列表
     */
    List<String> list;
    /**
     * 用来修改工单的状态值,在工单详情页点击更多进行修改
     */
    private Handler mHandler = new MyHandler(this,2);

    class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mFragmentReference;
        int i;
        public MyHandler(AppCompatActivity fragment,int i) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.i = i;
        }

        @Override
        public void handleMessage(Message msg) {
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if (taskId != null && i == 2) {
                            Toast.makeText(ctx, "修改失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case ConstValues.MSG_ERROR:
                        if (taskId != null && i == 2) {
                            Toast.makeText(ctx, "修改失败", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        if(i == 2)
                        Toast.makeText(ctx, "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(i == 1){
                            String json = (String) msg.obj;
                            parsData(json);
                        }
                        if(i == 2){
                            if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                                Toast.makeText(ctx, "状态修改成功", Toast.LENGTH_SHORT).show();
                                list.clear();
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }

                        break;
                }
            }
        }
    }
    int myRole = -1;
    private void parsData( String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<WeiXiuOrderDetailEntry>>() {}.getType();
        List<WeiXiuOrderDetailEntry> list = gson.fromJson(json,listType);
        if(list != null && list.size() > 0){
            WeiXiuOrderDetailEntry entry = list.get(0);
            tvTaskId.setText(entry.getID()+"");
            tvLeiXing.setText(entry.getTypeName());
            tvOrderstate.setText(entry.getTaskStatus());
            tvHaoshi.setText("已耗时: "+entry.getUseTime());
            tvOrderTitle.setText(entry.getRealseName()+" 发布于 "+entry.getRealseTime());
            /**
             * 判断下是否是受理人或者发起人
             */
            if (userEntry != null) {
//                if (entry.getRealseName().equals(userEntry.getUserName())) {
//                    //发起人
//                    myRole = 0;
//                }
                if (entry.getAcceptName() != null &&entry.getAcceptName().equals(userEntry.getUserName())) {
                    //受理人
                    myRole = 1;
                } else if (entry.getTaskStatus().equals("未受理")) {//未受理状态
                    myRole = 1;
                }else if(entry.getAcceptName() == null){
                    myRole = 1;
                }

            } else {
                Log.e("lxs", "userentry == null,没有进行判断角色");
            }
        }
    }
    String mainId;
    public  int getMyRole(){
        return myRole;
    }
    public  String getorderstates(){
        return tvOrderstate.getText().toString();
    }
    /**
     * 设置view上的数据
     * @param taskId
     * @param LeiXing
     * @param orderstate
     * @param HaoShi
     */
    public void SetViewData(String taskId,String LeiXing,String orderstate,String HaoShi){
        tvTaskId.setText(taskId);
        tvLeiXing.setText(LeiXing);
        tvOrderstate.setText(orderstate);
        tvHaoshi.setText("已耗时: "+HaoShi);
    }


    @OnClick({R.id.order_cancle_view_tv, R.id.order_detatls_menu, R.id.order_handle_layout, R.id.order_details_replay_show_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_detatls_menu:
                if (orderHandleLayout.getVisibility() == View.VISIBLE) {
                    orderHandleLayout.setVisibility(View.GONE);
                } else {
                    orderHandleLayout.setVisibility(View.VISIBLE);
                    initStatusAdapter();
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

    MaterialDialog dialogTip;

    private void initStatusAdapter() {
            list = new ArrayList<String>();
             list.add("转移分配");
            if (getorderstates().equals("暂停")) {
                list.add("取消暂停");
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            if (getorderstates().equals("处理中")) {
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
                        Log.e("lxs", "onItemClick:----> " + position);
                        if (list.get(position).equals("转移分配") || list.get(position).equals("转移工单")) {//工单转移
                            Log.e("lxs", "onItemClick:工单转移----> ");
                            detailsFragment.ShouLiRen();
                        }
                        if (list.get(position).equals("取消暂停")) {
                            detailsFragment.updataOderstate("3",userEntry.getID());
                        }

                        if (list.get(position).equals("暂停")) {//暂停工单
                            dialogTip = new MaterialDialog(ctx)
                                    .setTitle("提示")
                                    .setMessage("确定暂停该工单?")
                                    .setPositiveButton("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            detailsFragment.updataOderstate("4",userEntry.getID());
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
