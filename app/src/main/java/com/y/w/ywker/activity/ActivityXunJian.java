package com.y.w.ywker.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.y.w.ywker.R;
import com.y.w.ywker.fragment.FragmentCurrentJob;
import com.y.w.ywker.fragment.FragmentJob;
import com.y.w.ywker.utils.OfflineDataManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityXunJian extends SuperActivity {

    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.order_list_back)
    ImageView orderListBack;
    @Bind(R.id.order_search_btn)
    ImageView orderSearchBtn;
    @Bind(R.id.xunjain_tab)
    SmartTabLayout xunjainTab;
    @Bind(R.id.xunjian_viewpager)
    ViewPager xunjianViewpager;

    /**
     * 巡检任务
     */
    private FragmentJob fragmentJob;
    private FragmentCurrentJob fragmentCurrentJob;
    private String mainId;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xunjian);
        ButterKnife.bind(this);
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        mainId = offlineDataManager.getMainID();
        userId = offlineDataManager.getUserID();

        xunjianViewpager.setAdapter(new XunJianFragmentPagerAdapter(getSupportFragmentManager()));
        xunjainTab.setViewPager(xunjianViewpager);
    }

    /**
     * 切换到第一个页面
     */
    public void setIndex(){
        xunjianViewpager.setCurrentItem(0);
        fragmentCurrentJob.loadData();
    }

    public void RefreshCurrentJobData(){
        fragmentCurrentJob.loadData();
    }

    @OnClick({R.id.order_list_back, R.id.order_search_btn, R.id.xunjain_tab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_list_back:
                finish();
                break;
//            case R.id.order_search_btn:
//                Toast.makeText(this,"此功能有待开发 敬请期待",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.xunjain_tab:
//                break;
        }
    }

    public class XunJianFragmentPagerAdapter extends FragmentPagerAdapter {


        private String tabTitles[] = new String[]{"当前巡检", "巡检计划"};

        public XunJianFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    if(fragmentCurrentJob == null){
                        fragmentCurrentJob = new FragmentCurrentJob();
                    }

                    return fragmentCurrentJob;
                case 1:

                    if(fragmentJob == null){
                        fragmentJob = new FragmentJob();
                    }

                    return fragmentJob;
                default:
                    if(fragmentCurrentJob == null){
                        fragmentCurrentJob = new FragmentCurrentJob();
                    }
                    return fragmentCurrentJob;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }


}