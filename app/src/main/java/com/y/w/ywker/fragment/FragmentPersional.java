package com.y.w.ywker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.activity.ActivityAbout;
import com.y.w.ywker.activity.ActivityContacts;
import com.y.w.ywker.activity.ActivityFeedBack;
import com.y.w.ywker.activity.ActivityMsgList;
import com.y.w.ywker.activity.ActivitySetting;
import com.y.w.ywker.activity.ActivityUserInfo;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/12.
 * 个人中心
 */
public class FragmentPersional extends Fragment {

    @Bind(R.id.personial_item_num)
    TextView personialItemNum;

    private YHttpManagerUtils httpManagerUtils;
    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;

        public MyHandler(Fragment fragment) {
            mFragmentReference = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            Fragment fragment = mFragmentReference.get();

            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:

                        break;
                    case ConstValues.MSG_ERROR:
                        Toast.makeText(getContext(), "暂无未读信息", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        handleData((String)msg.obj);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }

    private void handleData(String json) {
        if(personialItemNum != null){
            personialItemNum.setText(json);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_persinal, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void loadData() {
        httpManagerUtils = new YHttpManagerUtils(getContext(), String.format(ConstValues.Get_UNREAD_MSG_NUM, OfflineDataManager.getInstance(getContext()).getUserID()), new MyHandler(this), this.getClass().getName());
        httpManagerUtils.startRequest();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.personial_item_userinfo_layout,R.id.personial_item_user_layout,
    R.id.personial_item_about_layout,R.id.personial_item_feedback_layout,
    R.id.personial_item_setting_layout,R.id.personial_item_notify_layout})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.personial_item_userinfo_layout:
                Utils.start_Activity(getActivity(), ActivityUserInfo.class,new YBasicNameValuePair[]{});
                break;
            case R.id.personial_item_about_layout:
                Utils.start_Activity(getActivity(), ActivityAbout.class,new YBasicNameValuePair[]{});
                break;
            case R.id.personial_item_feedback_layout:
                Utils.start_Activity(getActivity(), ActivityFeedBack.class,new YBasicNameValuePair[]{});
                break;
            case R.id.personial_item_setting_layout:
                Utils.start_Activity(getActivity(), ActivitySetting.class,new YBasicNameValuePair[]{});
                break;
            case R.id.personial_item_notify_layout:
                Utils.start_Activity(getActivity(), ActivityMsgList.class,new YBasicNameValuePair[]{});
                break;
            case R.id.personial_item_user_layout:
                Utils.start_Activity(getActivity(), ActivityContacts.class, new YBasicNameValuePair[]{});
                break;
        }
    }
}