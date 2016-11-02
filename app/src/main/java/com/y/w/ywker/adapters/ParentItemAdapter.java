package com.y.w.ywker.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.interf.OnCommAdapterItemClickListener;

import java.util.List;

/**
 * Created by lxs on 16/4/19.
 */
public class ParentItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public void setListener(OnCommAdapterItemClickListener listener) {
        this.listener = listener;
    }

    private OnCommAdapterItemClickListener listener;
    private List<String> items;
    private int currentSelected = -1;
    public ParentItemAdapter(List<String> items){
        this.items = items;
    }

    public void setCurrentSelected(int position){
        currentSelected = position;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comm_text_arrow_item,parent,false);
        return new ParentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ParentViewHolder){
            ((ParentViewHolder) holder).bindText(position,items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (this.items == null)
            return 0;
        return this.items.size();
    }

    private class ParentViewHolder extends RecyclerView.ViewHolder{

        TextView textView1;
        TextView textView2;
        public ParentViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.comm_text_arrrow_text1);
            textView2 = (TextView) itemView.findViewById(R.id.comm_text_arrrow_text2);
            textView2.setVisibility(View.INVISIBLE);

        }
        public void bindText(final int position,String text){
            textView1.setText(text);
            if (position == currentSelected){
                textView1.setTextColor(Color.parseColor("#2471b8"));
            }else{
                textView1.setTextColor(Color.parseColor("#000000"));
            }
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
