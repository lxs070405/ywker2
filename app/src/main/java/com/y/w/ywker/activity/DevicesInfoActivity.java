package com.y.w.ywker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.ParentSelectorActivity;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.DevicesEntry;
import com.y.w.ywker.entry.PicEntry;
import com.y.w.ywker.entry.SerializableMap;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.Base64Utils;
import com.y.w.ywker.utils.ImgeUtils;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.PictureUtils;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.EditFocusLayout;
import com.y.w.ywker.views.MaterialDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/14.
 * 设备信息页面
 */
public class DevicesInfoActivity extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.btn_back_devices_info)
    ImageView btnBackDevicesInfo;
    @Bind(R.id.devices_info_client_name_tv)
    TextView devicesInfoClientNameTv;
    @Bind(R.id.devices_info_client_connect_name_tv)
    TextView devicesInfoClientConnectNameTv;
    @Bind(R.id.devices_info_name_tv)
    TextView devicesInfoNameTv;
    @Bind(R.id.devices_info_pinpai_tv)
    TextView devicesInfoPinpaiTv;

    @Bind(R.id.devices_info_xuliehao_tv)
    TextView devicesInfoXuliehaoTv;
    @Bind(R.id.devices_info_xinghao_tv)
    TextView devicesInfoXinghaoTv;
    @Bind(R.id.devices_info_btn_sure)
    Button devicesInfoBtnSure;

    /**
     * 绑定设备,回复工单,创建工单
     * <p/>
     * 回复:未绑定 -> 绑定 -> 回复
     * 绑定  -> 绑定工单 -> 创建工单
     * 创建工单 -> 未绑定 - > 绑定 - > 创建
     * replay  bind  create
     */

    int requestCode = -1;
    @Bind(R.id.devices_info_devices_name_layout)
    LinearLayout devicesInfoDevicesNameLayout;
    @Bind(R.id.devices_info_pinpai_layout)
    LinearLayout devicesInfoPinpaiLayout;
    @Bind(R.id.devices_info_xinghao_layout)
    LinearLayout devicesInfoXinghaoLayout;
    @Bind(R.id.ll_Xuliehao)
    LinearLayout llXuliehao;
    @Bind(R.id.tv_XunJianDsc)
    EditText tvXunJianDsc;
    @Bind(R.id.ll_BeiZhu)
