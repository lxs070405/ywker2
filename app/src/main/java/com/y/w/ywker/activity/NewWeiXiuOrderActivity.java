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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ActivityPickerTeam;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.ParentSelectorActivity;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.ConnectEntry;
import com.y.w.ywker.entry.SerializableMap;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.OfflineDataManager;
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
 * 新增维修工单
 */
public class NewWeiXiuOrderActivity extends SuperActivity {


    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.new_order_back)
    ImageView newOrderBack;
    @Bind(R.id.new_order_public)
    TextView newOrderPublic;
    @Bind(R.id.titledsc)
    TextView titledsc;
    @Bind(R.id.tv_leiXing)
    TextView tvLeiXing;
    @Bind(R.id.ll_LeiXing)
    LinearLayout llLeiXing;
    @Bind(R.id.tv_KeHuName)
    TextView tvKeHuName;
    @Bind(R.id.ll_KeHuName)
    LinearLayout llKeHuName;
    @Bind(R.id.et_SuerName)
    EditText etSuerName;
    @Bind(R.id.et_SureConacts)
    EditText etSureConacts;
    @Bind(R.id.tv_ShouLiRen)
    TextView tvShouLiRen;
    @Bind(R.id.ll_ShouLiRen)
    LinearLayout llShouLiRen;
    @Bind(R.id.new_order_arrrow2_text1)
    TextView newOrderArrrow2Text1;
    @Bind(R.id.et_desc)
    EditText etDesc;
    String keyManId = "MainID";//服务商主键
    String keyClientId = "ClientID";//：客户主键
    String keyWriteId = "WriteID";//：发布人主键，及登录人主键
    String keyWriteAddr = "WriteAdr";//：发布时所处地点
    String keySheetDetails = "TaskDetail";//：描述
    String keySheetLevel = "TaskPriority";//：优先级
    String keyTeamId = "TeamID";//：服务组主键
    String keyTeamerId = "AcceptID";//：受理人主键
    String keyTypeID = "AssetTypeID";//设备类型ID
    String keyLianXiRen = "LinkName";//联系人
    String keyPhone = "LinkTel";//联系人电话
    String keyAssetID = "AssetID";//设备ID
    String keyRepaireSummary = "RepaireSummary";//维修总结
    String keyRepairSchedule = "RepairSchedule";//维修进度Id
    String keyRepairAssetSummary = "RepairAssetSummary";//维修总结
    private YHttpManagerUtils httpManagerUtils;
    private  String userId;
    String mainId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newweixiuorder);
        ButterKnife.bind(this);
        mainId = OfflineDataManager.getInstance(this).getMainID();
        initMap();
    }

    private void initMap() {
        //初始化map
        map.put(keyRepaireSummary,"");
        map.put(keyRepairSchedule,"");
        map.put(keyRepairAssetSummary,"");
        map.put(keyTeamId, String.valueOf(""));
        map.put(keyManId, mainId);
        map.put(keyClientId, String.valueOf(""));
        map.put(keySheetDetails, String.valueOf(""));
        map.put(keySheetLevel, "03");
        map.put(keyTeamerId, String.valueOf(""));
        map.put(keyTeamId, String.valueOf(""));
        map.put(keyWriteAddr, String.valueOf(""));
        map.put(keyWriteId,  OfflineDataManager.getInstance(this).getUserID());
        map.put(keyWriteAddr, String.valueOf(""));
        map.put(keyTypeID,"");
        map.put(keyAssetID,"");
        SerializableMap outMap = (SerializableMap) getIntent().getSerializableExtra("new_order_map");
        if (outMap != null) {
            _outMap = outMap.getMap();
          if (_outMap.get("from").equals("devices")) {//来自设备入口发布工单
                map.put(keyClientId, (String) _outMap.get("ClientID"));
               String AssetID = getIntent().getStringExtra("AssetID");
                Log.e("lxs", "onCreate: "+ AssetID );
                map.put(keyAssetID,AssetID);
                tvKeHuName.setText((String) _outMap.get("ClientName"));
                tvLeiXing.setText((String) _outMap.get("TypeName"));
                map.put(keyTypeID,(String) _outMap.get("TypeID"));
              Log.e("lxs", "onCreate:_outMap.get(\"TypeID\") "+ _outMap.get("TypeID") );
            }
        }
    }
    Map _outMap = null;
    /**
     * 用于提交工单使用的HashMap
     */
    private HashMap<String, String> map = new HashMap<String, String>();

    @OnClick({R.id.new_order_back,R.id.ll_LeiXing, R.id.ll_KeHuName,
            R.id.ll_ShouLiRen, R.id.new_order_public})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_order_back:
                TiShi();
                break;
            case R.id.ll_LeiXing://设备类型
                if(_outMap != null){
                    return;
                }
                Intent i = new Intent(this, ActivityDevicesType.class);
                i.putExtra("weixiu","lxs");
                startActivityForResult(i, ConstValues.RESULT_FOR_DEVICES_TYPE);
                break;
            case R.id.ll_KeHuName://客户
                if(_outMap != null){
                    return;
                }
                Utils.start_ActivityResult(this, ParentSelectorActivity.class, ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "选择客户")});
                break;
            case R.id.ll_ShouLiRen://受理人
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Utils.start_ActivityResult(this, ActivityPickerTeam.class, ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "选择服务组")
                        });
                break;
            case R.id.new_order_public://发布
                PostData();
                break;
        }
    }
    private void PostData() {
        map.put(keySheetDetails, etDesc.getText().toString().trim());//获取描述信息
        if (map.get(keyTypeID).equals("")) {
            Toast.makeText(this, "请填写设备类型信息", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 判断客户
         */
        if (map.get(keyClientId).equals("")) {
            Toast.makeText(this, "请填写客户信息", Toast.LENGTH_SHORT).show();
            return;
        }
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
        if (map.get(keyTeamId).equals("")) {
            Toast.makeText(this, "请填写受理人信息", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 判断描述
         */
        if (map.get(keySheetDetails).equals("")) {
            Toast.makeText(this, "请填写描述", Toast.LENGTH_SHORT).show();
            return;
        }
        map.put(keyLianXiRen,lianxiren);
        map.put(keyPhone,phone);
        proDialog = new ProgressDialog(this);
        proDialog.setTitle("提示");
        proDialog.setMessage("正在发布");
        proDialog.show();
        if (proDialog.isShowing()) {
            newOrderPublic.setClickable(false);
            publicOrder(map);
        }
    }

    /**
     * 发布工单
     */
    private void publicOrder(HashMap map) {
        YHttpManagerUtils managerUtils = new YHttpManagerUtils(this, ConstValues.NEW_weixiu_ORDER_URL, mHandler, getClass().getName());
        managerUtils.startPostRequest(map);
    }

    private Handler mHandler = new MyHandler(this, 1);
    private ProgressDialog proDialog;



    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        private int i;

        public MyHandler(AppCompatActivity fragment, int i) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.i = i;
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
                        if (i == 2) {
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        break;
                    case ConstValues.MSG_ERROR:
                        if (i == 2) {
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        Toast.makeText(getBaseContext(), "工单提交失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        if (i == 2) {
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        if (i == 2) {
                            return;
                        }
                        newOrderPublic.setClickable(true);
                        break;
                    case ConstValues.MSG_SUCESS:
                        if (i == 2) {
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
            case ConstValues.RESULT_FOR_DEVICES_TYPE://设备类型
                Log.e("lxs", "设备详情页onActivityResult: " + "设备类型名称返回" + result
                +"keyTypeID---->"+ids);
                tvLeiXing.setText(result);
                if(ids != null){
                    map.put(keyTypeID, ids);
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT://客户
                if (result != null) {
                    tvKeHuName.setText(result);
                }
                if (ids != null && ids.contains(",")) {
                    String clientIds[] = ids.split(",");
                    map.put(keyClientId, clientIds[0]);
//                    if (clientIds != null && clientIds.length >= 2) {
//                        map.put(keyClientId, clientIds[0]);
//                    }
                }
                break;
            case ConstValues.RESULT_FOR_PICKER_SERVICES_ROOT://受理服务组
                if (ids != null && !ids.equals("")) {
                    String[] idssplit = ids.split(",");
                    map.put(keyTeamId, idssplit[0]);
                    map.put(keyTeamerId, idssplit[1]);
                }
                if (result != null) {
                    tvShouLiRen.setText(result);
                }
                break;
        }
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
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            TiShi();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
