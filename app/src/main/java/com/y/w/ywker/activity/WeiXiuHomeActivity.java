package com.y.w.ywker.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 维修首页
 */
public class WeiXiuHomeActivity extends SuperActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;
    @Bind(R.id.tempty_view)
    View emptyView;
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixiuhome);
        ButterKnife.bind(this);
        ctx = this;

        initView();

    }
    private YHttpManagerUtils httpManagerUtils;
    private void initData() {
        String mainId = OfflineDataManager.getInstance(this).getMainID();
        String userId = OfflineDataManager.getInstance(this).getUserID();
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.WEIXIU_HOME_URL, mainId, userId), mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }
    private Handler mHandler = new MyHandler(this);
    class MyHandler extends Handler {

        WeakReference<Activity> mFragmentReference;

        public MyHandler(Activity fragment) {
            mFragmentReference = new WeakReference<Activity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

                dismissLoading();
            if (refresh != null){
                refresh.setRefreshing(false);
            }

                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        Toast.makeText(ctx, "当前没有内容", Toast.LENGTH_SHORT).show();
                        emptyView.setVisibility(View.VISIBLE);
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(ctx, "当前没有内容", Toast.LENGTH_SHORT).show();
                        emptyView.setVisibility(View.VISIBLE);
                        //使用默认值
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(ctx, "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        emptyView.setVisibility(View.GONE);
                        handleData((String)msg.obj);
                        break;
            }
        }
    }
    HomeItemAdapter adapter;
    private Map<String,String> maps;

    @Override
    protected void onResume() {
        super.onResume();
        showLoading();
        initData();
    }

    private void handleData(String json){
        Gson gson = new Gson();
        Log.e(getClass().getSimpleName(),json);
        if (json.startsWith("[") && json.endsWith("]")){
            json = json.replaceFirst("\\[","");
            json = json.replace("]","");
        }
        maps = gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
        if (maps != null && maps.size() > 0) {
            adapter = new HomeItemAdapter();
            if (recyclerview != null){
                recyclerview.setAdapter(adapter);
            }
        }
    }

    private class HomeItemVH extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView title;
        TextView num;
        public HomeItemVH(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.homt_item_icon);
            title = (TextView) itemView.findViewById(R.id.homt_item_title);
            num = (TextView) itemView.findViewById(R.id.home_item_num);
        }

        public void bind(String title,String num,String icon,int iconid){
            this.title.setText(title);
            this.num.setText(num);
            this.icon.setImageResource(iconid);
        }

        public void bindClick(final String key, final String title){
            if (key != null && !key.equals("") &&
                    title != null && !title.equals(""))
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickOrderType(key,title);
                    }
                });
        }
    }

    private void onClickOrderType(String key, String title) {
        Log.e("lxs", "onClickOrderType: title"+title );
        Utils.start_Activity(this, ActivityWeiXiuOrderListPage.class,new YBasicNameValuePair[]{
                new YBasicNameValuePair("title",title),
                new YBasicNameValuePair("ordertype",key),
        });
    }

    private class HomeItemAdapter extends RecyclerView.Adapter<HomeItemVH>{


        private HashMap<String,String> titles = new HashMap<String,String>();
        private HashMap<String,Integer> icons = new HashMap<String,Integer>();
        private int []resid = new int[]{
                R.drawable.index_ordera,
                R.drawable.index_unallocateda,
                R.drawable.index_alla
        };
        private List<String> keys;

        public HomeItemAdapter(){
            if (maps != null && !maps.isEmpty()){

                titles.put("NotAssign","未分配");
                titles.put("MyNotComplete","我的未完成维修单");
                titles.put("MyAll","我的所有维修单");
                titles.put("All","所有维修单");
                icons.put("MyNotComplete",resid[0]);
                icons.put("NotAssign",resid[1]);
                icons.put("MyAll",resid[2]);
                icons.put("All",resid[2]);
                keys = new ArrayList<String>();
                for(String key : maps.keySet()){
                    keys.add(key);
                }
            }
        }
        @Override
        public HomeItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_item,parent,false);
            return new HomeItemVH(v);
        }

        @Override
        public void onBindViewHolder(HomeItemVH holder, int position) {
            String key = keys.get(position);
            holder.bind(titles.get(key),maps.get(key),"",position < icons.size() ? icons.get(key):resid[0]);
            holder.bindClick(key,titles.get(key));
        }


        @Override
        public int getItemCount() {
            if (maps == null || maps.isEmpty()){
                return 0;
            }
            return maps.size();
        }
    }


    private void initView() {
        layoutCommToolbarTitle.setText("维修");
        emptyView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        refresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        refresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        refresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小
    }

    @OnClick({R.id.btn_bind_devices, R.id.btn_new_weixiuworkorder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind_devices://扫描
                Utils.start_Activity(this, BindDevicesActivity.class,
                        new YBasicNameValuePair[]{
                                new YBasicNameValuePair("fromSource", "5")
                        });
                break;
            case R.id.btn_new_weixiuworkorder://创建
                Utils.start_Activity(this, NewWeiXiuOrderActivity.class, new YBasicNameValuePair[]{});
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (httpManagerUtils != null){
            httpManagerUtils.startRequest();
        }
    }

    @Override
    protected void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }
}