//    LinearLayout llBeiZhu;
    EditFocusLayout llBeiZhu;
    @Bind(R.id.image)
    ImageView iv0;
    @Bind(R.id.image1)
    ImageView iv1;
    @Bind(R.id.image2)
    ImageView iv2;
    @Bind(R.id.image4)
    ImageView addImage;
    @Bind(R.id.ll_TuPian)
    LinearLayout llTuPian;
    @Bind(R.id.tv_fenGe)
    TextView tvFenGe;
    @Bind(R.id.XunJianDsc)
    TextView XunJianDsc;
    @Bind(R.id.ll_xianshiBeizhu)
    LinearLayout llXianshiBeizhu;
    private String devicesInfo = "";
    private String codeId = "";
    private String bind_xunjian = "";
    private DevicesEntry entry;
    private HashMap<String, String> bindMap = new HashMap<String, String>();

    private YHttpManagerUtils httpManagerUtils;

    /**
     * 未绑定 - > ""
     * 已绑定 - > "设备code"
     */
    private SharedPreferences.Editor edit;
    private String QBCode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCameraTempFile(savedInstanceState);
        setContentView(R.layout.activity_devices_info);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        QBCode = getIntent().getStringExtra("QBCode");
        initmap();
        initData();

    }

    /**
     * 创建调用系统照相机待存储的临时文件
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    //请求相册
    private static final int REQUEST_PICK = 101;
    private StringBuffer sbbuffer = new StringBuffer("");
    private StringBuffer sbbase = new StringBuffer("");
    private HashMap<String, HashMap<String, Bitmap>> datapicmap = new HashMap<>();

    boolean isIv = false;
    boolean isIv1 = false;
    /**
     * 设置图片监听
     */
    private void setlistener() {
        iv0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                datapicmap.remove("1");
                iv0.setVisibility(View.GONE);
                isIv = true;
                return true;
            }
        });
        iv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                datapicmap.remove("2");
                iv1.setVisibility(View.GONE);
                isIv1 = true;
                return true;
            }
        });
        iv2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                datapicmap.remove("3");
                iv2.setVisibility(View.GONE);
                return true;
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i =  typeNum%3;
                if(i == 1){
                    type = 2;
                }else if(i == 2){
                    type = 3;
                }else {
                    type = 1;
                }

                uploadHeadImage();
            }
        });
    }
    private int typeNum = 0;
    private ArrayList<ImageView> arrlist = new ArrayList<>();
    /**
     * 上传头像
     */
    private void uploadHeadImage() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_addaseetxunjian, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });

        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, REQUEST_CAPTURE);
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到调用系统图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
                popupWindow.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private String Type = "";

    private void initData() {
        if (entry != null) {
            tvFenGe.setText("");
            if (Type.equals("createweixiu")) {
                devicesInfoBtnSure.setVisibility(View.VISIBLE);
                devicesInfoBtnSure.setText("创建维修工单");
                layoutCommToolbarTitle.setText("设备信息");
            }else
            if(Type.equals("zongjietype")){
                devicesInfoBtnSure.setVisibility(View.VISIBLE);
                devicesInfoBtnSure.setText("增加设备");
                layoutCommToolbarTitle.setText("设备信息");
            }else
            if (Type.equals("xunjiantype")) {
                devicesInfoBtnSure.setVisibility(View.GONE);
            } else if (Type.equals("create")) {
                devicesInfoBtnSure.setVisibility(View.VISIBLE);
                devicesInfoBtnSure.setText("创建工单");
                layoutCommToolbarTitle.setText("设备信息");
            } else if (Type.equals("replay")) {
                devicesInfoBtnSure.setVisibility(View.VISIBLE);
                devicesInfoBtnSure.setText("回复工单");
                layoutCommToolbarTitle.setText("回复工单");
            }
            int assetId = entry.getID();

            if (assetId > 0 && entry.getIsHavePic() > 0) {
                httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_DEVICESINFO_PIC, assetId, offlineDataManager.getMainID()), new MyHandler(this, 2), this.getClass().getName());
                httpManagerUtils.startRequest();
            } else if (assetId > 0 && entry.getIsHavePic() == 0) {
                llTuPian.setVisibility(View.GONE);
            }
            //初始化信息
            //设备类型
            devicesInfoNameTv.setText(entry.getTypeName());
            //品牌
            devicesInfoPinpaiTv.setText(entry.getBrandName());
            //型号
            devicesInfoXinghaoTv.setText(entry.getModelName());
            //序列号 有序列号显示序列号栏没有序列号不显示此栏
            if (entry.getAssetXuLie().equals("")) {
                llXuliehao.setVisibility(View.GONE);
            } else {
                devicesInfoXuliehaoTv.setText(entry.getAssetXuLie());
            }
            if (entry.getRemark().equals("")) {
                llBeiZhu.setVisibility(View.GONE);
            } else {
                llBeiZhu.setVisibility(View.GONE);
                llXianshiBeizhu.setVisibility(View.VISIBLE);
                XunJianDsc.setText(entry.getRemark());
            }

            //客户
            devicesInfoClientNameTv.setText(entry.getClientName());
