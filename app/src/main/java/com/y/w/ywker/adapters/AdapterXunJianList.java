package com.y.w.ywker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.entry.XunJianListEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;

import java.util.List;

/**
 * Created by lxs on 2016/7/20.
 * 巡检列表适配器
 */
public class AdapterXunJianList extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    /**
     * 1: 巡检计划 2:当前巡检
     */
    private  int type;

    private  Context ctx;
    public AdapterXunJianList(Context ctx,int type){
        this.ctx = ctx;
        this.type = type;
    }
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);

    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    private List<XunJianListEntry> list;

    public void addData(List<XunJianListEntry> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_xunjianlist_item,parent,false);

        return  new XunJianVH(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,  int position) {
        if (holder != null && list != null){
            if(holder instanceof XunJianVH ){
                (( XunJianVH)holder).bindData(list.get(position));
            }
        }

        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }
    private OnCommAdapterItemClickListener listener;
    public void setListener(OnCommAdapterItemClickListener listener) {
        this.listener = listener;
    }
    class XunJianVH extends RecyclerView.ViewHolder{

        TextView KeHuName;
        TextView time;
        TextView using;
        RelativeLayout startXunJian;

        public XunJianVH(View itemView) {
            super(itemView);
            KeHuName = (TextView) itemView.findViewById(R.id.tv_KeHuName);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            using = (TextView) itemView.findViewById(R.id.tv_using);
            startXunJian = (RelativeLayout) itemView.findViewById(R.id.tv_StartXunJian);
            if(type == 1){
                startXunJian.setVisibility(View.VISIBLE);
            }else if(type == 2){
                startXunJian.setVisibility(View.GONE);
            }
        }
        public void bindData(XunJianListEntry entry){
            KeHuName.setText(entry.getClientName());
            String timestr;
            if(type == 1){
                timestr = entry.getPlanDate().substring(0,entry.getPlanDate().indexOf(" "));
                timestr = timestr.replace("/","-");
                time.setText("计划时间 : "+timestr);
                using.setText(entry.getPlanExeName());
            }else if(type == 2){
                timestr = entry.getExecuteDate().substring(0,entry.getExecuteDate().indexOf(" "));
                timestr = timestr.replace("/","-");
                time.setText("执行时间 : "+timestr);
                using.setText(entry.getExecuteUserName());
            }
        }


    }
}
