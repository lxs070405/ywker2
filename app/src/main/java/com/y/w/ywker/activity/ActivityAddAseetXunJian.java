package com.y.w.ywker.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.DevicesEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.ActivityManager;
import com.y.w.ywker.utils.Base64Utils;
import com.y.w.ywker.utils.ImgeUtils;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.PictureUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MaterialDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 增加设备巡检
 */
public class ActivityAddAseetXunJian extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.btn_back_devices_info)
    ImageView btnBackDevicesInfo;
    @Bind(R.id.tv_post)
    TextView tvPost;
    @Bind(R.id.tv_AseetName)
    TextView tvAseetName;
    @Bind(R.id.tv_PinPai)
    TextView tvPinPai;
    @Bind(R.id.tv_XingHao)
    TextView tvXingHao;
    @Bind(R.id.tv_XuLieHao)
    TextView tvXuLieHao;
    @Bind(R.id.rb_RunGood)
    RadioButton rbRunGood;
    @Bind(R.id.rb_RunBad)
    RadioButton rbRunBad;
    @Bind(R.id.rb_FaceGood)
    RadioButton rbFaceGood;
    @Bind(R.id.rb_FaceBad)
    RadioButton rbFaceBad;
    @Bind(R.id.tv_XunJianDsc)
    EditText tvXunJianDsc;
    @Bind(R.id.btn_add)
    Button btnAdd;
    @Bind(R.id.ll_xuliehao)
    LinearLayout llXuliehao;
    @Bind(R.id.image)
    ImageView iv0;
    @Bind(R.id.image1)
    ImageView iv1;
    @Bind(R.id.image2)
    ImageView iv2;
    @Bind(R.id.image4)
    ImageView addImage;
    @Bind(R.id.tv_XunJianCode)
    EditText tvXunJianCode;
    private DevicesEntry entry;
    private HashMap<String, String> map = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCameraTempFile(savedInstanceState);
        setContentView(R.layout.activity_addaseetxunjian);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        showLoading();
        initdata();
        setlistener();
    }

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
                typeNum = 0;
                addImage.setVisibility(View.VISIBLE);
                iv0.setVisibility(View.GONE);
                isIv = true;
                return true;

            }
        });
        iv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                datapicmap.remove("2");
                typeNum = 1;
                addImage.setVisibility(View.VISIBLE);
                iv1.setVisibility(View.GONE);
                isIv1 = true;
                return true;
            }
        });
        iv2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                datapicmap.remove("3");
                typeNum = 2;
                addImage.setVisibility(View.VISIBLE);
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


    private int type = 1;

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

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //调用照相机返回图片临时文件
    private File tempFile;

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

    private void initdata() {
        String devicesInfo = getIntent().getStringExtra("devices_info");
        if (devicesInfo != null && !devicesInfo.equals("")) {
            Gson gson = new Gson();
            entry = gson.fromJson(devicesInfo, DevicesEntry.class);
        }
        tvAseetName.setText(entry.getAssetName());
        tvPinPai.setText(entry.getBrandName());
        tvXingHao.setText(entry.getModelName());
        String str = entry.getAssetXuLie();
        if (str.equals("") || TextUtils.isEmpty(str)) {
            llXuliehao.setVisibility(View.GONE);
        } else {
            tvXuLieHao.setText(str);
        }

        String qbCode = getIntent().getStringExtra("Code");
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        map.put("mainId", offlineDataManager.getMainID());
        map.put("ExecuteUserID", offlineDataManager.getUserID());
        map.put("qbCode", qbCode);
        map.put("Pic", "");
        map.put("Base64", "");
        tvPost.setVisibility(View.GONE);
//        tvXunJianDsc.setHint("请填写使用中设备问题,检修分析,检修建议");
//        tvXunJianDsc.setHintTextColor(getResources().getColor(R.color.commo_text_color));
//        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        hideKeyboard();
        dismissLoading();
    }

    /**
     * 设备状态 1正常2故障
     */
    private int AssetStatus;
    /**
     * 外观描述 1，正常；2，破旧
     */
    private int OutSideStatus;
    /**
     * 巡检描述
     */
    private String AssetDetail;

    @OnClick({R.id.btn_back_devices_info, R.id.tv_post, R.id.tv_XunJianDsc, R.id.btn_add,
            R.id.rb_RunGood, R.id.rb_RunBad, R.id.rb_FaceGood, R.id.rb_FaceBad
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_devices_info:
                finish();
                break;
            case R.id.tv_XunJianDsc:
                break;
            case R.id.btn_add:
                proDialog = new ProgressDialog(this);
                proDialog.setTitle("提示");
                proDialog.setMessage("正在提交");
                proDialog.show();
                postData();
                break;
            case R.id.rb_RunGood:
                AssetStatus = 1;
                break;
            case R.id.rb_RunBad:
                AssetStatus = 2;
                break;
            case R.id.rb_FaceGood:
                OutSideStatus = 1;
                break;
            case R.id.rb_FaceBad:
                OutSideStatus = 2;
                break;

        }
    }


    /**
     * 提交增加巡检设备信息
     */
    private void postData() {
        AssetDetail = tvXunJianDsc.getText().toString();
        map.put("AssetStatus", AssetStatus + "");
        map.put("OutSideStatus", OutSideStatus + "");
        map.put("AssetDetail", AssetDetail);
        map.put("PatrolRecordID", ConstValues.PatrolRecordID);
        String code = tvXunJianCode.getText().toString();
        if(TextUtils.isEmpty(code)){
            Toast.makeText(this,"请填写巡检编号",Toast.LENGTH_SHORT).show();
            return;
        }
        map.put("PagerOrderCode",code);
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
        map.put("Pic", sbbuffer.toString());
        map.put("Base64", sbbase.toString());
        Log.e("lxs", "上传图片的名称:" + sbbuffer.toString());
        Log.e("lxs", "上传图片的内容:" + sbbase.toString());

        YHttpManagerUtils managerUtils = new YHttpManagerUtils(this, ConstValues.POST_ADD_XUNJIAN, mHandler, getClass().getName());
        managerUtils.startPostRequest(map);
        sbbuffer = new StringBuffer("");
        sbbase = new StringBuffer("");

    }


    private ProgressDialog proDialog;
    private Handler mHandler = new MyHandler(this, 1);
    private MaterialDialog dialogTip;

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
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(getBaseContext(), "巡检失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if (i == 1) {
                            dialogTip = new MaterialDialog(ActivityAddAseetXunJian.this)
                                    .setTitle("提示")
                                    .setMessage("该设备已巡检,是否继续巡检")
                                    .setPositiveButton("是", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {//继续扫描
                                            finish();
                                            Utils.start_Activity(ActivityAddAseetXunJian.this, BindDevicesActivity.class, new YBasicNameValuePair[]{
                                                    new YBasicNameValuePair("fromSource", "3")});
                                        }
                                    }).setNegativeButton("否", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityManager.getInstance().finshActivities(ActivityXunJianLook.class);
                                            Utils.start_Activity(ActivityAddAseetXunJian.this, ActivityXunJianLook.class, new YBasicNameValuePair[]{
                                                    new YBasicNameValuePair("position", ConstValues.planId)});
                                            finish();
                                        }
                                    });
                            dialogTip.show();
                        }
                        break;
                }
            }
        }
    }

    private static final int TAKE_PICTURE = 0x000001;

    /**
     * 调用摄像头 拍照
     */
    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    //请求相册
    private static final int REQUEST_PICK = 101;
    private StringBuffer sbbuffer = new StringBuffer("");
    private StringBuffer sbbase = new StringBuffer("");
    private HashMap<String, HashMap<String, Bitmap>> datapicmap = new HashMap<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
            case REQUEST_PICK:  //调用系统相册返回

                if (resultCode == RESULT_OK) {
                    typeNum++;
                    Uri uri = intent.getData();
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
        }
        super.onActivityResult(requestCode, resultCode, intent);
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

}
