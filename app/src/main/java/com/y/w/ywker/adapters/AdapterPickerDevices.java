package com.y.w.ywker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;

import java.util.List;

/**
 * Created by lxs on 16/5/4.
 */

public class AdapterPickerDevices extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public void setListener(OnCommAdapterItemClickListener listener) {
        this.listener = listener;
    }

    private OnCommAdapterItemClickListener listener;
    private List<String> items;
    public AdapterPickerDevices(List<String> items){
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comm_text_picker_devices_item,parent,false);
        return new DevicesCommVH(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DevicesCommVH){
            ((DevicesCommVH) holder).bindText(position,items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (this.items == null)
            return 0;
        return this.items.size();
    }

    private class DevicesCommVH extends RecyclerView.ViewHolder{

        TextView devicesName;
        public DevicesCommVH(View itemView) {
            super(itemView);
            devicesName = (TextView) itemView.findViewById(R.id.comm_text_picker_devices_name);
        }

        public void bindText(final int position,String text){
            devicesName.setText(text);
            if (listener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position);
                    }
                });
            }
        }
    }
}
