package com.y.w.ywker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.entry.WeiXiuListEntry;
import com.y.w.ywker.interf.OnOrderListItemListener;
import com.y.w.ywker.utils.TimeUtils;

import java.util.List;

/**
 * Created by lxs on 16/4/22.
 */
public class WeixiuOrderListItemAdapter extends RecyclerView.Adapter<WeixiuOrderListItemAdapter.OrderListItemVH> {

    private List<WeiXiuListEntry> lists;
    private Context ctx;

    public void setItemClickListener(OnOrderListItemListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private OnOrderListItemListener itemClickListener;
    public WeixiuOrderListItemAdapter(Context ctx, List<WeiXiuListEntry> lists){
        this.ctx = ctx;
        this.lists = lists;
    }

    @Override
    public OrderListItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpter_weixiuorde_item,parent,false);
        return new OrderListItemVH(v);
    }

    @Override
    public void onBindViewHolder(OrderListItemVH holder, int position) {
        holder.bindData(this.lists.get(position));
    }



    @Override
    public int getItemCount() {
        if (lists != null){
            return lists.size();
        }
        return 0;
    }

    public class OrderListItemVH extends RecyclerView.ViewHolder{
        private TextView mTv_orderId;
        private TextView mTv_OrderTitle;
        private TextView mTv_weixiuType;
        private TextView mTv_time;

        public OrderListItemVH(View itemView) {
            super(itemView);
            mTv_orderId = (TextView) itemView.findViewById(R.id.tv_orderId);
            mTv_OrderTitle = (TextView) itemView.findViewById(R.id.tv_OrderTitle);
            mTv_weixiuType = (TextView) itemView.findViewById(R.id.tv_weixiuType);
            mTv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void bindData(final WeiXiuListEntry child){
           mTv_orderId.setText(child.getID()+"");
           mTv_OrderTitle.setText(child.getClientName());
            mTv_weixiuType.setText(child.getTypeName());
            String str = child.getRealseTime();
            mTv_time.setText(TimeUtils.frmatTime(str,"2"));
            if (itemClickListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClickOrder(String.valueOf(child.getID()),child.getClientName(),child.getMainID(),child.getTaskState());
                    }
                });
            }

        }
    }
}
