package com.y.w.ywker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.XunJianDetailEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityXunJianLook extends SuperActivity implements AdapterView.OnItemClickListener {


    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.btn_back_devices_info)
    ImageView btnBackDevicesInfo;
    @Bind(R.id.tv_post_jieshu)
    TextView tvPostJieshu;
    @Bind(R.id.tv_KeHuName)
    TextView tvKeHuName;
    @Bind(R.id.tv_ZhiXingTime)
    TextView tvZhiXingTime;
    @Bind(R.id.tv_XunJianNum)
    TextView tvXunJianNum;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.image_add)
    ImageView imageAdd;
    private String mainId;
    private YHttpManagerUtils httpManagerUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xunianlook);
        ButterKnife.bind(this);
        showLoading();
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
        mainId = offlineDataManager.getMainID();
       String position =  getIntent().getStringExtra("position");
        ConstValues.planId = position;
        httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_XUNJIAN_LISTDetail, position,mainId), new MyHandler(this), this.getClass().getName());
        httpManagerUtils.startRequest();

    }
    private MyAdapter myadapter;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utils.start_Activity(this,ActivityLookedXunJianAseet.class,new YBasicNameValuePair[]{
                new YBasicNameValuePair("AssetRecordId",list.get(position).getAssetRecordId())});
    }



    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(list != null){
                return list.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            View view ;
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                view = View.inflate(ActivityXunJianLook.this,R.layout.xunjiandetail_listview_item,null);
                holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
                holder.tv_bianHao = (TextView) view.findViewById(R.id.tv_XuLieHao);
                holder.tv_AseetName = (TextView) view.findViewById(R.id.tv_AseetName);
                holder.tv_desc = (ImageView) view.findViewById(R.id.tv_desc);
                view.setTag(holder);
            }else {
                view = convertView;
               holder = (ViewHolder) convertView.getTag();
            }
            String date = list.get(position).getAseetExecuDate();
            if(date.contains("-")){
                holder.tv_time.setText(TimeUtils.frmatTime(list.get(position).getAseetExecuDate(),"2"));
            }else {
                holder.tv_time.setText(TimeUtils.frmatTime(list.get(position).getAseetExecuDate(),"1"));
            }

            holder.tv_bianHao.setText("设备编号 : "+ list.get(position).getQBCode());
            holder.tv_AseetName.setText("设备名称 : "+ list.get(position).getAssetName());
//            holder.tv_desc.setText(list.get(position).getAssetStatus());
            if(list.get(position).getAssetStatus().equals("故障")){
                holder.tv_desc.setImageResource(R.drawable.bad);
            }else{
                holder.tv_desc.setImageResource(R.drawable.good);
            }
            return view;
        }
    }
    private class ViewHolder{
        private  TextView tv_time;
        private  TextView tv_bianHao;
        private  TextView tv_AseetName;
        private  ImageView tv_desc;
    }


    @OnClick({R.id.btn_back_devices_info, R.id.tv_post_jieshu, R.id.image_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_devices_info:
                finish();
                break;
            case R.id.tv_post_jieshu:
                if(!tvXunJianNum.getText().toString().equals("0")){
                    Utils.start_Activity(this,ActivityXunJianOver.class, new YBasicNameValuePair[]{
                            new YBasicNameValuePair("PatrolID", ConstValues.planId),
                            new YBasicNameValuePair("recordId", list.get(0).getRecordId()),
                            new YBasicNameValuePair("Number",tvXunJianNum.getText().toString())
                    });
                }else {
                    Toast.makeText(this,"当前巡检列表为空不能结束",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.image_add:
                Utils.start_Activity(this,BindDevicesActivity.class, new YBasicNameValuePair[]{
                        new YBasicNameValuePair("fromSource", "3")
                });
                break;
        }
    }



    class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mFragmentReference;
        public MyHandler(AppCompatActivity activitiy) {
            mFragmentReference = new WeakReference<AppCompatActivity>(activitiy);
        }
        @Override
        public void handleMessage(Message msg) {
            dismissLoading();
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null){
                switch (msg.what){
                    case ConstValues.MSG_FAILED:
                    case ConstValues.MSG_ERROR:
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        handleData((String) msg.obj);
                        break;
                }
            }
        }
    }
    private List<XunJianDetailEntry> list;
    private void handleData(String json) {
        Log.e("lxs", "巡检任务详情:" + json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<XunJianDetailEntry>>() {
        }.getType();
        list =  gson.fromJson(json, listType);
        if(list != null && list.size() > 0){
            tvKeHuName.setText(list.get(0).getClientName());
            tvZhiXingTime.setText(FmartString(list.get(0).getExecuteDate()));

            ConstValues.PatrolRecordID = list.get(0).getRecordId();
            if(!list.get(0).getAssetName().equals("")){
                tvXunJianNum.setText((list.size())+"");
                myadapter = new MyAdapter();
                listview.setAdapter(myadapter);
                listview.setOnItemClickListener(this);
            }

        }
    }

    /**
     * 把后台给的时间数据格式化成 yyyy-MM-dd
     * @param timestr
     * @return
     */
    private String FmartString(String timestr){
        timestr = timestr.substring(0,timestr.indexOf(" "));
        timestr = timestr.replace("/","-");
        return timestr;
    }

    @Override
    protected void onDestroy() {
        if (httpManagerUtils != null) {
            httpManagerUtils.setIsAviable(false);
            httpManagerUtils.cancle();
        }
        super.onDestroy();
    }
}