//            //联系人
//            devicesInfoClientConnectNameTv.setText(entry.getContactName() + "," + entry.getDepartName());

        } else {
            devicesInfoBtnSure.setText("绑定设备");
            layoutCommToolbarTitle.setText("绑定设备");
            setlistener();
        }
    }

    OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
    private String mainID = "";
    private String devicesData;
    private void initmap() {
        SharedPreferences sp = getSharedPreferences("lxs", Activity.MODE_PRIVATE);
        edit = sp.edit();
        bind_xunjian = getIntent().getStringExtra("bind_xunjian");
        Type = getIntent().getStringExtra("bind_type");
        requestCode = getIntent().getIntExtra("requestCode", -1);
        devicesData =devicesInfo = getIntent().getStringExtra("devices_info");
        codeId = getIntent().getStringExtra("codeID");
        if (!TextUtils.isEmpty(codeId)) {
            Log.e("lxs", "codeID : " + codeId);
        }
        if (devicesInfo != null && !devicesInfo.equals("")) {
            Gson gson = new Gson();
            entry = gson.fromJson(devicesInfo, DevicesEntry.class);
        }
        mainID = offlineDataManager.getMainID();
        bindMap.put("MainID", mainID);
        bindMap.put("ClientID", "");
        bindMap.put("ClientCode", "");
        bindMap.put("ContactID", "");
        bindMap.put("InTime", "");
        bindMap.put("BrandID", "");
        bindMap.put("ModelID", "");
        bindMap.put("UserID", offlineDataManager.getUserID());
        bindMap.put("AssetName", "");
        bindMap.put("AssetXuLie", "");
        bindMap.put("TypeName", "");
        bindMap.put("keyTypeID", "");
        bindMap.put("AssetID","");
    }


    @OnClick({R.id.devices_info_btn_sure, R.id.devices_info_devices_name_layout, R.id.btn_back_devices_info,
            R.id.devices_info_client_layout, R.id.devices_info_client_connect_layout,
            R.id.devices_info_pinpai_layout, R.id.devices_info_xinghao_layout, R.id.ll_Xuliehao})
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back_devices_info) {
            finish();
        }
        String text = devicesInfoBtnSure.getText().toString();
        if (v.getId() == R.id.devices_info_btn_sure) {
//            if(text.equals("增加设备")){
//                Intent intent = new Intent(DevicesInfoActivity.this,ActivityZongJie.class);
//                intent.putExtra("AseetId", bindMap.get("AssetID"));
//                String AseetName = devicesInfoNameTv.getText().toString()+" "+
//                        devicesInfoPinpaiTv.getText().toString()+" "+
//                        devicesInfoXinghaoTv.getText().toString();
//                intent.putExtra("AseetName",AseetName);
//                intent.putExtra("QBCode",QBCode);
//                Log.e("lxs", "onClick: "+AseetName);
//                setResult(ConstValues.ZONGJIE,intent);
//                finish();
//            }else
            if (text.equals("回复工单")) {
                Intent i = new Intent();
                i.putExtra("devices_replay_result", devicesData);
                Log.e("lxs", "回传的数据-------------" + devicesData);
                setResult(ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER, i);
                finish();
            } else if (text.equals("创建工单")) {//将联系人信息传过去
              CreatOrder("order");//创建工单
            } else if (text.equals("绑定设备")) {
                bindMap.put("InTime", TimeUtils.getTime(System.currentTimeMillis()));
                bindDevices();
            }else if (text.equals("创建维修工单")) {//将联系人信息传过去
                CreatOrder("weixiu");//创建工单
            }
        }
        /**
         * 不进行任何操作了
         */
        if (!text.equals("绑定设备")) {
            return;
        }
        switch (v.getId()) {
//            case R.id.devices_info_client_connect_layout:
////                ConstValues.isnew = false;
//                Utils.start_ActivityResult(this, ParentSelectorActivity.class, ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT,//RESULT_FOR_PICKER_CLIENT_ROOT
//                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "选择客户")});
//                break;
            case R.id.devices_info_client_layout://客户
//                ConstValues.isnew = false;
                Utils.start_ActivityResult(this, ParentSelectorActivity.class, ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "选择客户")});
                break;

            case R.id.devices_info_pinpai_layout:
                if (bindMap.get("ClientID").equals("")) {
                    Toast.makeText(this, "请先选择客户", Toast.LENGTH_SHORT).show();
                    return;
                }
