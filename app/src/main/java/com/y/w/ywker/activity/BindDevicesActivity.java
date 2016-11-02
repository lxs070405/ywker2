package com.y.w.ywker.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.AseetCodeSearchEntry;
import com.y.w.ywker.entry.DevicesEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.zxing.CaptureActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by lxs on 16/4/12.
 * 绑定设备判断页面(二维码按钮进来后的页面)
 */

public class BindDevicesActivity extends SuperActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.edt_bind_devices)
    EditText edtBindDevices;
    @Bind(R.id.btn_bind_devices_sure)
    Button btnBindDevicesSure;
    @Bind(R.id.listview)
    ListView listview;
    private YHttpManagerUtils httpManagerUtils;


    /**
     * 回复工单,其他
     *
     */
    private String devicesInfo = "";
    /**
     * 来源  1 主页   2 回复工单 3 巡检
     */
    private String fromSouce = "";

    /**
     * 扫完二维码判断完毕后返回的主键ID
     */
    private String codeId = "";
    private String MainID = "";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bind_devices);
        ButterKnife.bind(this);
        Utils.initToolBar(this, layoutCommToolbar);
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        MainID = offlineDataManager.getMainID();
        edtBindDevices.addTextChangedListener(new EditChangedListener());
        fromSouce = getIntent().getStringExtra("fromSource");
        if (fromSouce.equals("1")){
            layoutCommToolbarTitle.setText("扫描设备");
        }else if (fromSouce.equals("2")){
            layoutCommToolbarTitle.setText("回复工单");
        }else if(fromSouce.equals("3")){
            layoutCommToolbarTitle.setText("增加巡检设备");
        }else if(fromSouce.equals("4")||fromSouce.equals("6")){
            layoutCommToolbarTitle.setText("工单增加设备");
        }else if(fromSouce.equals("5")){
            layoutCommToolbarTitle.setText("扫描维修设备");
        }

    }

    boolean isSearch;
    boolean stopWath ;
    class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //文本输入之前时的回调
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //将输入的内容实时显示
            if(s.length() == 0){
                stopWath = false;
                btnBindDevicesSure.setEnabled(false);
                btnBindDevicesSure.setText("确定");
                listview.setVisibility(View.GONE);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {//文本改变之后的监听事件回调

            if(!isSearch&&!stopWath&&s.length() > 3){
                isSearch = true;
            }
            if(isSearch&& s.length() > 3){
                    Search(s.toString());
            }
        }

    }
    private boolean stopSearch;
    private void isDevicesBinded(String deviceCode) {
        btnBindDevicesSure.setEnabled(false);
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.BIND_DEVICES_COMPARE_URL, deviceCode,MainID), new MyHandler(this), this.getClass().getName());
        httpManagerUtils.startRequest();
    }
    private void Search(String deviceCode) {
//        isSearch = false;
        if(isSearch&&!stopWath){
            btnBindDevicesSure.setEnabled(false);
            httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.SEARCH_DEVICES_COMPARE_URL, deviceCode,MainID), new  SearchHandler(this), this.getClass().getName());
            httpManagerUtils.startRequest();
            isSearch = false;
        }

    }
    class  SearchHandler extends Handler{
        WeakReference<AppCompatActivity> mFragmentReference;
        public SearchHandler(AppCompatActivity fragment){
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null){
                switch (msg.what){
                    case  ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        Utils.showToast(BindDevicesActivity.this,"未查询到该标签请重新输入!");
                        break;
                    case ConstValues.MSG_SUCESS:
                       String result = (String) msg.obj;
                        if(result.equals("error")){
                            Utils.showToast(BindDevicesActivity.this,"未查询到该标签请重新输入!");
                            return;
                        }
                        final ArrayList<String> arr = PaseData(result);
                        if(!stopSearch){
                            listview.setVisibility(View.VISIBLE);
                            stopSearch = false;
                        }
                        stopWath = true;
                        listview.setAdapter(new ArrayAdapter<String>(BindDevicesActivity.this,
                                android.R.layout.simple_list_item_1, arr));
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                edtBindDevices.setText(arr.get(position));
                                listview.setVisibility(View.GONE);
                                isDevicesBinded(arr.get(position));
                            }
                        });
                        break;
                }
            }
        }
    }

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
                        btnBindDevicesSure.setEnabled(true);
                        break;
                    case ConstValues.MSG_ERROR:
                        //使用默认值
