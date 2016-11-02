package com.y.w.ywker.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.entry.ConnectEntry;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;

import java.util.List;

/**
 * Created by lxs on 16/4/26.
 */
public class PickerConactsAdapter extends RecyclerView.Adapter<ViewHolder>{
    public void setListener(OnCommAdapterItemClickListener listener) {
        this.listener = listener;
    }

    private OnCommAdapterItemClickListener listener;
    private List<ConnectEntry> items;
    public PickerConactsAdapter(List<ConnectEntry> items){
        this.items = items;
    }

    private boolean isSelectAll = false;
    /**
     * 全部选中
     */
    public void selecteAll(){
        isSelectAll = true;
        notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void unSelecteAll(){
        isSelectAll = false;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comm_text_picker_connect_item,parent,false);
        return new ParentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ParentViewHolder){
            ((ParentViewHolder) holder).bindText(position,items.get(position).getUserName());
        }
    }

    @Override
    public int getItemCount() {
        if (this.items == null)
            return 0;
        return this.items.size();
    }

    private class ParentViewHolder extends ViewHolder{

        TextView textView1;
        CheckBox cbx;
        public ParentViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.comm_text_picker_text1);
            cbx = (CheckBox) itemView.findViewById(R.id.comm_picker_cbx);
        }
        public void bindText(final int position,String text){
            textView1.setText(text);
            if (isSelectAll){
                cbx.setChecked(true);
            }else{
                cbx.setChecked(false);
            }
            if (listener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         *调用选择数据
                         */
                        listener.onItemClick(position);
                        if (cbx.isChecked()){
                            cbx.setChecked(false);
                        }else{
                            cbx.setChecked(true);
                        }

                    }
                });
            }
        }
    }
}
