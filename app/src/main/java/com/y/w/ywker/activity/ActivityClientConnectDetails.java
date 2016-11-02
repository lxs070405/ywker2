package com.y.w.ywker.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.activity.SuperActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/9.
 * 客户联系人详情查看
 */

public class ActivityClientConnectDetails extends SuperActivity {

    @Bind(R.id.connect_details_arrrow1_text2)
    TextView connectDetailsArrrow1Text2;
    @Bind(R.id.connect_details_arrrow2_text2)
    TextView connectDetailsArrrow2Text2;
    @Bind(R.id.connect_details_arrrow3_text2)
    TextView connectDetailsArrrow3Text2;
    @Bind(R.id.connect_details_arrow4_text2)
    TextView connectDetailsArrow4Text2;
    @Bind(R.id.connect_details_arrow5_text2)
    TextView connectDetailsArrow5Text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_connect_details);
        ButterKnife.bind(this);
        connectDetailsArrrow1Text2.setText(getIntent().getStringExtra("connect_name"));
        connectDetailsArrrow2Text2.setText(getIntent().getStringExtra("connect_bumen"));
        connectDetailsArrrow3Text2.setText(getIntent().getStringExtra("connect_tel"));
        connectDetailsArrow4Text2.setText(getIntent().getStringExtra("connect_email"));
        connectDetailsArrow5Text2.setText(getIntent().getStringExtra("connect_wx"));
    }

    @OnClick({R.id.client_connect_details_img_back})
    public void onBack(){
        finish();
    }
}
