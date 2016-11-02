package com.y.w.ywker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.activity.ActivityOrderReplayDetails;
import com.y.w.ywker.activity.WeiXiuOrderDetailActivity;
import com.y.w.ywker.adapters.DynamicAdapter;
import com.y.w.ywker.entry.DynamicEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lxs on 16/4/12.
 * 动态
 */
public class FragmentDynamic extends Fragment implements OnCommAdapterItemClickListener,SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;
    @Bind(R.id.layout_swipe_refresh_empty_view)
    View emptyView;

    private DynamicAdapter adapter;

    /**
     * 正在加载
     */
    private boolean isLoading = false;
    /**
     * 正在刷新
     */
    private boolean isRefresh = false;

    private int lastVisibleItem = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_comm_swiprefresh_recylerview, container, false);
        ButterKnife.bind(this, rootView);
        emptyView.setVisibility(View.GONE);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);
        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小
        adapter = new DynamicAdapter(getActivity());
        adapter.setListener(FragmentDynamic.this);
        layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
        layoutCommSwipeRefresh.setEnabled(true);
        layoutCommSwipeRefreshRecyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRefresh){
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && adapter != null && lastVisibleItem + 1 == adapter.getItemCount()
                        && !isLoading) {
                    //加载更多
                    isLoading = true;
                    if (!dynamicEntryList.isEmpty()) {
                        loadData(dynamicEntryList.get(dynamicEntryList.size() - 1).getID() + "");
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        //第一次加载数据
        loadData("0");
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(int position) {
        DynamicEntry entry = dynamicEntryList.get(position);
        httpManagerUtils = new YHttpManagerUtils(getActivity(), String.format(ConstValues.isRed_URL,entry.getID()), new MyHandler(this,2), this.getClass().getName());
        httpManagerUtils.startRequest();
        if(entry.getDataType() == 1){
            Utils.start_Activity(getActivity(), ActivityOrderReplayDetails.class, new YBasicNameValuePair[]{
                    new YBasicNameValuePair("order_id",entry.getSheetID() + ""),
                    new YBasicNameValuePair("order_title",entry.getTitle())
            });
        }else if(entry.getDataType() == 2){
            Utils.start_Activity(getActivity(),WeiXiuOrderDetailActivity.class,new YBasicNameValuePair[]{
                    new YBasicNameValuePair("taskId",entry.getSheetID() + ""),
            });
//            finish();
//            Utils.start_Activity(getActivity(), ActivityOrderReplayDetails.class, new YBasicNameValuePair[]{
//                    new YBasicNameValuePair("order_id",entry.getSheetID() + ""),
//                    new YBasicNameValuePair("order_title",entry.getTitle())
//            });
        }

    }

    private YHttpManagerUtils httpManagerUtils;
    private List<DynamicEntry> dynamicEntryList = new ArrayList<DynamicEntry>();

    /**
     * 第一次获取数据 id= 0
     * 上拉获取旧数据 id为列表中最后一条数据ID
     * @param id
     */
    public void loadData(String id) {
        if (isRefresh){
            Toast.makeText(getContext(),"正在刷新,请稍后重试加载更多",Toast.LENGTH_SHORT).show();
            return;
        }
        isLoading = true;
        isRefresh = false;
        String mainId = OfflineDataManager.getInstance(getActivity()).getMainID();
        String userId = OfflineDataManager.getInstance(getActivity()).getUserID();
        httpManagerUtils = new YHttpManagerUtils(getActivity(), String.format(ConstValues.DYMANIC_URL, mainId,userId,id), mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    /**
     * 下拉获取最新的数据,id为第一条数据ID
     */
    public void loadNewsData(String id){
        if (isLoading){
            Toast.makeText(getContext(),"正在加载更多,请稍后重试刷新",Toast.LENGTH_SHORT).show();
            return;
        }
        isLoading = false;
        isRefresh = true;
        String mainId = OfflineDataManager.getInstance(getActivity()).getMainID();
        String userId = OfflineDataManager.getInstance(getActivity()).getUserID();
        httpManagerUtils = new YHttpManagerUtils(getActivity(), String.format(ConstValues.DYNAMIC_LOAD_NEWS_URL, mainId,userId,id), mHandler, this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    @Override
    public void onRefresh() {
        if (dynamicEntryList != null && !dynamicEntryList.isEmpty()){
            loadNewsData(dynamicEntryList.get(0).getID() + "");
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getContext(),"没有新数据",Toast.LENGTH_SHORT).show();
            layoutCommSwipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    private Handler mHandler = new MyHandler(this,1);

    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;
        int i = 0;
        public MyHandler(Fragment fragment,int i) {
            mFragmentReference = new WeakReference<Fragment>(fragment);
            this.i = i;
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment = mFragmentReference.get();
            if (fragment != null) {
                layoutCommSwipeRefresh.setRefreshing(false);
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if( i == 2){
                            return;
                        }
                        if (isLoading){
                            adapter.setIsLoadDone(true);
                        }
                        isLoading = false;
                        isRefresh = false;
                        if(adapter != null){
                           adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(getContext(), "已经全部加载完毕", Toast.LENGTH_SHORT).show();
                        if (dynamicEntryList.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ConstValues.MSG_ERROR:
                        if( i == 2){
                            return;
                        }
                        if (isLoading){
                            adapter.setIsLoadDone(true);
                        }
                        isLoading = false;
                        isRefresh = false;
                        if(adapter != null){
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(getContext(), "已经全部加载完毕", Toast.LENGTH_SHORT).show();
                        if (dynamicEntryList.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        if( i == 2){
                            return;
                        }
                        isLoading = false;
                        isRefresh = false;
                        Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        if( i == 2){
                            return;
                        }
                        isLoading = false;
                        isRefresh = false;
                        break;
                    case ConstValues.MSG_SUCESS:
                        if( i == 2){
                            return;
                        }
                        emptyView.setVisibility(View.GONE);
                        handleData((String) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DynamicEntry>>() {
        }.getType();
        List<DynamicEntry> _list = gson.fromJson(json, type);
        if (_list != null && !_list.isEmpty()){
            if (isLoading){//加载更多
                isLoading = false;
                dynamicEntryList.addAll(_list);
                //更新adapter
                adapter.addAll(_list);
                adapter.setStartLoad(false);
            }
            if (isRefresh){//刷新数据
                isRefresh = false;
                dynamicEntryList.addAll(0,_list);
                //更新adapter
                adapter.addRefreshData(_list);
            }
        }else{
            isLoading = false;
            isRefresh = false;
//            adapter.setStartLoad(false);
//            adapter.notifyDataSetChanged();
        }
    }
}