//                        Toast.makeText(BindDevicesActivity.this, "设备未绑定", Toast.LENGTH_SHORT).show();
                        btnBindDevicesSure.setEnabled(true);
                        btnBindDevicesSure.setText("绑定设备");
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        btnBindDevicesSure.setEnabled(true);
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        btnBindDevicesSure.setEnabled(true);
                        break;
                    case ConstValues.MSG_SUCESS:
                        btnBindDevicesSure.setEnabled(true);
                        String result = (String) msg.obj;

                        Log.e(getClass().getSimpleName(),"扫描完毕判断返回值 : " + result);

                        if (result.equals("0")){
                            Toast.makeText(BindDevicesActivity.this, "该标签不存在此服务商中", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            if (Integer.parseInt(result) > 0){
                                codeId = result;
                                Toast.makeText(BindDevicesActivity.this, "设备未绑定", Toast.LENGTH_SHORT).show();
                                btnBindDevicesSure.setEnabled(true);
                                btnBindDevicesSure.setText("绑定设备");
                                return;
                            }
                        }catch (Exception e){

                        }

                        handData(result);
                        btnBindDevicesSure.setEnabled(true);
                        if(fromSouce.equals("4")){
                            btnBindDevicesSure.setText("增加设备信息(已绑定)");
                        }else {
                            btnBindDevicesSure.setText("查看设备信息(已绑定)");
                        }

                        break;
                    default:
                        break;
                }
            }
        }
    }


    private  ArrayList PaseData(String json){
        Gson gson = new Gson();
        ArrayList<String> arr =new ArrayList<>();
        Type type = new TypeToken<List<AseetCodeSearchEntry>>(){}.getType();
        List<AseetCodeSearchEntry> list = gson.fromJson(json,type);
        if (list != null && !list.isEmpty()){
            for(AseetCodeSearchEntry entry : list ){
               arr.add(entry.getQBCode()) ;
            }
        }
        return arr;
    }
    private String AseetId = "0";
    private String AseetName = "";

    private void handData(String json){
        Log.e("lxs", "handData: "+json);
        Gson gson = new Gson();
        Type type = new TypeToken<List<DevicesEntry>>(){}.getType();
        List<DevicesEntry> _list = gson.fromJson(json,type);
        if (_list != null && !_list.isEmpty()){
            devicesInfo = gson.toJson(_list.get(0));
            AseetId = _list.get(0).getID()+"";
            AseetName = _list.get(0).getAssetName();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btn_bind_devices_back, R.id.btn_bind_devices_scan_qr, R.id.btn_bind_devices_sure})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_devices_sure:
                /**
                 * 如果绑定,获取绑定信息的
                 */
                if (btnBindDevicesSure.getText().equals("绑定设备")){
                    if(fromSouce.equals("4")||fromSouce.equals("5")||fromSouce.equals("6")){
                        Utils.start_Activity(BindDevicesActivity.this,DevicesInfoActivity.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("bind_type","zongjietype"),
                                new YBasicNameValuePair("bind_xunjian","zongjie"),
                                new YBasicNameValuePair("QBCode",edtBindDevices.getText().toString()),
                                new YBasicNameValuePair("codeID",codeId) });
                        finish();
                    }else
                    if (fromSouce.equals("1")){//创建
                        Utils.start_Activity(BindDevicesActivity.this,DevicesInfoActivity.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("bind_type","create"),
                                new YBasicNameValuePair("codeID",codeId)});
                        finish();
                    }else if(fromSouce.equals("2")){//回复
                        Utils.start_ActivityResult(BindDevicesActivity.this, DevicesInfoActivity.class, ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER, new YBasicNameValuePair[]{
                                new YBasicNameValuePair("bind_type", "replay"),//replay
                                new YBasicNameValuePair("codeID",codeId)
                        });
//                        finish();
                    }else if(fromSouce.equals("3")){
                        Utils.start_Activity(BindDevicesActivity.this,DevicesInfoActivity.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("bind_type","xunjiantype"),
                                new YBasicNameValuePair("bind_xunjian","xunjian"),
                                new YBasicNameValuePair("codeID",codeId) });
                        finish();
                    }
                }else if(btnBindDevicesSure.getText().toString().contains("已绑定")){
                    if(fromSouce.equals("6")){//维修工单增加设备
                        Intent intent = new Intent(BindDevicesActivity.this,WeiXiuOrderDetailActivity.class);
                        intent.putExtra("AseetId",AseetId);
                        intent.putExtra("AseetName",AseetName);
                        intent.putExtra("QBCode",edtBindDevices.getText().toString());
                        Log.e("lxs", "onClick:传递---- "+AseetId );
                        setResult(ConstValues.ADD_SHEBEI,intent);
                        finish();
                    }else
                    if (fromSouce.equals("5")){//创建维修工单
                        Utils.start_Activity(BindDevicesActivity.this,DevicesInfoActivity.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("bind_type","createweixiu"),
                                new YBasicNameValuePair("devices_info",devicesInfo)
                        });
                        finish();
                    }else
                    if(fromSouce.equals("4")){//工作总结
                        Intent intent = new Intent(BindDevicesActivity.this,ActivityZongJie.class);
                        intent.putExtra("AseetId",AseetId);
                        intent.putExtra("AseetName",AseetName);
                        intent.putExtra("QBCode",edtBindDevices.getText().toString());
                        setResult(ConstValues.ZONGJIE,intent);
                        finish();
                    }else
                    if(fromSouce.equals("3") ){//巡检
                        Utils.start_Activity(BindDevicesActivity.this,ActivityAddAseetXunJian.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("devices_info",devicesInfo),
                                new YBasicNameValuePair("Code",edtBindDevices.getText().toString())
                        });
                        finish();
                    }else if (fromSouce.equals("1")){//创建工单
                        Utils.start_Activity(BindDevicesActivity.this,DevicesInfoActivity.class,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("bind_type","create"),
                                new YBasicNameValuePair("devices_info",devicesInfo)
                        });
                        finish();
                    }else if(fromSouce.equals("2")){//回复
                        Log.e("lxs", "onClick: devicesInfo--------->"+devicesInfo );
                        Utils.start_ActivityResult(BindDevicesActivity.this,DevicesInfoActivity.class,ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER,new YBasicNameValuePair[]{
                                new YBasicNameValuePair("bind_type","replay"),
                                new YBasicNameValuePair("devices_info",devicesInfo)
                        });
//                        finish();
                    }
                }else if(btnBindDevicesSure.getText().toString().contains("确定")){
                    if (edtBindDevices.getText() != null){
                        isDevicesBinded(edtBindDevices.getText().toString());
                    }else{
                        Toast.makeText(this,"请填写设备信息",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btn_bind_devices_scan_qr:
                if (Build.VERSION.SDK_INT >= 23) {
                    requestCodeQrcodePermissions();
                } else {
                    Utils.start_ActivityResult(BindDevicesActivity.this, CaptureActivity.class, ConstValues.QR_CODE_REQUEST);
                }
                break;
            case R.id.btn_bind_devices_back:
                finish();
                break;
        }
    }
    String Codedata = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstValues.QR_CODE_REQUEST && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (TextUtils.isEmpty(result)) {
                Utils.showToast(this, "二维码格式有错或者结果为空");
            } else {
                String scan_result = data.getStringExtra("result");
//                scan_result = scan_result.toLowerCase();
                /**
                 * 判断是否是易维客官方二维码
                 */
                if (scan_result.contains("Cid=")||scan_result.contains("cid=")){
                    String sCode = scan_result.substring(scan_result.lastIndexOf("=") + 1,scan_result.length());
                    edtBindDevices.setText(sCode);
                     listview.setVisibility(View.GONE);
                    /**
                     * 开始判断是否绑定
                     */
                    LOG.e(this,"设备码 = " + sCode);
                    stopSearch = true;
                    isDevicesBinded(sCode);

                }
                else{
                    edtBindDevices.setText(scan_result);
                }
            }
        }else if(requestCode == ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER){
            setResult(ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER,data);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LOG.e(this, "onPermissionsDenied");
    }

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQrcodePermissions() {
        LOG.e(this, "开始请求权限...");
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.FLASHLIGHT};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和闪光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            LOG.e(this, "已经含有权限.");
            Utils.start_ActivityResult(BindDevicesActivity.this, CaptureActivity.class, ConstValues.QR_CODE_REQUEST);
        }
    }
}
