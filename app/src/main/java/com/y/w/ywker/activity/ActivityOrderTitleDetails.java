package com.y.w.ywker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.y.w.ywker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lxs on 16/5/25.
 * 查看工单详情的标题页
 */
public class ActivityOrderTitleDetails extends AppCompatActivity {
    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.activity_order_title_back)
    ImageView activityOrderTitleBack;
    @Bind(R.id.order_title_tv)
    TextView orderTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_title_details);
        ButterKnife.bind(this);

        /**
         * 获取传过来的数据
         */
        String msg = getIntent().getStringExtra("details");

        /**
         * 设置activity的标题
         */
        layoutCommToolbarTitle.setText("详情");

        if (!TextUtils.isEmpty(msg)){
            orderTitleTv.setText(msg);
        }

        activityOrderTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
