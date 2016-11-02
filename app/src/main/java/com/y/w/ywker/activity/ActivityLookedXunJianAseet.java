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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.PicEntry;
import com.y.w.ywker.entry.XunJianAseetDetailEntry;
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
 * 查看巡检设备
 */
public class ActivityLookedXunJianAseet extends SuperActivity {

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
    TextView tvXunJianDsc;
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
    @Bind(R.id.iv_bigImg)
    ImageView ivBigImg;
    @Bind(R.id.rl_bigImg)
    RelativeLayout rlBigImg;

    private String AssetRecordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCameraTempFile(savedInstanceState);
        setContentView(R.layout.activity_addaseetxunjian);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        btnAdd.setVisibility(View.GONE);
        showLoading();
        AssetRecordId = getIntent().getStringExtra("AssetRecordId");
        Log.e("lxs", "onCreate: AssetID" + AssetRecordId);
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_DETAIL_URL, AssetRecordId), new MyHandler(this, 1), this.getClass().getName());
        httpManagerUtils.startRequest();
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        String mainId = offlineDataManager.getMainID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_XUNJIAN_PIC, AssetRecordId, mainId), new MyHandler(this, 3), this.getClass().getName());
        httpManagerUtils.startRequest();
        iv0.setVisibility(View.INVISIBLE);
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
                    if (arrlist.contains(iv0)) {
                        arrlist.remove(iv0);
                        picName.remove(lists.get(0).getFileName());
                    }
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
                    if (arrlist.contains(iv1)) {
                        arrlist.remove(iv1);
                        picName.remove(lists.get(1).getFileName());
                    }
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
                    if (arrlist.contains(iv2)) {
                        arrlist.remove(iv2);
                        picName.remove(lists.get(2).getFileName());
                    }
                    iv2.setVisibility(View.GONE);
                    return true;

            }
        });
        ivBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlBigImg.setVisibility(View.GONE);
            }
        });

        isShowAddImge();
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i =  typeNum%3;
                if(i == 1){
                    type = 2;
                    if (arrlist.contains(iv1)) {
                        arrlist.remove(iv1);
                        picName.remove(lists.get(1).getFileName());
                    }
                }else if(i == 2){
                    type = 3;
                    if (arrlist.contains(iv2)) {
                        arrlist.remove(iv2);
                        picName.remove(lists.get(2).getFileName());
                    }
                }else {
                    type = 1;
                    if (arrlist.contains(iv0)) {
                        arrlist.remove(iv0);
                        picName.remove(lists.get(0).getFileName());
                    }
                }

                uploadHeadImage();
            }
        });
        iv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrlist.contains(iv0)) {
                    showImage(lists.get(0).getPicHref());
                }
            }
        });
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrlist.contains(iv1)) {
                    showImage(lists.get(1).getPicHref());
                }
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrlist.contains(iv2)) {
                    showImage(lists.get(2).getPicHref());
                }
            }
        });
    }
    private void isShowAddImge() {
        if(arrlist != null && arrlist.size() == 3){
            addImage.setVisibility(View.GONE);
        }
    }
    private int typeNum = 0;
    /**
     * 全屏显示图片的大图
     *
     * @param img_url
     */
    public void showImage(String img_url) {
        if (rlBigImg.getVisibility() == View.GONE) {
            rlBigImg.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(img_url).placeholder(R.drawable.photo_default).error(R.drawable.photo_default).into(ivBigImg);
    }

    MaterialDialog     dialogTip;
    private int type = 0;

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

    /**
     * 创建调用系统照相机待存储的临时文件
     *
     * @param savedInstanceState
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

    //调用照相机返回图片临时文件
    private File tempFile;
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    private String AssetStatus = "1";
    private String OutSideStatus = "1";

    @OnClick({R.id.btn_back_devices_info, R.id.rb_RunGood,
            R.id.rb_RunBad, R.id.rb_FaceGood, R.id.rb_FaceBad
    })// , R.id.image, R.id.image1, R.id.image2
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_devices_info:
                finish();
                break;
            case R.id.rb_RunGood:
                AssetStatus = "1";
                break;
            case R.id.rb_RunBad:
                AssetStatus = "2";
                break;
            case R.id.rb_FaceGood:
                OutSideStatus = "1";
                break;
            case R.id.rb_FaceBad:
                OutSideStatus = "2";
                break;
//            case R.id.image:
//                break;
//            case R.id.image1:
//                break;
//            case R.id.image2:
//                break;
        }
    }

    private YHttpManagerUtils httpManagerUtils;

    @Override
    protected void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    private HashMap<String, String> map = new HashMap<String, String>();
    private ProgressDialog proDialog;

    @OnClick(R.id.tv_post)
    public void onClick() {//保存
        String code = tvXunJianCode.getText().toString();
        if(TextUtils.isEmpty(code)){
            Toast.makeText(this,"请填写巡检编号",Toast.LENGTH_SHORT).show();
            return;
        }

        map.put("PagerOrderCode",code);
        proDialog = new ProgressDialog(this);
        proDialog.setTitle("提示");
        proDialog.setMessage("正在保存中......");
        proDialog.show();
        map.put("AssetRecordId", AssetRecordId);
        map.put("AssetStatus", AssetStatus);
        map.put("OutSideStatus", OutSideStatus);
        map.put("AssetDetail", tvXunJianDsc.getText().toString());
        if(arrlist.size()>0){
            for (int i = 0; i < arrlist.size(); i++) {
                ImageView image = arrlist.get(i);
                image.setDrawingCacheEnabled(true);
                image.buildDrawingCache(true);
                Bitmap  bitMap = image.getDrawingCache();
                iv2.setDrawingCacheEnabled(false);
                sbbase.append(Base64Utils.imgToBase64(bitMap) + "$");
            }
            for (String s : picName) {
                sbbuffer.append(s + "$");
            }
        }
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
        sbbase = new StringBuffer();
        sbbuffer = new StringBuffer();

        YHttpManagerUtils managerUtils = new YHttpManagerUtils(this, ConstValues.POST_XUNJIANCHAKAN, new MyHandler(this, 2), getClass().getName());
        managerUtils.startPostRequest(map);
    }
    private ArrayList<ImageView> arrlist = new ArrayList<>();
    /**
     * 初始图片信息
     */
    private void invalidatapic() {
        if(lists != null &&lists.size() >0){
            for(int i = 0;i <lists.size(); i++){
                PicEntry bean = lists.get(i);
                HashMap<String, Bitmap> datamap = new HashMap<>();
                if(i == 0 && iv0.isFocusable()){
                    iv0.setDrawingCacheEnabled(true);
                    iv0.buildDrawingCache(true);
                    Bitmap  bitMap = iv0.getDrawingCache();
                    iv0.setDrawingCacheEnabled(false);
                    datamap.put(bean.getFileName(), bitMap);
                }
                if(i == 1 && iv1.isFocusable()){
                    iv1.setDrawingCacheEnabled(true);
                    iv1.buildDrawingCache(true);
                    Bitmap  bitMap = iv1.getDrawingCache();
                    iv1.setDrawingCacheEnabled(false);
                    datamap.put(bean.getFileName(), bitMap);
                }
                if(i == 2 && iv2.isFocusable()){
                    iv2.setDrawingCacheEnabled(true);
                    iv2.buildDrawingCache(true);
                    Bitmap  bitMap = iv2.getDrawingCache();
                    iv2.setDrawingCacheEnabled(false);
                    datamap.put(bean.getFileName(), bitMap);
                }
                datapicmap.put((i+1)+"",datamap);
            }
        }
    }


    class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mFragmentReference;
        private int type;

        public MyHandler(AppCompatActivity fragment, int type) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.type = type;
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
                        if (type == 3) {
                            return;
                        }
                        if (type == 2) {
                            Toast.makeText(getBaseContext(), "保存失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getBaseContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if (type == 1) {
                            handleData((String) msg.obj);
                            return;
                        }
                        if (type == 2) {
                            Toast.makeText(getBaseContext(), "保存成功", Toast.LENGTH_SHORT).show();
                            ActivityManager.getInstance().finshActivities(ActivityXunJianLook.class);
                            finish();
                            Utils.start_Activity(ActivityLookedXunJianAseet.this, ActivityXunJianLook.class, new YBasicNameValuePair[]{
                                    new YBasicNameValuePair("position", ConstValues.planId)
                            });
                            return;
                        }
                        if (type == 3) {
                            handlePicData((String) msg.obj);
                            return;
                        }
                        break;
                }
            }
        }
    }

    List<PicEntry> lists;
    /**
     * 解析图片数据
     */
    List<String>  picName= new ArrayList<>();
    private void handlePicData(String json) {
        Log.e("lxs", "巡检设备详情:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PicEntry>>() {
        }.getType();
        lists = gson.fromJson(json, listType);

        if (lists != null && lists.size() > 0) {
            for(int i = 0;i < lists.size();i++){
                picName.add(lists.get(i).getFileName());
            }
            if (lists.size() == 1) {
                typeNum = 0;
                 Glide.with(this)
                        .load(lists.get(0).getPicHref())
                         .centerCrop()
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv0) ;
                arrlist.add(iv0);
//                iv0.setImageBitmap(Base64Utils.base64ToBitmap(lists.get(0).getBase64()));
//                Bitmap  bitmap = ((BitmapDrawable) iv0.getDrawable()).getBitmap();
//                String str = lists.get(0).getPicHref();
//                String picname = str.substring(str.lastIndexOf("/") + 1, str.length());
//                HashMap<String, Bitmap> datamap = new HashMap<>();
//                datamap.put(picname, bitmap);
//                datapicmap.put("1", datamap);
                iv0.setVisibility(View.VISIBLE);
            }
            if (lists.size() == 2) {
                typeNum = 1;
                iv1.setVisibility(View.VISIBLE);
                iv0.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv0);
//                iv0.setImageBitmap(Base64Utils.base64ToBitmap(lists.get(0).getBase64()));
//                iv1.setImageBitmap(Base64Utils.base64ToBitmap(lists.get(1).getBase64()));
//                Bitmap  bitmap = ((BitmapDrawable) iv0.getDrawable()).getBitmap();
//                String str = lists.get(0).getPicHref();
//                String picname = str.substring(str.lastIndexOf("/") + 1, str.length());
//                HashMap<String, Bitmap> datamap = new HashMap<>();
//                datamap.put(picname, bitmap);
//                datapicmap.put("1", datamap);

                Glide.with(this)
                        .load(lists.get(1).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv1);
//                String str1 = lists.get(1).getPicHref();
//                String picname1 = str1.substring(str1.lastIndexOf("/") + 1, str1.length());
//                HashMap<String, Bitmap> datamap1 = new HashMap<>();
//                Bitmap bitmap1 = ((BitmapDrawable) iv1.getDrawable()).getBitmap();
//                datamap1.put(picname1, bitmap1);
//                datapicmap.put("2", datamap1);
                arrlist.add(iv0);
                arrlist.add(iv1);
            }
            if (lists.size() == 3) {
                typeNum = 2;
                iv0.setVisibility(View.VISIBLE);
                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.VISIBLE);
                arrlist.add(iv0);
                arrlist.add(iv1);
                arrlist.add(iv2);
