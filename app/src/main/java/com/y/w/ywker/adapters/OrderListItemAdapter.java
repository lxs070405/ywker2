package com.y.w.ywker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tel.qudu.reader.stickyrecyclerviewlibrary.StickyRecyclerHeadersAdapter;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.OrderListItemGroupEntry;
import com.y.w.ywker.interf.OnOrderListItemListener;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.TimeUtils;

import java.util.List;

/**
 * Created by lxs on 16/4/22.
 */

public class OrderListItemAdapter extends RecyclerView.Adapter<OrderListItemAdapter.OrderListItemVH> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private List<OrderListItemGroupEntry> lists;
    private Context ctx;

    public void setItemClickListener(OnOrderListItemListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private OnOrderListItemListener itemClickListener;
    public OrderListItemAdapter(Context ctx,List<OrderListItemGroupEntry> lists){
        this.ctx = ctx;
        this.lists = lists;
    }

    @Override
    public OrderListItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_orderlist_item,parent,false);
        return new OrderListItemVH(v);
    }

    @Override
    public void onBindViewHolder(OrderListItemVH holder, int position) {
        holder.bindData(this.lists.get(position));
    }

    @Override
    public long getHeaderId(int position) {
        if (this.lists != null && this.lists.size() > 0){
            return lists.get(position).getParentID();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comm_sticky_header_item,parent,false);
        return new OrderListHeadVH(headerView);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OrderListHeadVH){
            ((OrderListHeadVH) holder).bindData(lists.get(position).getParentTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (lists != null){
            return lists.size();
        }
        return 0;
    }

    public class OrderListItemVH extends RecyclerView.ViewHolder{
        /**
         * 工单编号
         */
        private TextView order_num_tv;
        /**
         * 标题
         */

        private TextView title;
        /**
         * 最新回复
         */
        private TextView msg;
        /**
         * 发起人姓名
         */
        private TextView author;
        /**
         * 时间
         */
        private TextView time;
        /**
         * 状态
         */
        private TextView status;
        /**
         * 身份(关注着,发起人....)
         */
        private TextView auth;

        public OrderListItemVH(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.dynamic_item_title);
//            order_num_tv = (TextView) itemView.findViewById(R.id.order_num_tv);
            time = (TextView) itemView.findViewById(R.id.dynamic_item_time);
//            auth = (TextView) itemView.findViewById(R.id.dynamic_item_auth);
            author = (TextView) itemView.findViewById(R.id.dynamic_item_author);
            msg = (TextView) itemView.findViewById(R.id.dynamic_item_msg);
            status = (TextView) itemView.findViewById(R.id.dynamic_item_status);
        }

        public void bindData(final OrderListItemGroupEntry entry){
//            title.setText(entry.getSheetTitle());
            title.setText(entry.getClientName());
//            order_num_tv.setText(entry.getID());
//            time.setText(TimeUtils.formatYwkerDate(entry.getWriteTime()));
            time.setText(TimeUtils.setTime(entry.getWriteTime()));
//            auth.setVisibility(View.INVISIBLE);
//            author.setText(entry.getClientName());
            author.setText(entry.getID()+"");//"工单编号 : "
            msg.setText(entry.getMessage());
            status.setText(entry.getSheetStateCN());
            if (itemClickListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClickOrder(String.valueOf(entry.getID()),entry.getSheetTitle(),entry.getParentTitle(),entry.getSheetStateCN());
                    }
                });
            }
        }
    }

    public class OrderListHeadVH extends RecyclerView.ViewHolder{
        private TextView type;
        public OrderListHeadVH(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.comm_sticky_header_tv);
        }

        public void bindData(String title){
            if (title != null){
                LOG.e(ctx,title);
                type.setText(title);
            }else{
                type.setText("未知");
            }
        }
    }
}
