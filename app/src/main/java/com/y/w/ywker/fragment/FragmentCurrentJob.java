package com.y.w.ywker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.activity.ActivityXunJian;
import com.y.w.ywker.activity.ActivityXunJianLook;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.AdapterXunJianList;
import com.y.w.ywker.entry.XunJianListEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lxs on 2016/7/19.
 * 当前巡检任务列表
 */
public class FragmentCurrentJob extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterXunJianList.OnItemClickLitener {

    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;
    private YHttpManagerUtils httpManagerUtils;
    private String mainId;
    private String userId;
    private  AdapterXunJianList adapter;
    FrameLayout emptyView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currentjoblist, container, false);
        ButterKnife.bind(this, view);
        emptyView = (FrameLayout) view.findViewById(R.id.empty_view);
        emptyView.setVisibility(View.VISIBLE);
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(getContext());
        mainId = offlineDataManager.getMainID();
        userId = offlineDataManager.getUserID();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);
        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小
        adapter = new AdapterXunJianList(getContext(),2);
        layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
        httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.GET_XUNJIAN_LIST, mainId,userId,"2","0"), mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
        adapter.setOnItemClickLitener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if(adapter != null)
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if(ConstValues.isStartRefsh){
            maxId = "0";
        }else {
            if(list != null){
                maxId = list.get(0).getID();
            }
        }
        loadData();
        ConstValues.isStartRefsh = false;
    }

    private  String maxId = "0";
    public void loadData() {
        ((ActivityXunJian) getActivity()).showLoading();
        httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.GET_XUNJIAN_LIST, mainId,userId,"2",maxId), new MyHandler(this,2), this.getClass().getName());
        httpManagerUtils.startRequest();
    }



    private MyHandler mHandler = new MyHandler(this,1);

    @Override
    public void onItemClick(View view, int position) {
        Utils.start_Activity(getActivity(), ActivityXunJianLook.class,new YBasicNameValuePair[]{ new YBasicNameValuePair("position",list.get(position).getID()+"")});
    }

    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;
        private int type;
        public MyHandler(Fragment fragment,int type) {
            mFragmentReference = new WeakReference<Fragment>(fragment);
            this.type = type;
        }

        @Override
        public void handleMessage(Message msg) {
            ((ActivityXunJian) getActivity()).dismissLoading();
            Fragment fragment = mFragmentReference.get();
            if (fragment != null) {
                if (layoutCommSwipeRefresh != null) {
                    layoutCommSwipeRefresh.setRefreshing(false);
                }
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if(type == 2){
                            Toast.makeText(getContext(), "获取当前巡检信息失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getContext(), "获取当前巡检列表信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
                        if(type == 2){
                            Toast.makeText(getContext(), "已是最新信息", Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        Toast.makeText(getContext(), "已是最新信息", Toast.LENGTH_SHORT).show();
                        //使用默认值
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        if(type == 2){
                            Toast.makeText(getContext(), "获取当前巡检信息失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getContext(), "获取当前巡检列表信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_SUCESS:
                        handleData((String) msg.obj);
                        break;
                }
            }
        }
    }
    private List<XunJianListEntry> list;
    private void handleData(String json) {
        emptyView.setVisibility(View.GONE);
        Log.e("lxs", "巡检开始列表详情:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<XunJianListEntry>>() {
        }.getType();
        list =  gson.fromJson(json, listType);
        if(list != null && list.size() > 0){
            adapter.addData(list);
            adapter.notifyDataSetChanged();
        }
        if(adapter != null)
        adapter.notifyDataSetChanged();
    }
}
