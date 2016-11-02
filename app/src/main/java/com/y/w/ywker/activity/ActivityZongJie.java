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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.AdapterDelete;
import com.y.w.ywker.adapters.AdapterGrideviewImage;
import com.y.w.ywker.basepackege.ImageSelectorBaseUtile;
import com.y.w.ywker.entry.PicEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.entry.ZongJieEntry;
import com.y.w.ywker.utils.Base64Utils;
import com.y.w.ywker.utils.ImgeUtils;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.PictureUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.yuyh.library.imgsel.ImgSelActivity;

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

public class ActivityZongJie extends SuperActivity implements AdapterDelete.IonSlidingViewClickListener {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.btn_back_devices_info)
    ImageView btnBackDevicesInfo;
    @Bind(R.id.tv_post)
    TextView tvPost;
    @Bind(R.id.et_Zongjie)
    EditText etZongjie;
    @Bind(R.id.image)
    ImageView iv;
    @Bind(R.id.image1)
    ImageView iv1;
    @Bind(R.id.image2)
    ImageView iv2;
    @Bind(R.id.image4)
    ImageView addImage;
    @Bind(R.id.btn_addAseet)
    Button btnAddAseet;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.ll_image)
    LinearLayout llImage;
    @Bind(R.id.iv_bigImg)
    ImageView ivBigImg;
    @Bind(R.id.rl_bigImg)
    RelativeLayout rlBigImg;
    @Bind(R.id.gridview)
//    MyGridView gridview;
    GridView gridview;
    private String orderId;
    private AdapterDelete mAdapter;
    private int myRole;//角色
    private YHttpManagerUtils httpManagerUtils;
    private Context ctx;
    private String isFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCameraTempFile(savedInstanceState);
        setContentView(R.layout.activity_zong_jie);
        ButterKnife.bind(this);
        showLoading();
        ctx = this;
        initView();
    }

    private void initView() {
        isFinish = getIntent().getStringExtra("isFinish");
        orderId = getIntent().getStringExtra("orderId");
        myRole = Integer.parseInt(getIntent().getStringExtra("myRole"));
        Log.e("lxs", "onCreate:myRole---> " + myRole);
        if (myRole < 1) {
            tvPost.setVisibility(View.GONE);
            btnAddAseet.setVisibility(View.GONE);
            etZongjie.setFocusable(false);
            etZongjie.setFocusableInTouchMode(false);
        }
        llImage.setVisibility(View.VISIBLE);
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_ZONGJIE_DETEI, orderId), new MyHandler(this, 1), this.getClass().getName());
        httpManagerUtils.startRequest();
        LoadPic();
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AdapterDelete(this, data);
        mAdapter.setDeleteBtnClickListener(this);
        recyclerview.setAdapter(mAdapter);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        grideviewAdapter = new AdapterGrideviewImage(this);
        gridview.setAdapter(grideviewAdapter);

        setGridviews();
//
    }
    AdapterGrideviewImage  grideviewAdapter;
