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
import com.y.w.ywker.adapters.AdapterGrideviewImage;
import com.y.w.ywker.basepackege.ImageSelectorBaseUtile;
import com.y.w.ywker.entry.ChangYongWeiXiuEntry;
import com.y.w.ywker.entry.ChangYongZongJieEntry;
import com.y.w.ywker.entry.LingJianEntry;
import com.y.w.ywker.entry.PicEntry;
import com.y.w.ywker.utils.Base64Utils;
import com.y.w.ywker.utils.ImgeUtils;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.PictureUtils;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.LingJianView;
import com.y.w.ywker.views.WeiXiuJinDuDialog;
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

public class ActivityWeXiuZongJie extends SuperActivity {

    @Bind(R.id.btn_back_devices_info)
    ImageView btnBackDevicesInfo;
    @Bind(R.id.tv_post)
    TextView tvPost;
    @Bind(R.id.tv_SheBeiXinXi)
    TextView tvSheBeiXinXi;
    @Bind(R.id.tv_WeiXiuJinDu)
    TextView tvWeiXiuJinDu;
    @Bind(R.id.ll_addlingjian)
    LinearLayout llAddlingjian;
    @Bind(R.id.tv_AddLingJian)
    TextView tvAddLingJian;
    @Bind(R.id.et_Zongjie)
    EditText etZongjie;
    @Bind(R.id.tv_zongjie)
    TextView tvZongjie;
    @Bind(R.id.image)
    ImageView iv;
    @Bind(R.id.image1)
    ImageView iv1;
    @Bind(R.id.image2)
    ImageView iv2;
    @Bind(R.id.image4)
    ImageView addImage;
    @Bind(R.id.ll_image)
    LinearLayout llImage;
    @Bind(R.id.btn_img)
    Button btnImg;
    @Bind(R.id.iv_bigImg)
    ImageView ivBigImg;
    @Bind(R.id.rl_bigImg)
    RelativeLayout rlBigImg;
    @Bind(R.id.gridview)
//    MyGridView gridview;
    GridView gridview;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCameraTempFile(savedInstanceState);
        setContentView(R.layout.activity_wexiuzongjie);
        ButterKnife.bind(this);
        context = this;
        showLoading();
        initview();
        lodeData();
    }
    YHttpManagerUtils httpManagerUtils;
    private String  sourceId = "";//也是TaskAssetID
    private void lodeData() {
        sourceId = getIntent().getIntExtra("sourceId",0)+"";
        map.put("TaskAssetID",sourceId);
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        String mainId = offlineDataManager.getMainID();
        //获取常用维修进度
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.WEIXIU_ORDERDjindu_URL, mainId), new MyHandler(this,1), this.getClass().getName());
        httpManagerUtils.startRequest();
        //获取图片信息
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.WEIXIU_ORDERDPIC_URL, sourceId,mainId), new MyHandler(this,2), this.getClass().getName());
        httpManagerUtils.startRequest();
        //获取零件信息
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.WEIXIU_ORDERDLINGJIAN_URL,"0", sourceId), new MyHandler(this,3), this.getClass().getName());
        httpManagerUtils.startRequest();
        //获取常用总结
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.WEIXIU_ORDERDzongjie_URL, mainId), new MyHandler(this,4), this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mFragmentReference;
        int i;
        public MyHandler(AppCompatActivity fragment,int i) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.i = i;
        }

        @Override
        public void handleMessage(Message msg) {
            dismissLoading();
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        if(i == 2 ){//&& myRole == 1
                            setlistener();
                            gridview.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        String json = (String) msg.obj;
                        if(i == 1){
                            parsData(json,"jindu");
                        }
                        if(i == 2){
                            handlePicData(json);
                            gridview.setVisibility(View.GONE);
                        }
                        if(i == 3){
                            parsData(json,"lingjian");
                        }
                        if(i == 4){
                            parsData(json,"zongjie");
                        }
                        if(i == 5 ){
                            if(proDialog != null &&proDialog.isShowing())
                            proDialog.dismiss();
                            Toast.makeText(context,"提交完成",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        break;
                }
        }
    }

    List<ChangYongWeiXiuEntry> weiXiujinduEntries;
    private void parsData(String json,String desc) {
        Gson gson = new Gson();
        if(desc.equals("jindu")){
            Type listType = new TypeToken<List<ChangYongWeiXiuEntry>>() {}.getType();
             weiXiujinduEntries = gson.fromJson(json,listType);
            if(weiXiujinduEntries != null && weiXiujinduEntries.size() > 0){
                for (ChangYongWeiXiuEntry entry : weiXiujinduEntries) {
                    list.add(entry.getSpdName());
                }
            }
        }else if(desc.equals("lingjian")){
            Type listType = new TypeToken<List<LingJianEntry>>() {}.getType();
            List<LingJianEntry> entries = gson.fromJson(json,listType);
            if(entries != null && entries.size() > 0){//显示零件
                for (final LingJianEntry jianEntry : entries) {
                    LingJianView myview = new LingJianView(context);
                    llAddlingjian.addView(myview);
                    if(myRole == 1){
                        myview.setViewListener();
                    }
                    myview.setLingjianData(jianEntry.getPartNum(),jianEntry.getAssetPart());
                    viewlist.add(myview);
                }
            }
        }else if(desc.equals("zongjie")){
            Type listType = new TypeToken<List<ChangYongZongJieEntry>>() {}.getType();
            List<ChangYongZongJieEntry> lists = gson.fromJson(json,listType);
            if(lists != null && lists.size() > 0){
                for (ChangYongZongJieEntry zongJieEntry : lists) {
                    zongjieDatalist.add(zongJieEntry.getSheetReply());
                }
            }
        }
    }
    int  myRole;
    String aseetId;
    String TaskID;
    private void initview() {
        int  RepairSchedule = getIntent().getIntExtra("RepairSchedule",0);
        map.put("RepairSchedule",RepairSchedule+"");
        String SpdName = getIntent().getStringExtra("SpdName");
        if(SpdName != null && !SpdName.equals("")){
            tvWeiXiuJinDu.setText(SpdName);
        }
        String aseetName = getIntent().getStringExtra("aseetName");
       String zongjie =  getIntent().getStringExtra("zongjie");
        if(zongjie != null && !zongjie.equals("")){
            etZongjie.setText(zongjie);
        }
        tvSheBeiXinXi.setText(aseetName);
        aseetId = getIntent().getStringExtra("aseetId");
        TaskID = getIntent().getStringExtra("TaskID");
        myRole = getIntent().getIntExtra("myRole",0);
        if(myRole != 1){
            tvPost.setVisibility(View.GONE);
        }
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        userId = offlineDataManager.getUserID();
        MainId = offlineDataManager.getMainID();
        map.put("MainId",MainId);
        map.put("AddUserId",userId);
        map.put("TaskID",TaskID);
        grideviewAdapter = new AdapterGrideviewImage(this);
        gridview.setAdapter(grideviewAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (grideviewAdapter.getItem(position) == null
                        ||grideviewAdapter.isShowAddItem(position)){
                    ImgSelActivity.startActivity(ActivityWeXiuZongJie.this,
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
    AdapterGrideviewImage  grideviewAdapter;
    String MainId;
    String userId;
    private ProgressDialog proDialog;
    @OnClick({R.id.btn_back_devices_info,
            R.id.tv_post,R.id.rl_bigImg,
            R.id.tv_WeiXiuJinDu, R.id.tv_AddLingJian, R.id.tv_zongjie})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_devices_info:
                finish();
                break;
            case R.id.tv_post:
                proDialog = new ProgressDialog(this);
                proDialog.setTitle("提示");
                proDialog.setMessage("正在提交");
                proDialog.show();
                postData();
                break;
            case R.id.tv_WeiXiuJinDu:
                if(myRole == 1)
                ShowDailogWeiXiuJinDu();
                break;
            case R.id.tv_AddLingJian:
                if(myRole == 1){//受理人的情况下可查看 可编辑
                    LingJianView myview = new LingJianView(context);
                    llAddlingjian.addView(myview);
                    viewlist.add(myview);
                    myview.setViewListener();
                }
                break;
            case R.id.tv_zongjie:
                if(myRole == 1)
                ShowDailogZongJie();
                break;
            case R.id.rl_bigImg:
                rlBigImg.setVisibility(View.GONE);
                break;
        }
    }

    List<LingJianView> viewlist = new ArrayList<>();
    String LingJianData = "";
    /**
     * 维修进度状态列表数据
     */
    private List<String> zongjieDatalist = new ArrayList<>();
    private WeiXiuJinDuDialog zongjieDialog;
    /**
     * 显示常用总结对话框
     */
    private void ShowDailogZongJie() {
        if(zongjieDatalist.size() == 0){
            Toast.makeText(this,"常用总结数据未配置",Toast.LENGTH_SHORT).show();
            return;
        }
        zongjieDialog = new WeiXiuJinDuDialog(this, R.style.MyDialogStyle, zongjieDatalist, "", "", new WeiXiuJinDuDialog.DialogClickListener() {
            @Override
            public void setWeiXiuJinDu(WeiXiuJinDuDialog myDialog, String string,int position) {
                etZongjie.setText(string);
                myDialog.dismiss();
            }
        });
        zongjieDialog.show();
    }

    /**
     * 维修进度状态列表数据
     */
    private List<String> list = new ArrayList<>();

    private WeiXiuJinDuDialog weiXiuJinDuDialog;
    /**
     * 显示维修进度对话框
     */
    private void ShowDailogWeiXiuJinDu() {
        if(list.size() == 0){
            Toast.makeText(this,"维修进度数据未配置",Toast.LENGTH_SHORT).show();
            return;
        }
        weiXiuJinDuDialog = new WeiXiuJinDuDialog(this, R.style.MyDialogStyle, list, "", "", new WeiXiuJinDuDialog.DialogClickListener() {
            @Override
            public void setWeiXiuJinDu(WeiXiuJinDuDialog myDialog, String string,int position) {
                tvWeiXiuJinDu.setText(string);
                map.put("RepairSchedule",weiXiujinduEntries.get(position).getID());
                myDialog.dismiss();
            }
        });
        weiXiuJinDuDialog.show();
    }


    /**
     * 获取零件数据 返回值:为""说明没有数据
     * @return 数量+","+零件名称
     */
    public String getLingJianData() {
        StringBuffer sb = new StringBuffer();
        if (viewlist.size() > 0) {
            for (LingJianView view : viewlist) {

                if (view.getLingJianData().equals("")) {
                    continue;
                }
                    sb.append(view.getLingJianData());
                    sb.append("-");
            }
            LingJianData = sb.toString();
        }
        return LingJianData;
    }
    StringBuffer AssetPart = new StringBuffer();//零件名称
    StringBuffer  PartaNum = new StringBuffer();//零件数量
    private String TAG = "lxs";
    private HashMap<String, String> map = new HashMap<String, String>();
    private void postData() {
        String zongjie = etZongjie.getText().toString().trim();
        if(TextUtils.isEmpty(zongjie)){
            Toast.makeText(this,"请填写总结",Toast.LENGTH_SHORT).show();
            proDialog.dismiss();
            return;
        }
        map.put("RepairSummary",zongjie);

       String lingjian = getLingJianData();
        if(!lingjian.equals("")){
            String[] strings = lingjian.split("-");
            for (int i = 0; i < strings.length; i++) {
                String[] data = strings[i].split(",");
                String num = data[0];
                String name = data[1];
                PartaNum.append(num+"$");
                AssetPart.append(name+"$");
            }
        }
        map.put("AssetPart",AssetPart.toString());
        map.put("PartNum",PartaNum.toString());
        Log.e(TAG, "postData: 零件名称"+AssetPart.toString()+",个数:"+PartaNum.toString() );
        if (arrlist.size() > 0) {
            for (int i = 0; i < arrlist.size(); i++) {
                ImageView image = arrlist.get(i);
                image.setDrawingCacheEnabled(true);
                image.buildDrawingCache(true);
                Bitmap bitMap = image.getDrawingCache();
                iv2.setDrawingCacheEnabled(false);
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
        YHttpManagerUtils managerUtils = new YHttpManagerUtils(this, ConstValues.POST_weixiuZONGJIE_FINISH_URL, new MyHandler(this, 5), getClass().getName());
        managerUtils.startPostRequest(map);
        sbbuffer = new StringBuffer("");
        sbbase = new StringBuffer("");
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
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    private int type = 0;
    //调用照相机返回图片临时文件
    private File tempFile;
    boolean isIv = false;
    boolean isIv1 = false;
    private int typeNum = 0;
    /**
     * 设置图片监听
     */
    private void setlistener() {

//        iv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                datapicmap.remove("1");
//                typeNum = 0;
//                addImage.setVisibility(View.VISIBLE);
//                if (arrlist.contains(iv)) {
//                    arrlist.remove(iv);
//                    picName.remove(lists.get(0).getFileName());
//                }
//                iv.setVisibility(View.GONE);
//                isIv = true;
//                return true;
//            }
//        });
//        iv1.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                datapicmap.remove("2");
//                typeNum = 1;
//                addImage.setVisibility(View.VISIBLE);
//                if (arrlist.contains(iv1)) {
//                    arrlist.remove(iv1);
//                    picName.remove(lists.get(1).getFileName());
//                }
//                iv1.setVisibility(View.GONE);
//                isIv1 = true;
//                return true;
//            }
//        });
//        iv2.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                datapicmap.remove("3");
//                typeNum = 2;
//                addImage.setVisibility(View.VISIBLE);
//                if (arrlist.contains(iv2)) {
//                    arrlist.remove(iv2);
//                    picName.remove(lists.get(2).getFileName());
//                }
//                iv2.setVisibility(View.GONE);
//                return true;
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
//                int i =  typeNum%3;
//                if(i == 1){
//                    type = 2;
//                    if (arrlist.contains(iv1)) {
//                        arrlist.remove(iv1);
//                        picName.remove(lists.get(1).getFileName());
//                    }
//                }else if(i == 2){
//                    type = 3;
//                    if (arrlist.contains(iv2)) {
//                        arrlist.remove(iv2);
//                        picName.remove(lists.get(2).getFileName());
//                    }
//                }else {
//                    type = 1;
//                    if (arrlist.contains(iv)) {
//                        arrlist.remove(iv);
//                        picName.remove(lists.get(0).getFileName());
//                    }
//                }
//                uploadHeadImage();
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
        if(arrlist != null && arrlist.size() == 3){
            addImage.setVisibility(View.GONE);
        }else {
            addImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 解析图片数据
     */
    List<PicEntry> lists;
    List<String> picName = new ArrayList<>();
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
                Glide.with(this)
                        .load(lists.get(0).getPicHref())
                        .centerCrop()
                        .placeholder(R.drawable.photo_default)
                        .error(R.drawable.photo_default)
                        .crossFade()
                        .into(iv);
                arrlist.add(iv);
                typeNum++;
                iv.setVisibility(View.VISIBLE);
            }
            if (lists.size() == 2) {
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
                typeNum = 2;
            }
            if (lists.size() == 3) {
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
                typeNum = 3;
            }
            setlistener();
        }
    }
    private ArrayList<ImageView> arrlist = new ArrayList<>();
    /**
     * 全屏显示图片的大图
     * @param img_url
     */
    public void showImage(String img_url) {
        if (rlBigImg.getVisibility() == View.GONE) {
            rlBigImg.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(img_url).placeholder(R.drawable.photo_default).error(R.drawable.photo_default).into(ivBigImg);
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
//                        iv2.setVisibility(View.VISIBLE);
                    } else if (type == 3) {
                        iv2.setImageBitmap(bitMap);
                        iv2.setVisibility(View.VISIBLE);
                        bitMap = ImgeUtils.zoomBitmap(bitMap, 720, 720);
                        HashMap<String, Bitmap> datamap = new HashMap<>();
                        datamap.put(picname, bitMap);
                        datapicmap.put("3", datamap);
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
