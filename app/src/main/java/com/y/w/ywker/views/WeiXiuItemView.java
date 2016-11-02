package com.y.w.ywker.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y.w.ywker.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by lxs on 2016/9/24.
 */
public class WeiXiuItemView extends LinearLayout implements View.OnClickListener {
    TextView tvSheBeiXinXi;
    TextView tvWeiXiuJinDu;
    TextView tvAddLingJian;
    TextView tvdelete;
    TextView tvdevieceName;
    TextView tvzongjie;
    RelativeLayout rl_device;
    LinearLayout llAddlingjian;
    private EditText  etZongjie;
    private LinearLayout mLl_image;
    private ImageView mImage;
    private ImageView mImage1;
    private ImageView mImage2;
    public WeiXiuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeiXiuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }


    public WeiXiuItemView(Context context) {
        this(context, null);
    }



    private Context context;
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.weixiu_item_view, this);
        tvSheBeiXinXi = (TextView) view.findViewById(R.id.tv_SheBeiXinXi);
        tvWeiXiuJinDu = (TextView) view.findViewById(R.id.tv_WeiXiuJinDu);
        tvAddLingJian = (TextView) view.findViewById(R.id.tv_AddLingJian);
        tvdelete = (TextView) view.findViewById(R.id.tv_delete);
        tvdevieceName = (TextView) view.findViewById(R.id.tv_devieceName);
        tvzongjie = (TextView) view.findViewById(R.id.tv_zongjie);
        etZongjie = (EditText) view.findViewById(R.id.et_Zongjie);
        mLl_image = (LinearLayout) view.findViewById(R.id.ll_image);
        mImage = (ImageView) view.findViewById(R.id.image);
        mImage1 = (ImageView) view.findViewById(R.id.image1);
        mImage2 = (ImageView) view.findViewById(R.id.image2);
        llAddlingjian = (LinearLayout) view.findViewById(R.id.ll_addlingjian);
        rl_device = (RelativeLayout) view.findViewById(R.id.rl_device);
        tvdelete.setOnClickListener(this);
        tvzongjie.setOnClickListener(this);
        tvAddLingJian.setOnClickListener(this);
        tvWeiXiuJinDu.setOnClickListener(this);
        llAddlingjian.setOnClickListener(this);
    }
    private WeiXiuItemClickListener listener;
    public void setListener(WeiXiuItemClickListener listener){
        this.listener = listener;
    }
    public interface WeiXiuItemClickListener {
        void remvoeWeiXiuItem(WeiXiuItemView view);
    }
    public void setSheBeiXinXi(String str) {
        tvSheBeiXinXi.setText(str);
    }

    @OnClick({R.id.tv_SheBeiXinXi, R.id.tv_WeiXiuJinDu,R.id.tv_delete,
            R.id.tv_zongjie,
            R.id.tv_AddLingJian})
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.tv_zongjie://显示常用总结选框
                new WeiXiuJinDuDialog(context, R.style.MyDialogStyle, zongjieDatalist, "", "", new WeiXiuJinDuDialog.DialogClickListener() {
                    @Override
                    public void setWeiXiuJinDu(WeiXiuJinDuDialog myDialog, String string,int p) {
                        etZongjie.setText(string);
                        myDialog.dismiss();
                    }
                }).show();
                break;
            case R.id.tv_WeiXiuJinDu://显示维修进度选框
                new WeiXiuJinDuDialog(context, R.style.MyDialogStyle, list, "", "", new WeiXiuJinDuDialog.DialogClickListener() {
                    @Override
                    public void setWeiXiuJinDu(WeiXiuJinDuDialog myDialog, String string,int p) {
                        tvWeiXiuJinDu.setText(string);
                        myDialog.dismiss();
                    }
                }).show();
                break;
            case R.id.tv_AddLingJian://增加零件
                LingJianView  myview = new LingJianView(context);
                llAddlingjian.addView(myview);
                viewlist.add(myview);
//                myview.SetViewListener();
                break;
            case R.id.tv_delete://删除
                rl_device.setVisibility(View.GONE);
                listener.remvoeWeiXiuItem(this);
                break;
        }
    }
    List<LingJianView>  viewlist = new ArrayList<>();
    String LingJianData = "";

    /**
     * 获取总结数据
     * @return
     */
    public  String getZongJie(){
       return etZongjie.getText().toString().trim();
    }

    /**
     * 设置总结内容
     * @param zongjie
     */
    public  void  setEtZongjie(String zongjie){
        etZongjie.setText(zongjie);
    }
    /**
     * 设置设备名称头
     * @param devieceName
     */
    public void setTvdevieceName(String devieceName){
        tvdevieceName.setText(devieceName);
    }
    /**
     * 获取零件数据 返回值:为""说明没有数据
     * @return 数量+","+零件名称
     */
    public String getLingJianData() {
        StringBuffer sb = new StringBuffer();
        if(viewlist.size()>0){
            for (LingJianView view : viewlist) {
                if(view.getLingJianData().equals("")){
                    continue;
                }
                sb.append(view.getLingJianData()) ;
                sb.append("-");
            }
            LingJianData = sb.toString();
        }
        return LingJianData;
    }
    /**
     * 获取维修进度数据
     */
    public String GetWeiXiuJinDu(){
        return tvWeiXiuJinDu.getText().toString();
    }
    /**
     * 获取设备信息
     */
    public String GetSheBeiXinxi(){
        return tvSheBeiXinXi.getText().toString();
    }
    /**
     * 初始化维修进度状态列表
     */
    private List<String> list;

    /**
     * 维修进度数据
     * @param list
     */
    public void setWeiXiuJinDuData(List<String> list) {
        if (list != null) {
            this.list = list;
        }
        else {
            list = new ArrayList<>();
        }
    }


    /**
     * 初始化常用总结列表
     */
    private List<String> zongjieDatalist;
    /**
     * 常用总结数据
     * @param zongjieDatalist
     */
    public void setEtZongjieData(List<String> zongjieDatalist) {
        if (zongjieDatalist != null) {
            this.zongjieDatalist = zongjieDatalist;
        }
        else {
            zongjieDatalist = new ArrayList<>();
        }
    }


}
