package com.y.w.ywker.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.adapters.AdapterBrandName;
import com.y.w.ywker.entry.ModeEntry;
import com.y.w.ywker.entry.XingHaoEntry;
import com.y.w.ywker.utils.YHttpManagerUtils;
import com.y.w.ywker.views.MyDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 型号(设备品牌)
 */
public class ActivityXingHaoList extends SuperActivity {

    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.btn_add)
    Button btnAdd;
    private String MainID;
    private String TypeID;
    private String BrandID;
    private String ClientID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xing_hao_list);
        ButterKnife.bind(this);
        String mode = getIntent().getStringExtra("mode");
        MainID = getIntent().getStringExtra("MainID");
        if (mode.equals("1")) {
            tvTitle.setText("选择品牌");
            btnAdd.setVisibility(View.GONE);
            httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_MODE_URL, MainID), new MyHandler(this, 3), this.getClass().getName());
            httpManagerUtils.startRequest();
        } else if (mode.equals("2")) {
            ClientID = getIntent().getStringExtra("ClientID");
            TypeID = getIntent().getStringExtra("keyTypeID");
            BrandID = getIntent().getStringExtra("BrandID");
            httpManagerUtils = new YHttpManagerUtils(this, String.format(ConstValues.GET_XINGHAO_URL, MainID, TypeID, BrandID), new MyHandler(this, 1), this.getClass().getName());
            httpManagerUtils.startRequest();
        }
        showLoading();

    }

    private YHttpManagerUtils httpManagerUtils;

    //    private Handler mHandler = new MyHandler(this,1);
    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;
        int type = 0;

        public MyHandler(AppCompatActivity fragment, int type) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
            this.type = type;
        }

        @Override
        public void handleMessage(Message msg) {
            dismissLoading();
            AppCompatActivity fragment = mFragmentReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        break;
                    case ConstValues.MSG_ERROR:
                        if (type == 2) {
                            Toast.makeText(getBaseContext(), "增加型号失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:

                        if (msg.obj != null) {
                            if (type == 1) {
                                String data = (String) msg.obj;
                                parseData(data);
                            }
                            if (type == 2) {
                                proDialog.dismiss();
                                entry.setID((String) msg.obj);
                                if( xinghaoEntryList == null){//第一次增加时
                                    xinghaoEntryList = new ArrayList<>();
                                    xinghaoEntryList.add(entry);
                                    setLisener();
                                }else {
                                    xinghaoEntryList.add(0, entry);
                                    adapter.notifyDataSetChanged();
                                }


                            }
                            if (type == 3) {
                                String json = (String) msg.obj;
                                parseModeData(json);
                            }
                            if(type == 4){
                                if(intent != null){
                                    intent.putExtra("AssetID",(String)msg.obj);
                                    Log.e("lxs", "handleMessage: 型号页面传递"+(String)msg.obj);
                                    setResult(ConstValues.RESULT_FOR_DEVICES_XINGHAO, intent);
                                    finish();
                                }
                            }
                        }
                        break;
                }
            }
        }
    }

    private void parseModeData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ModeEntry>>() {
        }.getType();
        modeEntryList = gson.fromJson(json, type);
        if (modeEntryList != null && modeEntryList.size() > 0) {
            listview.setAdapter(new AdapterBrandName(modeEntryList,ActivityXingHaoList.this));
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String modleId = modeEntryList.get(position).getID();
                    String modleName = modeEntryList.get(position).getBrandName();
                    Intent intent = new Intent(ActivityXingHaoList.this, DevicesInfoActivity.class);
                    intent.putExtra("_ids", modleId);
                    intent.putExtra("result", modleName);
//                    Log.e("lxs", "设备品牌页: parseModeData" + "设备品牌名称返回" + modleId
//                            +"BrandID---->"+modleId);
                    setResult(ConstValues.RESULT_FOR_DEVICES_PINPAI, intent);
                    finish();
                }
            });
        }
    }

    private List<ModeEntry> modeEntryList;
    private List<XingHaoEntry> xinghaoEntryList;
    private MyAdapter adapter  = new MyAdapter();

    private void parseData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<XingHaoEntry>>() {
        }.getType();

        xinghaoEntryList = gson.fromJson(json, type);
        setLisener();
    }

    private void setLisener() {
        if (xinghaoEntryList != null && xinghaoEntryList.size() > 0) {
            Log.e("lxs", "parseData: "+xinghaoEntryList.size());
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String modleId = xinghaoEntryList.get(position).getID();
                    String modleName = xinghaoEntryList.get(position).getModelName();
                    httpManagerUtils = new YHttpManagerUtils(ActivityXingHaoList.this, String.format(ConstValues.GET_ASEETID_URL, MainID, TypeID, BrandID, modleId,ClientID), new MyHandler(ActivityXingHaoList.this, 4), this.getClass().getName());
                    httpManagerUtils.startRequest();
                    intent = new Intent(ActivityXingHaoList.this, DevicesInfoActivity.class);
                    intent.putExtra("_ids", modleId);
                    intent.putExtra("result", modleName);
                }
            });
        }
    }


    private  Intent intent;
    private ProgressDialog proDialog;

    @OnClick({R.id.btn_back, R.id.btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_add:
                new MyDialog(ActivityXingHaoList.this, R.style.MyDialog, "增加型号", "确定", "取消", new MyDialog.DialogClickListener() {
                    @Override
                    public void onRightBtnClick(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onLeftBtnClick(MyDialog myDialog, String reslut) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        entry = new XingHaoEntry();
                        entry.setModelName(reslut);
                        entry.setNotBindCount("0");
                        map.put("MainID", MainID);
                        map.put("TypeID", TypeID);
                        map.put("ModelName", reslut);
                        map.put("BrandID", BrandID);
                        YHttpManagerUtils managerUtils = new YHttpManagerUtils(ActivityXingHaoList.this, ConstValues.POST_ADD_XINGHAO, new MyHandler(ActivityXingHaoList.this, 2), getClass().getName());
                        managerUtils.startPostRequest(map);
                        myDialog.dismiss();
                        proDialog = new ProgressDialog(ActivityXingHaoList.this);
                        proDialog.setTitle("提示");
                        proDialog.setMessage("正在提交");
                        proDialog.show();
                    }
                }).show();

                break;
        }
    }

    private XingHaoEntry entry;

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return xinghaoEntryList.size();
        }

        @Override
        public Object getItem(int position) {
            return xinghaoEntryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHodler hodler = null;
            if (convertView == null) {
                hodler = new ViewHodler();
                convertView = View.inflate(ActivityXingHaoList.this, R.layout.listview_item_xinghao, null);
                hodler.XingHao = (TextView) convertView.findViewById(R.id.tv_XingHao);
                hodler.Number = (TextView) convertView.findViewById(R.id.tv_Number);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }
            hodler.XingHao.setText(xinghaoEntryList.get(position).getModelName());
            String num = xinghaoEntryList.get(position).getNotBindCount();
            if (num.equals("0")||num.equals("")) {
                hodler.Number.setText("");//无设备
            } else {
                hodler.Number.setText("未绑定 " + num);
            }
            return convertView;
        }
    }

    class ViewHodler {
        TextView XingHao;
        TextView Number;
    }
}