//    private int getDataSize(){
//        return  pathList == null ? 0 :  pathList.size();
//    }

    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        int type = 0;

        public MyHandler(AppCompatActivity fragment, int type) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.type = type;
        }

        @Override
        public void handleMessage(Message msg) {
            dismissLoading();
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        if (type == 1) {
                            Toast.makeText(getBaseContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if (msg.obj != null) {
                            if (type == 1) {
                                String data = (String) msg.obj;
                                parseData(data);
                                return;
                            }
                            if (type == 2) {
                                proDialog.dismiss();
                                Intent in = new Intent();
                                setResult(ConstValues.GONGZUOJONGJIE, in);
                                OfflineDataManager.getInstance(ctx).saveIsFinish("OK");
                                finish();
                                return;
                            }
                            if (type == 3) {
                                handlePicData((String) msg.obj);
                                return;
                            }
                        }
                        break;
                }
            }
        }
    }

    /**
     * 解析图片数据
     */
    List<PicEntry> lists;
    List<String> picName = new ArrayList<>();
    private int typeNum = 0;

    private void handlePicData(String json) {
        Log.e("lxs", "解析图片数据:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PicEntry>>() {
        }.getType();
        lists = gson.fromJson(json, listType);
        if (lists != null && lists.size() > 0) {
            for (int i = 0; i < lists.size(); i++) {
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
                        .into(iv);
                arrlist.add(iv);
                iv.setVisibility(View.VISIBLE);
            }
            if (lists.size() == 2) {
                typeNum = 1;
                iv.setVisibility(View.VISIBLE);
                iv1.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv);
                arrlist.add(iv);
                Glide.with(this)
                        .load(lists.get(1).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv1);
                arrlist.add(iv1);
            }
            if (lists.size() == 3) {
                typeNum = 2;
                iv.setVisibility(View.VISIBLE);
                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv);
                arrlist.add(iv);
                Glide.with(this)
                        .load(lists.get(1).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv1);
                arrlist.add(iv1);
                Glide.with(this)
                        .load(lists.get(2).getPicHref())
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv2);
                arrlist.add(iv2);
            }
        }
    }


    private List<ZongJieEntry.AssetListBean> entry;
    private List<String> data = new ArrayList<>();

    private void parseData(String json) {
        Log.e("lxs", "parseData: 工作总结get" + json);
        Gson gson = new Gson();
        Type type = new TypeToken<ZongJieEntry>() {
        }.getType();
        ZongJieEntry zongJieEntry = gson.fromJson(json, type);
        if (zongJieEntry != null) {
            isLoadPICdata(zongJieEntry);
            etZongjie.setText(zongJieEntry.getSheetSummay());
            entry = zongJieEntry.getAssetList();
            if (entry != null && entry.size() > 0) {
                for (ZongJieEntry.AssetListBean bean : entry) {
                    data.add(bean.getAssetName() + "," + bean.getQBCode() + "," + bean.getAssetID());
                }
                mAdapter.notifyDataSetChanged();
            }

            if (zongJieEntry.getIsExistPic() > 0) {
                setlistener();
                gridview.setVisibility(View.GONE);
            } else {
                setlistener();
                gridview.setVisibility(View.VISIBLE);
            }
        }
        dismissLoading();
    }

    private void isLoadPICdata(ZongJieEntry zongJieEntry) {
        if (myRole < 1) {//非受理人
            if (zongJieEntry.getIsExistPic() == 0) {
                llImage.setVisibility(View.GONE);
            } else {
                addImage.setVisibility(View.GONE);
            }
        } else {//受理人
            if (zongJieEntry.getIsExistPic() != 0)
                //根据大小展示图片 并给与修改 点击监听
                llImage.setVisibility(View.VISIBLE);
        }
    }

    private void LoadPic() {
        if (lists == null) {
            httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_GONGDANPIC_URL, orderId, OfflineDataManager.getInstance(ctx).getMainID()), new MyHandler(this, 3), this.getClass().getName());
            httpManagerUtils.startRequest();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lists = null;
        httpManagerUtils.cancle();
        httpManagerUtils = null;
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
    private HashMap<String, HashMap<String, ImageView>> dataimgemap = new HashMap<>();
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    private int type = 0;
    //调用照相机返回图片临时文件
    private File tempFile;
    boolean isIv = false;
    boolean isIv1 = false;

    /**
     * 设置图片监听
     */
    private void setlistener() {

//        iv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (myRole == 1) {
//                    datapicmap.remove("1");
//                    typeNum = 0;
//                    addImage.setVisibility(View.VISIBLE);
//                    if (arrlist.contains(iv)) {
//                        arrlist.remove(iv);
//                        picName.remove(lists.get(0).getFileName());
//                    }
//                    iv.setVisibility(View.GONE);
//                    isIv = true;
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//        });
//        iv1.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (myRole == 1) {
//                    datapicmap.remove("2");
//                    typeNum = 1;
//                    addImage.setVisibility(View.VISIBLE);
//                    if (arrlist.contains(iv1)) {
//                        arrlist.remove(iv1);
//                        picName.remove(lists.get(1).getFileName());
//                    }
//                    iv1.setVisibility(View.GONE);
//                    isIv1 = true;
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//        });
//        iv2.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (myRole == 1) {
//                    datapicmap.remove("3");
//                    typeNum = 2;
//                    addImage.setVisibility(View.VISIBLE);
//                    if (arrlist.contains(iv2)) {
//                        arrlist.remove(iv2);
//                        picName.remove(lists.get(2).getFileName());
//                    }
//                    iv2.setVisibility(View.GONE);
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//        });
        ivBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlBigImg.setVisibility(View.GONE);
            }
        });

