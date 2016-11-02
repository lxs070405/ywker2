//package com.y.w.ywker.activity;
//
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.y.w.ywker.R;
//import com.y.w.ywker.views.WeiXiuItemView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//public class ShoppingCartItemActivity extends Activity {
//
//    Context context;
//    @Bind(R.id.ll_add)
//    LinearLayout llAdd;
//    @Bind(R.id.btn_text)
//    Button btnText;
//    @Bind(R.id.btn_post)
//    Button btnPost;
//    @Bind(R.id.scrollView)
//    ScrollView scrollView;
//    @Bind(R.id.tv_texst)
//    TextView tvTexst;
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.add_layout);
//        context = this;
//        ButterKnife.bind(this);
//        list.add("配件不全");
//        list.add("维修完成");
//        list.add("移交售后");
//        list.add("带回处理");
//        list.add("维修中");
//        zongjielist.add("已完成");
//        zongjielist.add("哈希吧");
//        zongjielist.add("刺激是的哈");
//        zongjielist.add("恩啊房东罚 ");
//        zongjielist.add("而且玩儿");
//        zongjielist.add(" 而缺乏的规范的施工方");
//        zongjielist.add("让我去退热贴个v");
//        zongjielist.add("切勿让他请提前退");
//    }
//
//
//    List<String> list = new ArrayList<>();
//    List<String> zongjielist = new ArrayList<>();
//
//    String str;
//
//    @OnClick({R.id.btn_text, R.id.btn_post})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_post://获取设备数据
//               getData();
//                break;
//            case R.id.btn_text://增加设备
//               addDevices();
//                break;
//        }
//    }
//
//    private void getData() {
//        StringBuffer sb = new StringBuffer();
//        if (viewlist.size() > 0) {
//            for (WeiXiuItemView itemview : viewlist) {
//                if(itemview.getLingJianData().equals("")){
//                    continue;
//                }
//                String  weixiujindu = itemview.GetWeiXiuJinDu();
//                sb.append(weixiujindu+"\n");
//                sb.append(itemview.GetSheBeiXinxi()+"\n");
//                str = itemview.getLingJianData();
//                String[] arr = str.split("-");
//                for (int i = 0; i < arr.length; i++) {
//                    String[] arritemdata =arr[i].split(",");
//                    sb.append("数量"+arritemdata[0]+"个配件名称"+arritemdata[1]);
//                    sb.append("\n");
//                }
//                sb.append("总结:"+itemview.getZongJie());
//                sb.append("\n\n");
//            }
//            tvTexst.setText(sb.toString());
//            if(sb.toString().contains("请选择")){
//                Toast.makeText(context,"提交的设备数据有问题,请检查数据",Toast.LENGTH_SHORT).show();
//            }
//        }else {
//            tvTexst.setText(" ");
//        }
//    }
//    private int num = 1;
//    private void addDevices() {
//        WeiXiuItemView myview = new WeiXiuItemView(this);
//        myview.setWeiXiuJinDuData(list);//设置设备进度数据
//        myview.setEtZongjieData(zongjielist);
//        llAdd.addView(myview);
//        viewlist.add(myview);
//        myview.setListener(new WeiXiuItemView.WeiXiuItemClickListener() {
//            @Override
//            public void remvoeWeiXiuItem(WeiXiuItemView view) {
//                viewlist.remove(view);
//            }
//        });
//        myview.setTvdevieceName("设备"+ num++);
//        myview.setSheBeiXinXi("联想电脑苏尼");//设置设备数据
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
//    }
//
//    Handler mHandler = new Handler();
//    List<WeiXiuItemView> viewlist = new ArrayList<>();
//
//}