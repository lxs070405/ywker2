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
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.activity.ActivityXunJian;
import com.y.w.ywker.adapters.AdapterXunJianList;
import com.y.w.ywker.entry.XunJianListEntry;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MaterialDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lxs on 2016/7/19.
 * 巡检任务列表
 */
public class FragmentJob extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterXunJianList.OnItemClickLitener {

    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;


    private String mainId;
    private String userId;
    private YHttpManagerUtils httpManagerUtils;
    private  AdapterXunJianList adapter;
    FrameLayout emptyView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.joblist, container, false);
        ButterKnife.bind(this, view);
        emptyView = (FrameLayout) view.findViewById(R.id.empty_view);
        emptyView.setVisibility(View.VISIBLE);
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(getContext());
        mainId = offlineDataManager.getMainID();
        userId = offlineDataManager.getUserID();
        httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.GET_XUNJIAN_LIST, mainId,userId,"0","0"),myHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);

        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        adapter = new AdapterXunJianList(getContext(),1);
        layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);

        adapter.setOnItemClickLitener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
//        Log.e("lxs", "onRefresh:巡检计划刷新 ");
        loadData();
    }

    private  String maxId = "0";
    private void loadData() {
        ((ActivityXunJian) getActivity()).showLoading();
        if(list != null &&list.size()>0){
            maxId = list.get(0).getID();
            httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.GET_XUNJIAN_LIST, mainId,userId,"0",maxId), new MyHandler(this,3), this.getClass().getName());
            httpManagerUtils.startRequest();
        }

//        Log.e("lxs", "onRefresh:巡检计划刷新 ");
    }
  private MyHandler myHandler  = new MyHandler(this,1);
    @Override
    public void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.cancle();
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils = null;
        }
        super.onDestroy();
    }
    MaterialDialog  dialogTip;
    @Override
    public void onItemClick(View view, final int position) {

        dialogTip = new MaterialDialog(getContext())
                .setTitle("提示")
                .setMessage("是否开始巡检")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,String> bindMap = new HashMap<String,String>();
                        bindMap.put("MainId",mainId);
                        bindMap.put("UserID",userId);
                        bindMap.put("PatrolID",list.get(position).getID());
                        YHttpManagerUtils managerUtils = new YHttpManagerUtils(getContext(), ConstValues.POST_START_XUNJIAN,new MyHandler(getParentFragment(),2), getClass().getName());
                        managerUtils.startPostRequest(bindMap);
                        ActivityXunJian activityXunJian = (ActivityXunJian) getActivity();
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                        ConstValues.isStartRefsh = true;
                        activityXunJian.setIndex();
                        Log.e("lxs", "onItemClick:post请求网址: "+ ConstValues.POST_START_XUNJIAN);
                        dialogTip.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogTip.dismiss();
                    }
                });
        dialogTip.show();

    }


    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;

        private int type ;
        public MyHandler(Fragment fragment,int type) {
            mFragmentReference = new WeakReference<Fragment>(fragment);
           this.type = type;
        }

        @Override
        public void handleMessage(Message msg) {

            Fragment fragment = mFragmentReference.get();
            ((ActivityXunJian) getActivity()).dismissLoading();
            if (fragment != null) {

                if (layoutCommSwipeRefresh != null) {
                    layoutCommSwipeRefresh.setRefreshing(false);
                }
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        Toast.makeText(getContext(), "获取巡检列表信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_ERROR:
//                        emptyView.setVisibility(View.VISIBLE);
//                            Toast.makeText(getContext(), "获取巡检列表信息失败", Toast.LENGTH_SHORT).show();

                        if(type == 2){
                            Toast.makeText(getContext(), "执行巡检失败", Toast.LENGTH_SHORT).show();
                        }
                        if(type == 3){
                            Toast.makeText(getContext(), "已是最新消息", Toast.LENGTH_SHORT).show();
                        }
                        //使用默认值
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        Toast.makeText(getContext(), "获取巡检列表信息失败", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_SUCESS:
                        if(type == 1){
                            handleData((String) msg.obj);
                        }
                       if(type == 2){
//                           if((int)msg.obj > 0){
                               Toast.makeText(getContext(), "执行成功", Toast.LENGTH_SHORT).show();
//                           }
                       }
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private List<XunJianListEntry> list;
    private void handleData(String json) {
        emptyView.setVisibility(View.GONE);
        Log.e("lxs", "巡检任务列表详情:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<XunJianListEntry>>() {
        }.getType();
      list =  gson.fromJson(json, listType);
        if(list != null && list.size() > 0){
            adapter.addData(list);
        }
    }
}
