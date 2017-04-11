package com.y.w.ywker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.entry.DynamicEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.views.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxs on 16/4/20.
 * 工单适配器
 */

public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public void setListener(OnCommAdapterItemClickListener listener) {
        this.listener = listener;
    }


    private enum ITEM_TYPE{
        TYPE_ITEM,
        TYPE_FOOTER
    }

    private boolean isLoadDone = false;

    private boolean startLoad = true;

    public boolean isLoadDone() {
        return isLoadDone;
    }

    public void setIsLoadDone(boolean isLoadDone) {
        this.isLoadDone = isLoadDone;
        notifyDataSetChanged();
    }

    public boolean isStartLoad() {
        return startLoad;
    }
    public void setIsStartLoad(boolean isLoad){
        startLoad = isLoad;
    }
    public void setStartLoad(boolean startLoad) {
        this.startLoad = startLoad;
    }


    private OnCommAdapterItemClickListener listener;

    private List<DynamicEntry> dynamicEntryList = new ArrayList<DynamicEntry>();
    private Context ctx;

    public DynamicAdapter(Context ctx){
        this.ctx = ctx;
    }

    /**
     * 添加加载更多的数据
     * @param _dynamicEntryList
     */
    public void addAll(List<DynamicEntry> _dynamicEntryList){
        if (_dynamicEntryList != null && !_dynamicEntryList.isEmpty()){
            if(dynamicEntryList.size() > 0){
                dynamicEntryList.clear();
            }
//            for (DynamicEntry entry : _dynamicEntryList) {
//                dynamicEntryList.contains(entry);
//                dynamicEntryList.remove(entry);
//            }
            dynamicEntryList.addAll(_dynamicEntryList);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加加载最新数据
     * @param _dynamicEntryList
     */
    public void addRefreshData(List<DynamicEntry> _dynamicEntryList){
        if (_dynamicEntryList != null && !_dynamicEntryList.isEmpty()){
           if(dynamicEntryList.size() > 0){
               dynamicEntryList.clear();
           }
            dynamicEntryList.addAll(0,_dynamicEntryList);
            notifyDataSetChanged();
        }
    }

    public void removeAll(){
        if (dynamicEntryList != null){
            dynamicEntryList.clear();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = null;
        if (viewType == ITEM_TYPE.TYPE_ITEM.ordinal()){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dynamic_item,parent,false);
            return new DynamicVH(v);
        }else if(viewType == ITEM_TYPE.TYPE_FOOTER.ordinal()){
            v = LayoutInflater.from(ctx).inflate(R.layout.adapter_item_footer,parent,false);
            return new FooterViewHoler(v);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DynamicVH){
            ((DynamicVH)holder).bindData(position);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadDone){
            return ITEM_TYPE.TYPE_ITEM.ordinal();
        }
        return position + 1 == getItemCount() ? ITEM_TYPE.TYPE_FOOTER.ordinal() : ITEM_TYPE.TYPE_ITEM.ordinal();
    }

    @Override
    public int getItemCount() {
        if (isLoadDone && dynamicEntryList != null){
            return dynamicEntryList.size();
        }
        if (dynamicEntryList != null){
            return dynamicEntryList.size() + 1;
        }
        return 0;
    }

    public class DynamicVH extends RecyclerView.ViewHolder{

        TextView time;
        TextView author;
        TextView status;
        TextView title;
        TextView msg;
        TextView auth;//身份
        ImageView isred_image;
        public DynamicVH(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.dynamic_item_time);
            author = (TextView) itemView.findViewById(R.id.dynamic_item_author);
            status = (TextView) itemView.findViewById(R.id.dynamic_item_status);
            title = (TextView) itemView.findViewById(R.id.dynamic_item_title);
            msg = (TextView) itemView.findViewById(R.id.dynamic_item_msg);
            auth = (TextView) itemView.findViewById(R.id.dynamic_item_auth);
            isred_image = (ImageView) itemView.findViewById(R.id.isred_image);
        }

        public void bindData(final int position){
            DynamicEntry entry = dynamicEntryList.get(position);
//             time.setText(TimeUtils.formatYwkerDate(entry.getSendTime()));
            time.setText(TimeUtils.setTime(entry.getSendTime()));
            auth.setText("");
            author.setText(entry.getSheetID()+"");//"工单编号 : "+
//            status.setText(entry.getMessageType());
//             title.setText(entry.getTitle());
            title.setText(entry.getClient());
            msg.setText(entry.getSendDetail());
            if(entry.getIsRead()){
                isred_image.setVisibility(View.INVISIBLE);
            }else {
                isred_image.setVisibility(View.VISIBLE);
            }
            if (listener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position);
                        isred_image.setVisibility(View.INVISIBLE);
                        dynamicEntryList.get(position).setIsRead(true);
                    }
                });
            }
        }
    }

    class FooterViewHoler extends RecyclerView.ViewHolder{

        CircleProgressBar progressBar;
        TextView tip;
        public FooterViewHoler(View itemView) {
            super(itemView);
            progressBar = (CircleProgressBar) itemView.findViewById(R.id.search_result_item_footer_progress);
            tip = (TextView) itemView.findViewById(R.id.search_result_item_footer_tip);
            if (isStartLoad()&& dynamicEntryList.size() >= 10){
                progressBar.setVisibility(View.VISIBLE);

                tip.setVisibility(View.GONE);
            }else{
                progressBar.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
            }
            if (isLoadDone){
                Log.e("tag", "加载完毕,显示tip");
                progressBar.setVisibility(View.GONE);
                tip.setVisibility(View.VISIBLE);
            }
//            else {
//                progressBar.setVisibility(View.GONE);
//                tip.setVisibility(View.GONE);
//            }
        }
    }
}