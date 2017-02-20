package com.y.w.ywker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpushdemo.ExampleUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.y.w.ywker.ActivityOrderTypeInfo;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.fragment.FragmentDynamic;
import com.y.w.ywker.fragment.FragmentGongNengList;
import com.y.w.ywker.fragment.FragmentHome;
import com.y.w.ywker.fragment.FragmentPersional;
import com.y.w.ywker.service.UpdateService;
import com.y.w.ywker.utils.ActivityManager;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.views.MaterialDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by lxs on 16/4/12.
 * 主页Home
 */

public class HomeActivity extends SuperActivity implements View.OnClickListener {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar homeToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView homeActivityTitle;
    @Bind(R.id.home_viewpager_tab)
    SmartTabLayout homeViewpagerTab;
    @Bind(R.id.home_viewpager)
    ViewPager homeViewpager;
    @Bind(R.id.my_none_view)
    Button myNoneView;

    private Class[] fragments = new Class[]{
            FragmentDynamic.class,
            FragmentHome.class,
            FragmentGongNengList.class,
            FragmentPersional.class
    };
    private String titles[] = new String[]{
            "首页",
            "工单",
            "工作",
            "我的"
    };

    private int resid[] = new int[]{
            R.drawable.home_index_drawable,
            R.drawable.home_dynamic_drawable,
            R.drawable.home_contacts_drawable,
            R.drawable.home_personial_drawable
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        isUpdateApp();
        if (homeToolbar != null) {
            setSupportActionBar(homeToolbar);
        }
        myNoneView.setVisibility(View.GONE);
        getSupportActionBar().setTitle("");
        homeActivityTitle.setText("首页");
        FragmentPagerItems pages = new FragmentPagerItems(this);
        for (int i = 0; i < titles.length; i++) {
            pages.add(FragmentPagerItem.of(titles[i], fragments[i]));
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);
        homeViewpager.setAdapter(adapter);
        homeViewpagerTab.setViewPager(homeViewpager);
        for (int i = 0; i < resid.length; i++) {
            ((ImageView) homeViewpagerTab.getTabAt(i).findViewById(R.id.home_custom_tab_icon)).setImageResource(resid[i]);
        }
        homeViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                homeActivityTitle.setText(titles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //推送注册
        JPushInterface.init(getApplicationContext());
        registerMessageReceiver();
    }
    OfflineDataManager offlineDataManager;
    private void isUpdateApp() {
        offlineDataManager = OfflineDataManager.getInstance(this);
        String code = offlineDataManager.getVersionCode();
        if(code.equals("")){
            return;
        }
        int local =  getlocalVersion();
        int servicecode = Integer.parseInt(code);
        Log.e("lxs", "onCreate:local "+local+",serviceCode"+servicecode);
        if(local <  servicecode){
            updateVersion();
        }
    }
    private MaterialDialog dialogTip;
    /**
     * 版本更新
     */
    public void updateVersion(){
        dialogTip = new MaterialDialog(this)
                .setTitle("版本更新提示")
                .setMessage("eweic有了新版本,是否更新?")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startService(new Intent(HomeActivity.this, UpdateService.class));
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
    /**
     * 获取本地版本号
     * @return
     */
    public int getlocalVersion(){
        int localversion = 0;
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            localversion = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localversion;
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @OnClick({R.id.btn_bind_devices, R.id.btn_new_workorder})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_devices:
                Utils.start_Activity(HomeActivity.this, BindDevicesActivity.class,
                        new YBasicNameValuePair[]{
                                new YBasicNameValuePair("fromSource", "1")
                        });
                break;
            case R.id.btn_new_workorder:
                Utils.start_Activity(this,ActivityOrderTypeInfo.class,new YBasicNameValuePair[]{new YBasicNameValuePair("title", "请选择发布工单类型")});
//                Utils.start_Activity(this, ActivityWorkOrderStatusInfo.class, ConstValues.RESULT_FOR_PICKER_ORDER_TYPES,
//                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "工单类型")});
//                Utils.start_Activity(HomeActivity.this, NewWorkOrderActivity.class, new YBasicNameValuePair[]{});
                break;
            default:
                break;
        }
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                ActivityManager.getInstance().finshAllActivities();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