//        isShowAddImge();
//        addImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int i = typeNum % 3;
//                if (i == 1) {
//                    type = 2;
//                    if (arrlist.contains(iv1)) {
//                        arrlist.remove(iv1);
//                        picName.remove(lists.get(1).getFileName());
//                    }
//                } else if (i == 2) {
//                    type = 3;
//                    if (arrlist.contains(iv2)) {
//                        arrlist.remove(iv2);
//                        picName.remove(lists.get(2).getFileName());
//                    }
//                } else {
//                    type = 1;
//                    if (arrlist.contains(iv)) {
//                        arrlist.remove(iv);
//                        picName.remove(lists.get(0).getFileName());
//                    }
//                }
//
////                uploadHeadImage();
////                ImageSelectorBaseUtile.initImageSelectorUtil();
//                ImgSelActivity.startActivity(ActivityZongJie.this, ImageSelectorBaseUtile.initImageSelectorUtil(), ConstValues.Image_REQUEST_CODE);
//            }
//        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrlist.contains(iv)) {
                    showImage(lists.get(0).getPicHref());
                    return;
                }
            }
        });
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrlist.contains(iv1)) {
                    showImage(lists.get(1).getPicHref());
                    return;
                }
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrlist.contains(iv2)) {
                    showImage(lists.get(2).getPicHref());
                    return;
                }
            }
        });
    }

    private void isShowAddImge() {
        if (arrlist != null && arrlist.size() == 3 || myRole != 1) {
            addImage.setVisibility(View.GONE);
        } else if (myRole == 1) {
            addImage.setVisibility(View.VISIBLE);
        }
    }


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

    private ArrayList<ImageView> arrlist = new ArrayList<>();
    private void setGridviews(){
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (grideviewAdapter.getItem(position) == null
                        ||grideviewAdapter.isShowAddItem(position)){
                    ImgSelActivity.startActivity(ActivityZongJie.this,
                            ImageSelectorBaseUtile.initImageSelectorUtil(),
                            ConstValues.Image_REQUEST_CODE);
                }

            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                grideviewAdapter.removeImage(position);
                grideviewAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
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

    private List<String> pathList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ConstValues.ZONGJIE://扫描二维码页面返回
                if (intent != null) {
                    String AseetId = intent.getStringExtra("AseetId");
                    String QBCode = intent.getStringExtra("QBCode");
                    String AseetName = intent.getStringExtra("AseetName");
                    data.add(AseetName + "," + QBCode + "," + AseetId);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case ConstValues.Image_REQUEST_CODE:
                if (intent != null && resultCode == RESULT_OK) {
                   pathList= intent.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
                   grideviewAdapter.addData(pathList);
                   grideviewAdapter.notifyDataSetChanged();
                }
                break;
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    typeNum++;
                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), Uri.fromFile(tempFile));
                    Log.e("lxs", "onActivityResult:图片 路径" + cropImagePath);
                    String picname = cropImagePath.substring(cropImagePath.lastIndexOf("/") + 1, cropImagePath.lastIndexOf(".")) + System.currentTimeMillis() + ".jpg";
                    Bitmap bitMap = PictureUtils.getSmallBitmap(cropImagePath);
                    if (type == 1) {
                        iv.setImageBitmap(bitMap);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("1", datamap);
                        iv.setVisibility(View.VISIBLE);
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
                    String picname = "";
                    if (cropImagePath.contains(".")) {
                        picname = cropImagePath.substring(cropImagePath.lastIndexOf("/") + 1, cropImagePath.lastIndexOf(".")) + System.currentTimeMillis() + ".jpg";
                    } else {
                        picname = cropImagePath.substring(cropImagePath.lastIndexOf("/") + 1, cropImagePath.length()) + System.currentTimeMillis() + ".jpg";
                    }
                    Log.e("lxs", "onActivityResult:图片 路径" + picname);
                    Bitmap bitMap = ImgeUtils.compressBitmap(null, null, this, uri, 2, false);
                    if (type == 1) {
                        iv.setImageBitmap(bitMap);
                        bitMap = ImgeUtils.zoomBitmap(bitMap, 720, 720);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("1", datamap);
                        iv.setVisibility(View.VISIBLE);
                    } else if (type == 2) {
                        iv1.setImageBitmap(bitMap);
                        bitMap = ImgeUtils.zoomBitmap(bitMap, 720, 720);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("2", datamap);
                        iv1.setVisibility(View.VISIBLE);
                    } else if (type == 3) {
                        iv2.setImageBitmap(bitMap);
                        bitMap = ImgeUtils.zoomBitmap(bitMap, 720, 720);
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

    private ProgressDialog proDialog;

    @OnClick({R.id.tv_post, R.id.btn_addAseet, R.id.btn_back_devices_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_devices_info://返回
                finish();
                break;
            case R.id.tv_post://完成按钮
                proDialog = new ProgressDialog(this);
                proDialog.setTitle("提示");
                proDialog.setMessage("正在提交");
                proDialog.show();
                PostData();
                break;
            case R.id.btn_addAseet://增加设备
                Utils.start_ActivityResult(this, BindDevicesActivity.class, ConstValues.ZONGJIE, new YBasicNameValuePair[]{
                        new YBasicNameValuePair("fromSource", "4")
                });
                break;
        }
    }

    private int btnIsClick = 1;
    private HashMap<String, String> map = new HashMap<String, String>();

    /**
     * 提交数据
     */
    private void PostData() {
        String zpm = etZongjie.getText().toString().trim();
        if (TextUtils.isEmpty(zpm)) {
            Toast.makeText(ctx, "必须填写总结", Toast.LENGTH_SHORT).show();
            proDialog.dismiss();
            return;
        }
        map.put("sheetId", orderId);
        map.put("Summary", zpm);
        map.put("opId", OfflineDataManager.getInstance(ctx).getUserID());
        StringBuffer sbAseetIds = new StringBuffer();
        for (String str : data) {
            sbAseetIds.append(str.split(",")[2] + ",");
        }
        map.put("AssetIds", sbAseetIds.toString());
        if (arrlist.size() > 0) {
            for (int i = 0; i < arrlist.size(); i++) {
                ImageView image = arrlist.get(i);
                image.setDrawingCacheEnabled(true);
                image.buildDrawingCache(true);
                Bitmap bitMap = image.getDrawingCache();
                image.setDrawingCacheEnabled(false);
                sbbase.append(Base64Utils.imgToBase64(bitMap) + "$");
            }
            for (String s : picName) {
                sbbuffer.append(s + "$");
            }
        }
        List<String> adapterdatas = grideviewAdapter.getAdapterData();
        for (int i = 0; i < adapterdatas.size(); i++) {
            String path = adapterdatas.get(i);
            String picname = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) + System.currentTimeMillis() + ".jpg";
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
//            opts.inSampleSize = 4;
//            Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
            Bitmap bitmap = PictureUtils.getSmallBitmap(path);
            sbbuffer.append(picname + "$");
            sbbase.append(Base64Utils.imgToBase64(bitmap) + "$");
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

        YHttpManagerUtils managerUtils = new YHttpManagerUtils(this, ConstValues.POST_ZONGJIE_FINISH_URL, new MyHandler(ActivityZongJie.this, 2), getClass().getName());
        managerUtils.startPostRequest(map);
        sbbuffer = new StringBuffer("");
        sbbase = new StringBuffer("");
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        if (myRole < 1) {
            Toast.makeText(ActivityZongJie.this, "无权修改", Toast.LENGTH_SHORT).show();
            mAdapter.closeMenu();
            return;
        }
        mAdapter.removeData(position);
    }


}
