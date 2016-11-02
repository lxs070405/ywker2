package com.y.w.ywker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.entry.MsgEntry;
import com.y.w.ywker.interf.OnOrderListItemListener;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.TimeUtils;

import java.util.List;

/**
 * Created by lxs on 16/4/22.
 */

public class MsgListItemAdapter extends RecyclerView.Adapter<MsgListItemAdapter.MsgListItemVH>{

    private List<MsgEntry> lists;
    private Context ctx;

    public void setItemClickListener(OnOrderListItemListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private OnOrderListItemListener itemClickListener;
    public MsgListItemAdapter(Context ctx, List<MsgEntry> lists){
        this.ctx = ctx;
        this.lists = lists;
    }

    @Override
    public MsgListItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_msg_list_item,parent,false);
        return new MsgListItemVH(v);
    }

    @Override
    public void onBindViewHolder(MsgListItemVH holder, int position) {
        holder.bindData(this.lists.get(position));
    }

    @Override
    public int getItemCount() {
        if (lists != null){
            return lists.size();
        }
        return 0;
    }

    public class MsgListItemVH extends RecyclerView.ViewHolder{

        /**
         * 发起人姓名
         */
        private TextView author;
        /**
         * 时间
         */
        private TextView time;
        /**
         * 未读数量
         */
        private TextView count;

        /**
         * 消息内容
         */
        private TextView msg;

        /**
         * 头像
         */
        private ImageView header;

        public MsgListItemVH(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.msg_item_name);
            time = (TextView) itemView.findViewById(R.id.msg_item_time);
            count = (TextView) itemView.findViewById(R.id.msg_item_num);
            msg = (TextView) itemView.findViewById(R.id.msg_item_msg);
            header = (ImageView) itemView.findViewById(R.id.msg_list_item_header);
        }

        public void bindData(final MsgEntry entry){

            if (entry == null){
                LOG.e(ctx,"entry == null");
                return;
            }

            author.setText(entry.getName());
//            time.setText(TimeUtils.formatYwkerDate(entry.getMessageTime()));
            time.setText(TimeUtils.setTime(entry.getMessageTime()));
            if (entry.getCount() == 0){
                count.setVisibility(View.INVISIBLE);
            }else{
                count.setVisibility(View.VISIBLE);
                count.setText(entry.getCount() + "");
            }

            msg.setText(entry.getDetail());

            if (itemClickListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClickOrder(String.valueOf(entry.getID()), entry.getName(), "", "");
                    }
                });
            }
        }
    }

}
