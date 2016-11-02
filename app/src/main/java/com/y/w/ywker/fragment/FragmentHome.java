package com.y.w.ywker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.y.w.ywker.activity.ActivityOrderList;
import com.y.w.ywker.activity.HomeActivity;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.LOG;
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

/**
 * Created by lxs on 16/4/12.
 */
public class FragmentHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener,OnCommAdapterItemClickListener {

    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;

    @Bind(R.id.layout_swipe_refresh_empty_view)
    View emptyView;

    private Map<String,String> maps;

    private YHttpManagerUtils httpManagerUtils;

    //构造默认数据
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_comm_swiprefresh_recylerview, container, false);
        ButterKnife.bind(this, rootView);

        emptyView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);

        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        return rootView;
    }


    private void loadData(){

        ((HomeActivity)getActivity()).showLoading();

        String mainId = OfflineDataManager.getInstance(getContext()).getMainID();
        String userId = OfflineDataManager.getInstance(getContext()).getUserID();

        httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.HOME_URL, mainId, userId), mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    private Handler mHandler = new MyHandler(this);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(int position) {

    }

    public void onClickOrderType(String type,String title){
        LOG.e(getContext(),"type = " + type);
        Utils.start_Activity(getActivity(), ActivityOrderList.class,new YBasicNameValuePair[]{
                new YBasicNameValuePair("title",title),
                new YBasicNameValuePair("ordertype",type),
        });
    }

    @Override
    public void onRefresh() {
        if (httpManagerUtils != null){
            httpManagerUtils.startRequest();
        }
    }

    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;

        public MyHandler(Fragment fragment) {
            mFragmentReference = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            ((HomeActivity)getActivity()).dismissLoading();

            Fragment fragment = mFragmentReference.get();
            if (layoutCommSwipeRefresh != null){
                layoutCommSwipeRefresh.setRefreshing(false);
            }

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        Toast.makeText(getContext(), "首页没有内容", Toast.LENGTH_SHORT).show();
                        emptyView.setVisibility(View.VISIBLE);
                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(getContext(), "首页没有内容", Toast.LENGTH_SHORT).show();
                        emptyView.setVisibility(View.VISIBLE);
                        //使用默认值
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        if( emptyView != null){
                            emptyView.setVisibility(View.GONE);
                        }

                        handleData((String)msg.obj);
                        break;
                    default:
                        break;
                }
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
    private class HomeItemAdapter extends RecyclerView.Adapter<HomeItemVH>{

        private int []resid = new int[]{
                R.drawable.index_ordera,
                R.drawable.index_unallocateda,
                R.drawable.index_processa,
                R.drawable.index_completeda,
                R.drawable.index_alla
        };

        private HashMap<String,String> titles = new HashMap<String,String>();
        private HashMap<String,Integer> icons = new HashMap<String,Integer>();

        private List<String> keys;

        public HomeItemAdapter(){
            if (maps != null && !maps.isEmpty()){

                titles.put("NoAccepted","未受理工单");
                titles.put("MyNoFinish","我的未完成工单");
                titles.put("MyFinish","我的已完成工单");
                titles.put("TeamNoFinish","组内未完成工单");
                titles.put("AllNoFinish","所有未完成工单");
                titles.put("All","全部工单");
                titles.put("Urgent","紧急工单数量");

                icons.put("MyNoFinish",resid[0]);
                icons.put("NoAccepted",resid[1]);
                icons.put("TeamNoFinish",resid[2]);
                icons.put("MyFinish",resid[3]);
                icons.put("AllNoFinish",resid[2]);
                icons.put("All",resid[4]);
                icons.put("Urgent",resid[0]);

                keys = new ArrayList<String>();

                for(String key : maps.keySet()){
//                    if(key.equals("TeamNoFinish")&&key.equals("Urgent")){
//                        continue;
//                    }
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

    private void handleData(String json){
        Gson gson = new Gson();
        Log.e(getClass().getSimpleName(),json);
        if (json.startsWith("[") && json.endsWith("]")){
            json = json.replaceFirst("\\[","");
            json = json.replace("]","");
        }
        maps = gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
        if (maps != null && maps.size() > 0) {
            HomeItemAdapter adapter = new HomeItemAdapter();
            if (layoutCommSwipeRefreshRecyclerview != null){
                layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
            }
        }
    }
}