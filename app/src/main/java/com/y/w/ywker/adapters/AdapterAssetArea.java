package com.y.w.ywker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.entry.AssetAreaEntry;

import java.util.List;

/**
 * Created by lxs on 2016/8/29.
 * 品牌
 */
public class AdapterAssetArea extends BaseAdapter{
    private List<AssetAreaEntry> modeEntryList;
    private Context ctx;
    public AdapterAssetArea(List<AssetAreaEntry> modeEntryList, Context ctx){
        this.modeEntryList = modeEntryList;
        this.ctx = ctx;
    }
    @Override
    public int getCount() {
        return modeEntryList.size();
    }

    @Override
    public Object getItem(int position) {
        return modeEntryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = View.inflate(ctx, R.layout.listview_mode_item,null);
            holder.pinpai = (TextView) convertView.findViewById(R.id.tv_PinPai);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        holder.pinpai.setText(modeEntryList.get(position).getAreaName());
        return convertView;
    }
    class Holder{
        TextView pinpai;
    }
}
