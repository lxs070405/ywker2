package com.y.w.ywker.basepackege;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.y.w.ywker.ConstValues;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelConfig;

/**
 * Created by lxs on 2016/10/25.
 */

public class ImageSelectorBaseUtile {

    private static ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };
    public static ImgSelConfig initImageSelectorUtil(){
        ImgSelConfig config = new ImgSelConfig.Builder(loader).multiSelect(true)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 确定按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                // 确定按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                .title("图片")
                .titleColor(Color.WHITE)
                .titleBgColor(Color.parseColor("#3F51B5"))
                .cropSize(1, 1, 200, 200)
                .needCrop(false)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(ConstValues.MAX_IMAGE_SIZE)
                .build();
//        ImgSelActivity.startActivity(this, config, ConstValues.Image_REQUEST_CODE);
        return config;
    }
}