//                startActivityF(ConstValues.RESULT_FOR_DEVICES_PINPAI, "选择品牌",
//                        bindMap.get("ClientID"), bindMap.get("ContactID"), "Brand", "", "");
                Intent intent = new Intent(DevicesInfoActivity.this, ActivityXingHaoList.class);
                intent.putExtra("mode","1");
                intent.putExtra("MainID", bindMap.get("MainID"));
                startActivityForResult(intent,ConstValues.RESULT_FOR_DEVICES_PINPAI);
                break;
            case R.id.devices_info_xinghao_layout:
                if (bindMap.get("ClientID").equals("")) {
                    Toast.makeText(this, "请先选择客户", Toast.LENGTH_SHORT).show();
                    return;
                }
               String s = bindMap.get("keyTypeID");
                Log.e("lxs", "onClick: keyTypeID--->"+s );
                if (s.equals("")) {
                    Toast.makeText(this, "请先选择设备类别", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bindMap.get("BrandID").equals("")) {
                    Toast.makeText(this, "请先选择设备品牌", Toast.LENGTH_SHORT).show();
                    return;
                }
//                startActivityF(ConstValues.RESULT_FOR_DEVICES_XINGHAO, "选择型号",
//                        bindMap.get("ClientID"), bindMap.get("ContactID"), "Model", "", "");
                Intent in = new Intent(DevicesInfoActivity.this, ActivityXingHaoList.class);
                in.putExtra("MainID", bindMap.get("MainID"));
                in.putExtra("keyTypeID", bindMap.get("keyTypeID"));
                in.putExtra("BrandID", bindMap.get("BrandID"));
                in.putExtra("mode","2");
                in.putExtra("ClientID",bindMap.get("ClientID"));
                startActivityForResult(in, ConstValues.RESULT_FOR_DEVICES_XINGHAO);
                if(beizhu != null){
                    tvXunJianDsc.setText(beizhu);
                }
                break;
            case R.id.devices_info_devices_name_layout://设备类型

                if (bindMap.get("ClientID").equals("")) {
                    Toast.makeText(this, "请先选择客户", Toast.LENGTH_SHORT).show();
                    return;
                }
//                startActivityF(ConstValues.RESULT_FOR_DEVICES_NAME, "选择设备类型",
//                        bindMap.get("ClientID"), bindMap.get("ContactID"), "", bindMap.get("BrandID"), bindMap.get("ModelID"));
                Intent i = new Intent(DevicesInfoActivity.this, ActivityDevicesType.class);
                startActivityForResult(i, ConstValues.RESULT_FOR_DEVICES_TYPE);
                break;
            case R.id.ll_Xuliehao:
                if (bindMap.get("ClientID").equals("")) {
                    Toast.makeText(this, "请先选择客户", Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils.start_ActivityResult(this, ActivityXuLieHao.class, ConstValues.RESULT_FOR_DEVICES_XULIEHAO,
                        new YBasicNameValuePair[]{new YBasicNameValuePair("title", "产品序列号")});
                break;
        }

    }

    /**
     * 创建工单
     */
    private void CreatOrder(String str) {
        if(str.equals("order")){
            if (entry != null && !TextUtils.isEmpty(entry.getClientID() + "") && !TextUtils.isEmpty("" + entry.getContactID())) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ClientID", entry.getClientID() + "");
                map.put("ContactID", entry.getContactID() + "");
                map.put("ClientName", entry.getClientName());
                map.put("ClientAdress", "");
                map.put("ContactName", entry.getContactName());
                map.put("from", "devices");
//                map.put("TypeID",entry.getTypeID()+"");
                Log.e("lxs", "onClick: "+entry.getID()+"设备创建工单" );
                SerializableMap outMap = new SerializableMap();
                outMap.setMap(map);
                Intent i = new Intent(this, NewWorkOrderActivity.class);
                i.putExtra("new_order_map", outMap);
                i.putExtra("AssetID",entry.getID()+"");
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "未得到设备的客户信息", Toast.LENGTH_SHORT).show();
            }
        }else {
            if (entry != null && !TextUtils.isEmpty(entry.getClientID() + "") && !TextUtils.isEmpty("" + entry.getContactID())) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ClientID", entry.getClientID() + "");
                map.put("ContactID", entry.getContactID() + "");
                map.put("ClientName", entry.getClientName());
                map.put("ContactName", entry.getContactName());
                map.put("from", "devices");
                map.put("TypeName",entry.getTypeName());
                map.put("TypeID",entry.getTypeID()+"");
                Log.e("lxs", "onClick: "+entry.getID()+"设备创建工单" );
                SerializableMap outMap = new SerializableMap();
                outMap.setMap(map);
                Intent i = new Intent(this, NewWeiXiuOrderActivity.class);
                i.putExtra("new_order_map", outMap);
                i.putExtra("AssetID",entry.getID()+"");

                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "未得到设备的客户信息", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private ProgressDialog proDialog;
    /**
     * 绑定设备
     */
    private void bindDevices() {
//        if(bindMap.get("AssetID").equals("0")){
//            Toast.makeText(this,"该设备未录入后台数据库中,请联系管理员",Toast.LENGTH_SHORT).show();
//            return;
//        }
        proDialog = new ProgressDialog(this);
        proDialog.setTitle("提示");
        proDialog.setMessage("正在提交");
        proDialog.show();
        httpManagerUtils = new YHttpManagerUtils(this, ConstValues.BIND_DEVICES_URL, new MyHandler(this, 1), getClass().getSimpleName());
        HashMap<String, String> bindmapdata = new HashMap<String, String>();
        bindmapdata.put("ID", codeId);
        bindmapdata.put("AssetID", bindMap.get("AssetID"));
        bindmapdata.put("AssetXuLie", bindMap.get("AssetXuLie"));
        bindmapdata.put("Remark", tvXunJianDsc.getText().toString());
        Iterator iter = datapicmap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            HashMap<String, Bitmap> datamap = (HashMap<String, Bitmap>) entry.getValue();
            Iterator iteror = datamap.entrySet().iterator();
            while (iteror.hasNext()) {
                Map.Entry entrydata = (Map.Entry) iteror.next();
                String key = (String) entrydata.getKey();
                Bitmap value = (Bitmap) entrydata.getValue();
                sbbuffer.append(key + "$");
                sbbase.append(Base64Utils.imgToBase64(value) + "$");
            }
        }
        bindmapdata.put("Pic", sbbuffer.toString());
        bindmapdata.put("Base64", sbbase.toString());
        bindmapdata.put("MainID",bindMap.get("MainID"));
        bindmapdata.put("ClientID",bindMap.get("ClientID"));
        bindmapdata.put("TypeID",bindMap.get("keyTypeID"));
        bindmapdata.put("BrandID",bindMap.get("BrandID"));
        bindmapdata.put("ModelID",bindMap.get("ModelID"));
//        Log.e("lxs", "上传图片的名称:" + sbbuffer.toString());
//        Log.e("lxs", "上传图片的内容:" + sbbase.toString());
        if (bindmapdata.get("Pic").equals("") && bindmapdata.get("AssetXuLie").equals("") && bindmapdata.get("Remark").equals("")) {
            Toast.makeText(this, "请填写选填项", Toast.LENGTH_SHORT).show();
            return;
        }
        bindmapdata.put("opId", offlineDataManager.getUserID());
        for (String key : bindmapdata.keySet()) {
            Log.e("lxs", "bindDevices: "+key + " : " + bindmapdata.get(key) );
        }
        httpManagerUtils.startPostRequest(bindmapdata);
        sbbuffer = new StringBuffer("");
        sbbase = new StringBuffer("");
    }


    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        private int i = 0;

        public MyHandler(AppCompatActivity fragment, int i) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.i = i;
        }

        @Override
        public void handleMessage(Message msg) {

            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if(proDialog != null && proDialog.isShowing()){
                            proDialog.dismiss();
                        }
                        if (i == 1)
                            Toast.makeText(getBaseContext(), "绑定设备失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        if(proDialog != null && proDialog.isShowing()){
                            proDialog.dismiss();
                        }
                        if (i == 1)
                            Toast.makeText(getBaseContext(), "绑定设备失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(proDialog != null && proDialog.isShowing()){
                            proDialog.dismiss();
                        }
                        if (i == 2) {
                            handlePicData((String) msg.obj);
                        }
                            /**
                             * 绑定设备
                             */
                            devicesInfo = (String) msg.obj;
                            if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                                Toast.makeText(DevicesInfoActivity.this, "设备绑定完成", Toast.LENGTH_SHORT).show();
                                //将信息设置到view上
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<DevicesEntry>>() {
                                }.getType();
                                List<DevicesEntry> _l = gson.fromJson(devicesInfo, type);
                                if (_l != null && _l.size() > 0) {
                                    entry = _l.get(0);
                                    devicesInfoPinpaiTv.setText(entry.getBrandName());
                                    devicesInfoXinghaoTv.setText(entry.getModelName());
                                    devicesInfo = gson.toJson(entry);
                                }
                                handleBtn();
                            }

                        break;
                }
            }
        }
    }

    private void handleBtn() {
        Log.e(getClass().getSimpleName(), "设备信息 := " + devicesInfo);
        if (Type.equals("create")) {
            devicesInfoBtnSure.setVisibility(View.GONE);
            dialogTip = new MaterialDialog(DevicesInfoActivity.this)
                    .setTitle("提示")
                    .setMessage("设备绑定完成,是否创建工单")
                    .setPositiveButton("创建", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                                                devicesInfoBtnSure.setText("创建工单");
                            CreatOrder("order");
                            dialogTip.dismiss();
                        }
                    }).setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                             finish();
                            dialogTip.dismiss();
                        }
                    });
            dialogTip.show();

        } else if (Type.equals("replay")) {
            devicesInfoBtnSure.setText("回复工单");
        }
        else {
            finish();
        }
    }

    private MaterialDialog dialogTip;
    private List<PicEntry> lists;

    /**
     * 解析图片数据
     */
    private void handlePicData(String json) {
        Log.e("lxs", "巡检设备详情:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PicEntry>>() {
        }.getType();
        lists = gson.fromJson(json, listType);
        if (lists != null && lists.size() > 0) {
            if (lists.size() == 1) {
                iv0.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .centerCrop()
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv0);
            }
            if (lists.size() == 2) {
                iv0.setVisibility(View.VISIBLE);
                iv1.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv0);
                Glide.with(this)
                        .load(lists.get(1).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv1);
            }
            if (lists.size() == 3) {
                iv0.setVisibility(View.VISIBLE);
                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv0);
                Glide.with(this)
                        .load(lists.get(1).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv1);
                Glide.with(this)
                        .load(lists.get(2).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv2);
            }
        }
    }

    /**
     * @param requestCode 请求标识
     * @param title       标题名
     * @param clientId    客户主键
     * @param connectId   联系人主键
     * @param device_type 设备名称
     * @param pinpaiid    品牌
     * @param xinghaoid   型号
     */
    private void startActivityF(int requestCode, String title, String clientId, String connectId, String device_type,
                                String pinpaiid, String xinghaoid) {
        Intent i = new Intent(this, ActivityPickerDevices.class);
        i.putExtra("title", title);
        i.putExtra("picker_devices", requestCode);
        i.putExtra("client_id", clientId);
        i.putExtra("connect_id", connectId);
        if (device_type.equals("")) {//选择名称列表
            i.putExtra("pinpai_id", pinpaiid);
            i.putExtra("xinghao_id", xinghaoid);
        } else {// 选择品牌型号
            i.putExtra("devices_type", device_type);
        }
        startActivityForResult(i, requestCode);
    }

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    private int type = 0;
    //调用照相机返回图片临时文件
    private File tempFile;
    String ids = "";
    String result = "";
    String AssetID = "";
    String beizhu = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
             ids = data.getStringExtra("_ids");
             result = data.getStringExtra("result");
             AssetID = data.getStringExtra("AssetID");
        }
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    typeNum++;
                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), Uri.fromFile(tempFile));
                    Log.e("lxs", "onActivityResult:图片 路径" + cropImagePath);
                    String picname = cropImagePath.substring(cropImagePath.lastIndexOf("/") + 1, cropImagePath.lastIndexOf(".")) + System.currentTimeMillis() + ".jpg";
                    Bitmap bitMap = PictureUtils.getSmallBitmap(cropImagePath);
                    if (type == 1) {
                        iv0.setImageBitmap(bitMap);
                        bitMap = ImgeUtils.zoomBitmap(bitMap,720,720);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("1", datamap);
                        iv0.setVisibility(View.VISIBLE);
                    } else if (type == 2) {
                        iv1.setImageBitmap(bitMap);
                        bitMap = ImgeUtils.zoomBitmap(bitMap,720,720);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("2", datamap);
                        iv1.setVisibility(View.VISIBLE);
                    } else if (type == 3) {
                        iv2.setImageBitmap(bitMap);
                        bitMap = ImgeUtils.zoomBitmap(bitMap,720,720);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("3", datamap);
                        iv2.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    typeNum++;
                    Uri uri = data.getData();
                    String cropImagePath = uri.getPath();
                    String picname= "";
                    if(cropImagePath.contains(".")){
                        picname = cropImagePath.substring(cropImagePath.lastIndexOf("/") + 1, cropImagePath.lastIndexOf(".")) + System.currentTimeMillis() + ".jpg";
                    }else {
                        picname = cropImagePath.substring(cropImagePath.lastIndexOf("/") + 1, cropImagePath.length()) + System.currentTimeMillis() + ".jpg";
                    }
                    Log.e("lxs", "onActivityResult:图片 路径" + picname);
                    Bitmap  bitMap = ImgeUtils.compressBitmap(null, null, this, uri, 2, false);
                    if (type == 1) {
                        iv0.setImageBitmap(bitMap);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("1", datamap);
                        iv0.setVisibility(View.VISIBLE);
                    } else if (type == 2) {
                        iv1.setImageBitmap(bitMap);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("2", datamap);
                        iv1.setVisibility(View.VISIBLE);
                    } else if (type == 3) {
                        iv2.setImageBitmap(bitMap);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("3", datamap);
                        iv2.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case ConstValues.RESULT_FOR_PICKER_CLIENT_ROOT:
                if (result != null) {
                    devicesInfoClientNameTv.setText(result);
                }
                if (ids != null && ids.contains(",")) {
                    String clientIds[] = ids.split(",");
                    if (clientIds != null && clientIds.length == 3) {
                        bindMap.put("ClientID", clientIds[0]);
                        bindMap.put("ClientCode", clientIds[2]);
                        bindMap.put("ContactID", clientIds[1]);
                        edit.putString("ClientID", clientIds[0]);
                        edit.putString("ClientCode", clientIds[1]);
                        edit.commit();
                    }
                }
                break;
            case ConstValues.RESULT_FOR_DEVICES_TYPE:
//                Log.e("lxs", "设备详情页onActivityResult: " + "设备类型名称返回" + result
//                +"keyTypeID---->"+ids);
                devicesInfoNameTv.setText(result);
                if(ids != null){
                    bindMap.put("keyTypeID", ids);
                }

                break;
            case ConstValues.RESULT_FOR_DEVICES_NAME:

                if (ids != null && !ids.equals("")) {
                    bindMap.put("AssetID", ids);
                }
//
//                if (result != null && !result.equals("")) {
//                    bindMap.put("AssetName", result);
//                    devicesInfoNameTv.setText(result);
//                    edit.putString("AseetName", result);
//                    edit.commit();
//                }
                break;
            case ConstValues.RESULT_FOR_DEVICES_PINPAI:
                if (ids != null && !ids.equals("")) {
                    bindMap.put("BrandID", ids);
                }
                if (result != null && !result.equals("")) {
                    devicesInfoPinpaiTv.setText(result);
                    edit.putString("PinPai", result);
                    edit.commit();
                }
                break;
            case ConstValues.RESULT_FOR_DEVICES_XINGHAO://型号
                if (ids != null && !ids.equals("")) {
                    bindMap.put("ModelID", ids);
                    if(AssetID != null){
                        final String[] str = AssetID.split("$");
                        if(str.length == 2){
                            bindMap.put("AssetID",str[0]);
                            beizhu = str[1];
                        }else {
                            bindMap.put("AssetID",str[0]);
                        }

                    }
//                    bindMap.put("AssetID",AssetID);
                    Log.e("lxs", "onActivityResult:AssetID "+AssetID );
                }

                if (result != null && !result.equals("")) {
                    devicesInfoXinghaoTv.setText(result);
                    edit.putString("XingHao", result);
                    edit.commit();
                }
                break;
            case ConstValues.RESULT_FOR_DEVICES_XULIEHAO:
                if (result != null && !result.equals("")) {
                    devicesInfoXuliehaoTv.setText(result);
                    bindMap.put("AssetXuLie", result);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onDestroy() {//清楚sp中保存的数据
        edit.clear();
        edit.commit();
        super.onDestroy();
    }
}
