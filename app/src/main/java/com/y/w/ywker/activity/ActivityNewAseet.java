package com.y.w.ywker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/6/29.
 * 新建设备
 */
public class ActivityNewAseet extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.btn_back_devices_info)
    ImageView btnBackDevicesInfo;
    @Bind(R.id.tv_post)
    TextView tvPost;
    @Bind(R.id.et_type)
    EditText etType;
    @Bind(R.id.et_aseetName)
    EditText etAseetName;
    @Bind(R.id.et_pinpai)
    EditText etPinpai;
    @Bind(R.id.et_xinghao)
    EditText etXinghao;
    @Bind(R.id.et_XuLieHao)
    EditText etXuLieHao;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_aseet);
        ButterKnife.bind(this);
        layoutCommToolbarTitle.setText("创建新的设备");

        sp =  getSharedPreferences("lxs", Activity.MODE_PRIVATE);
        String AseetName = sp.getString("AseetName","");
        String XingHao =  sp.getString("XingHao","");
        String PinPai =  sp.getString("PinPai","");

        LOG.e(this, "传递过来后 AseetNam = " + AseetName);
        if(!AseetName.equals("")){
            etAseetName.setText(AseetName);
        }
        if (!XingHao.equals("")){
            etXinghao.setText(XingHao);
        }
        if (!PinPai.equals("")){
            etPinpai.setText(PinPai);
        }
    }

    private HashMap<String,String> bindMap = new HashMap<String,String>();
    @OnClick({R.id.btn_back_devices_info, R.id.tv_post})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_devices_info:
                finish();
                break;
            case R.id.tv_post:
                String type = etType.getText().toString().trim();//设备类别
                String AseetName = etAseetName.getText().toString().trim();//设备名称
                String AseetPinPai = etPinpai.getText().toString().trim();
                String AseetXingHao = etXinghao.getText().toString().trim();
                String AseetXuLieHao = etXuLieHao.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    Utils.showToast(this,"类别不能为空");
                    return;
                }
                if (TextUtils.isEmpty(AseetName)){
                    Utils.showToast(this,"设备名称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(AseetPinPai)){
                    Utils.showToast(this,"设备品牌不能为空");
                    return;
                } if (TextUtils.isEmpty(AseetXingHao)){
                    Utils.showToast(this,"设备型号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(AseetXuLieHao)){
                    Utils.showToast(this,"设备序列号不能为空");
                    return;
                }
//                MainID：服务商主键
//                ClientID：客户主键
//                ClientCode：客户编号
//                ContactID：联系人主键
//                InTime：操作时间
//                UserID：操作人
//                AssetType：设备类别
//                Brand：品牌
//                Model：型号
//                AssetName：设备名称
//                AssetXuLie:序列号
//                sp.getString("ClientID","");
                OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
                bindMap.put("MainID", offlineDataManager.getMainID());
                bindMap.put("UserID",offlineDataManager.getUserID());
                bindMap.put("InTime", TimeUtils.getTime(System.currentTimeMillis()));
                bindMap.put("AssetType",type);
                bindMap.put("Brand",AseetPinPai);
                bindMap.put("Model",AseetXingHao);
                bindMap.put("AssetName",AseetName);
                bindMap.put("AssetXuLie",AseetXuLieHao);
                bindMap.put("ClientID",sp.getString("ClientID",""));
                bindMap.put("ContactID",sp.getString("ClientCode",""));
                bindMap.put("ClientCode",sp.getString("ClientCode1",""));

//                Utils.showToast(this,"此功能有待完善");

                proDialog = new ProgressDialog(this);
                proDialog.setTitle("提示");
                proDialog.setMessage("正在提交");
                proDialog.show();
                publicOrder(bindMap);
                break;
        }
    }

    @Override
    protected void onDestroy() {//清楚sp中保存的数据
        sp.edit().clear();
        sp.edit().commit();
        super.onDestroy();
    }

    /**
     * 提交
     * @param map
     */
    private void publicOrder(HashMap map) {

        YHttpManagerUtils managerUtils = new YHttpManagerUtils(this, ConstValues.NEW_ASEET_URL, mHandler, getClass().getName());
        managerUtils.startPostRequest(map);
    }
    private ProgressDialog proDialog;
    private Handler mHandler = new MyHandler(this);
    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity fragment) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            if (proDialog != null){
                proDialog.dismiss();
            }

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:

                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(getBaseContext(), "新增失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        Toast.makeText(getBaseContext(), "新增成功", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
