package com.y.w.ywker.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.y.w.ywker.R;
import com.y.w.ywker.utils.OfflineDataManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/4.
 */
public class ActivitySetting extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.setting_switch_all_dynamic)
    SwitchButton settingSwitchAllDynamic;
    @Bind(R.id.setting_switch_order_status)
    SwitchButton settingSwitchOrderStatus;
    @Bind(R.id.setting_switch_order_replay)
    SwitchButton settingSwitchOrderReplay;
    @Bind(R.id.setting_switch_order_handle)
    SwitchButton settingSwitchOrderHandle;
    @Bind(R.id.setting_switch_inner_msg)
    SwitchButton settingSwitchInnerMsg;
    @Bind(R.id.setting_switch_system_msg)
    SwitchButton settingSwitchSystemMsg;
    @Bind(R.id.setting_btn_back)
    ImageView settingBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        layoutCommToolbarTitle.setText("设置");

        initSwitch();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isAllClick = false;

    private void initSwitch() {
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);

        /**
         * 根据存储状态初始化
         */

        boolean on1 = offlineDataManager.getAllDynamicOn();
        boolean on2 = offlineDataManager.getInnerMsgOn();
        boolean on3 = offlineDataManager.getOrderHandleOn();
        boolean on4 = offlineDataManager.getOrderReplayOn();
        boolean on5 = offlineDataManager.getOrderDynamicOn();
        boolean on6 = offlineDataManager.getSystemMsgOn();

        Log.e("TAG", "设置动态");

        settingSwitchAllDynamic.setChecked(on5 && on3 && on4);
        settingSwitchInnerMsg.setChecked(on2);
        settingSwitchOrderHandle.setChecked(on3);
        settingSwitchOrderReplay.setChecked(on4);
        settingSwitchOrderStatus.setChecked(on5);
        settingSwitchSystemMsg.setChecked(on6);

        settingSwitchAllDynamic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isAllClick = true;
                return false;
            }
        });

        /**
         * 设置监听
         */
        settingSwitchAllDynamic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isAllClick){
                    OfflineDataManager.getInstance(ActivitySetting.this).setOrderHandleOn(isChecked);
                    OfflineDataManager.getInstance(ActivitySetting.this).setOrderReplayOn(isChecked);
                    OfflineDataManager.getInstance(ActivitySetting.this).setOrderDynamicOn(isChecked);
                    settingSwitchOrderHandle.setChecked(isChecked);
                    settingSwitchOrderStatus.setChecked(isChecked);
                    settingSwitchOrderReplay.setChecked(isChecked);
                }else{
                    // Do Nothing
                }

                isAllClick = false;
            }
        });

        //工单动态操作
        settingSwitchOrderHandle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OfflineDataManager.getInstance(ActivitySetting.this).setOrderHandleOn(isChecked);
                settingSwitchAllDynamic.setChecked(isAllDynamic());
            }
        });

        settingSwitchOrderReplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OfflineDataManager.getInstance(ActivitySetting.this).setOrderReplayOn(isChecked);
                settingSwitchAllDynamic.setChecked(isAllDynamic());
            }
        });

        settingSwitchOrderStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OfflineDataManager.getInstance(ActivitySetting.this).setOrderDynamicOn(isChecked);
                settingSwitchAllDynamic.setChecked(isAllDynamic());
            }
        });

        //消息
        settingSwitchSystemMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OfflineDataManager.getInstance(ActivitySetting.this).setSystemMsgOn(isChecked);
            }
        });

        settingSwitchInnerMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OfflineDataManager.getInstance(ActivitySetting.this).setInnerMsgOn(isChecked);
            }
        });
    }

    private boolean isAllDynamic(){
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        boolean on3 = offlineDataManager.getOrderHandleOn();
        boolean on4 = offlineDataManager.getOrderReplayOn();
        boolean on5 = offlineDataManager.getOrderDynamicOn();
        return on3 && on4 && on5;
    }

    @OnClick({R.id.setting_btn_back})
    public void onClick(View v) {
        finish();
    }
}
