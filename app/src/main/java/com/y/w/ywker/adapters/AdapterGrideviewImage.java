package com.y.w.ywker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lxs on 2016/10/25.
 */
public class AdapterGrideviewImage extends BaseAdapter{

    private  Context ctx;
    private List<String> data = new ArrayList<>();
    public AdapterGrideviewImage(Context ctx){
        this.ctx = ctx;
    }

    public void addData(List<String> strdata){
        if(data != null ){
            if(data.size() >= 3 || data.size() + strdata.size() >= 3){
                data.clear();
            }
            data.addAll(strdata);
        }
    }

    public List<String> getAdapterData(){
        return data;
    }
    public void  removeImage(int position){
        data.remove(position);
    }
    @Override
    public int getCount() {
        // 多返回一个用于展示添加图标
        if (data == null)
        {
            return 1;
        }
        else if (data.size() == ConstValues.MAX_IMAGE_SIZE)
        {
            return ConstValues.MAX_IMAGE_SIZE;
        }
        else
        {
            return data.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (data != null && data.size() == ConstValues.MAX_IMAGE_SIZE){
            return data.get(position);
        }
        else if (data == null || position - 1 < 0|| position > data.size()){
            return null;
        }else{
            return data.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(ctx);
            imageView.setAdjustViewBounds(false);//设置边界对齐
            imageView.setPadding(8, 8, 8, 8);//设置间距
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
            imageView.setLayoutParams(new GridView.LayoutParams(180,//ViewGroup.LayoutParams.MATCH_PARENT
                    180));//设置ImageView对象布局
        }
        else {
            imageView = (ImageView) convertView;
        }
        if(isShowAddItem(position)){
           imageView.setImageResource(R.drawable.icon_addpic_unfocused);
        }else {
            Log.e("lxs", "getView: " +data.get(0));
            String path = data.get(position);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
            opts.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
            imageView.setImageBitmap(bitmap);
//            imageView.setImageURI(Uri.parse("file://" + data.get(0)));//为ImageView设置图片资源
        }

        return imageView;
    }

    public boolean isShowAddItem(int position){
        if(data.size() >= ConstValues.MAX_IMAGE_SIZE){
            return false;
        }
        int size = data == null ? 0 : data.size();
        return position == size;
    }

}
