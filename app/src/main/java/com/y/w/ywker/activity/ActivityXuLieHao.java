package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.zxing.CaptureActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by lxs on 16/4/12.
 */
public class ActivityXuLieHao extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.btn_bind_devices_back)
    ImageView btnBindDevicesBack;
    @Bind(R.id.edt_bind_devices)
    EditText edtBindDevices;
    @Bind(R.id.btn_bind_devices_scan_qr)
    TextView btnBindDevicesScanQr;
    @Bind(R.id.btn_bind_devices_sure)
    Button btnBindDevicesSure;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        ButterKnife.bind(this);
        type = getIntent().getStringExtra("type");
        String title = getIntent().getStringExtra("title");
        layoutCommToolbarTitle.setText(title);
    }

    @OnClick({R.id.layout_comm_toolbar_title, R.id.btn_bind_devices_back,
            R.id.btn_bind_devices_scan_qr, R.id.btn_bind_devices_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind_devices_back:
                finish();
                break;
            case R.id.btn_bind_devices_scan_qr:
                if (Build.VERSION.SDK_INT >= 23) {
                    requestCodeQrcodePermissions();
                } else {
                    Utils.start_ActivityResult(this, CaptureActivity.class,
                            ConstValues.QR_CODE_REQUEST_XULIEHAO);
                }
                break;
            case R.id.btn_bind_devices_sure:
               String result =  edtBindDevices.getText().toString().trim();
                if(TextUtils.isEmpty(result)){
                    Utils.showToast(this, "序列号格式有错或者结果为空");
                    return;
                }
//                Utils.start_Activity(this,DevicesInfoActivity.class,new YBasicNameValuePair[]{
//                        new YBasicNameValuePair("bind_type","create"),
//                        new YBasicNameValuePair("xuliehao",result)
//                });
                if(type.equals("1")){
                    Intent i = new Intent(this,DevicesInfoActivity.class);
                    i.putExtra("result",result);
                    setResult(ConstValues.RESULT_FOR_DEVICES_XULIEHAO,i);
                    finish();
                }else {
                    Intent i = new Intent(this,DevicesInfoActivity.class);
                    i.putExtra("result",result);
                    setResult(ConstValues.RESULT_FOR_DEVICES_QBCODE,i);
                    finish();
                }

                break;
        }
    }
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQrcodePermissions() {
        LOG.e(this, "开始请求权限...");
        String[] perms = {android.Manifest.permission.CAMERA, android.Manifest.permission.FLASHLIGHT};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和闪光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            LOG.e(this, "已经含有权限.");
            Utils.start_ActivityResult(this, CaptureActivity.class, ConstValues.QR_CODE_REQUEST_XULIEHAO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstValues.QR_CODE_REQUEST_XULIEHAO && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (TextUtils.isEmpty(result)) {
                Utils.showToast(this, "格式有错或者结果为空");
            } else {

                String scan_result = data.getStringExtra("result");
                Log.e("lxs", "onActivityResult: "+scan_result );
                edtBindDevices.setText(scan_result);
            }
        }
    }
}