//                iv0.setImageBitmap(Base64Utils.base64ToBitmap(lists.get(0).getBase64()));
//                iv1.setImageBitmap(Base64Utils.base64ToBitmap(lists.get(1).getBase64()));
//                iv2.setImageBitmap(Base64Utils.base64ToBitmap(lists.get(2).getBase64()));
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv0);
//                Bitmap  bitmap = ((BitmapDrawable) iv0.getDrawable()).getBitmap();
//                String str = lists.get(0).getPicHref();
//                String picname = str.substring(str.lastIndexOf("/") + 1, str.length());
//                HashMap<String, Bitmap> datamap = new HashMap<>();
//                datamap.put(picname, bitmap);
//                datapicmap.put("1", datamap);
//                iv1.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(lists.get(1).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv1);
//                String str1 = lists.get(1).getPicHref();
//                String picname1 = str1.substring(str1.lastIndexOf("/") + 1, str1.length());
//                HashMap<String, Bitmap> datamap1 = new HashMap<>();
//                Bitmap bitmap1 = ((BitmapDrawable) iv1.getDrawable()).getBitmap();
//                datamap1.put(picname1, bitmap1);
//                datapicmap.put("2", datamap1);
                Glide.with(this)
                        .load(lists.get(2).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv2);
//                String str2 = lists.get(2).getPicHref();
//                String picname2 = str2.substring(str2.lastIndexOf("/") + 1, str2.length());
//                HashMap<String, Bitmap> datamap2 = new HashMap<>();
//                Bitmap bitmap2 = ((BitmapDrawable) iv2.getDrawable()).getBitmap();
//                datamap2.put(picname2, bitmap2);
//                datapicmap.put("3", datamap2);
            }
            dismissLoading();
        }
    }

    private List<XunJianAseetDetailEntry> list;

    private void handleData(String json) {
        Log.e("lxs", "巡检设备详情:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<XunJianAseetDetailEntry>>() {
        }.getType();
        list = gson.fromJson(json, listType);
        if (list != null && list.size() > 0) {
            XunJianAseetDetailEntry bean = list.get(0);
            tvAseetName.setText(bean.getAssetName());
            tvPinPai.setText(bean.getBrandName());
            tvXingHao.setText(bean.getModelName());
            tvXuLieHao.setText(bean.getAssetXuLie());
            AssetStatus = list.get(0).getAssetStatus();
            OutSideStatus = list.get(0).getOutSideStatus();
           tvXunJianCode.setText(bean.getPagerOrderCode());
            if ("1".equals(AssetStatus)) {
                rbRunGood.setChecked(true);
                rbRunBad.setChecked(false);
            } else if ("2".equals(AssetStatus)) {
                rbRunGood.setChecked(false);
                rbRunBad.setChecked(true);
            }
            if ("1".equals(OutSideStatus)) {
                rbFaceGood.setChecked(true);
                rbFaceBad.setChecked(false);
            } else if ("2".equals(OutSideStatus)) {
                rbFaceGood.setChecked(false);
                rbFaceBad.setChecked(true);
            }
            tvXunJianDsc.setText(bean.getAssetDetail());
        }
        if (list != null && list.size() > 0) {
            AssetStatus = list.get(0).getAssetStatus();
            OutSideStatus = list.get(0).getOutSideStatus();
        }
    }

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
