package com.y.w.ywker.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.y.w.ywker.R;
import com.y.w.ywker.adapters.BasePagerAdapter;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 引导页
 */
public class ActivityYinDaoPage extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.start_Button)
    Button startButton;
    @Bind(R.id.indicator)
    LinearLayout indicator;
    @Bind(R.id.activity_yindaopage)
    RelativeLayout activityYindaopage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yindaopage);
        ButterKnife.bind(this);
        initView();
    }

    private PagerAdapter pagerAdapter;
    private ArrayList<View> views;
    private ImageView[] indicators = null;
    private int[] images;
    // 初始化视图
    private void initView() {
        // 设置引导图片
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  仅需在这设置图片 指示器和page自动添加
        images =  new int[]{R.drawable.indctor1, R.drawable.indctor2, R.drawable.indctor3, R.drawable.indctor4};
        views = new ArrayList<View>();
        indicators = new ImageView[images.length]; // 定义指示器数组大小
        for (int i = 0; i < images.length; i++) {
            // 循环加入图片
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(images[i]);
            views.add(imageView);
//            // 循环加入指示器
            indicators[i] = new ImageView(this);
            indicators[i].setImageResource(R.drawable.indicator_default);
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)  indicators[i].getLayoutParams();
//            layoutParams.width =  30;
//            layoutParams.height = 30;
//
//            indicators[i].setLayoutParams(layoutParams);
            indicators[i].setPadding(20,20,20,20);
            if (i == 0) {
                indicators[i].setImageResource(R.drawable.indicator_now);//Resource(R.drawable.indicator_now);
            }
            indicator.addView(indicators[i]);
        }
        pagerAdapter = new BasePagerAdapter(views);
        viewPager.setAdapter(pagerAdapter); // 设置适配器
        viewPager.setOnPageChangeListener(this);
    }


    @OnClick(R.id.start_Button)
    public void onClick() {
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        offlineDataManager.saveIsFristUse(false);
        Utils.start_Activity(this, LoginActivity.class, new YBasicNameValuePair[]{});
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 显示最后一个图片时显示按钮
        if (position == images.length - 1) {
            startButton.setVisibility(View.VISIBLE);
        } else {
            startButton.setVisibility(View.INVISIBLE);
        }
        // 更改指示器图片
        for (int i = 0; i < indicators.length; i++) {
            indicators[position].setImageResource(R.drawable.indicator_now);
            if (position != i) {
                indicators[i].setImageResource(R.drawable.indicator_default);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

    }
}
