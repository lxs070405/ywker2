package com.y.w.ywker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.y.w.ywker.fragment.FragmentOrderDetails.MODIFY_TYPE;

/**
 * Created by lxs on 16/4/18.
 */
public class ActivityCommEdt extends SuperActivity implements View.OnClickListener{
    @Bind(R.id.layout_comm_toolbar)
    Toolbar layoutCommToolbar;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.activity_comm_edt_back)
    ImageView activityCommEdtBack;
    @Bind(R.id.activity_comm_edt_done)
    TextView activityCommDone;
    @Bind(R.id.dialog_comm_edt)
    EditText dialogCommEdt;

    private String title = "";
    private String orderId = "";
    private String msg = "";
    private String msgNew = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_edt);
        ButterKnife.bind(this);

        title = getIntent().getStringExtra("edttitle");
        orderId = getIntent().getStringExtra("orderId");
        msg = getIntent().getStringExtra("edtmsg");

        layoutCommToolbarTitle.setText(title);
        if (msg.equals("选填项") || msg.equals("必填项")){
            dialogCommEdt.setHint("请填写" + title);
        }else{
            dialogCommEdt.setText(msg);
        }
    }

    @OnClick({R.id.activity_comm_edt_done,R.id.activity_comm_edt_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_comm_edt_back:
                finish();
                break;
            case R.id.activity_comm_edt_done:
                msgNew = dialogCommEdt.getText().toString();
                if (orderId != null){
                    if (msgNew.equals(msg)){//不进行修改
                        finish();
                        break;
                    }else{
                        //提交进行修改
                        modifyMsg(title.equals("标题")?MODIFY_TYPE.ORDER_TITLE:MODIFY_TYPE.ORDER_DETAILS,msgNew);
                    }
                }else{
                    Intent i = new Intent();
                    if (!msgNew.equals("")){
                        i.putExtra("edtmsg",msgNew);
                    }
                    setResultBack(i);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 修改成功进行回调结果结束activity,失败提示修改失败
     * @param msg
     */
    private void modifyMsg(MODIFY_TYPE type,String msg){
        /**
         * 用于提交修改工单使用的HashMap
         */
        HashMap<String,String> mapModify = new HashMap<String,String>();
        OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);

        mapModify.put("ID",orderId);
        mapModify.put("MainID",offlineDataManager.getMainID());
        mapModify.put("UserID",offlineDataManager.getUserID());
        mapModify.put("UpdateDime", TimeUtils.formatDuring(System.currentTimeMillis()));
        mapModify.put("UpdateDetail",msg);
        switch (type){
            case ORDER_TITLE:
                mapModify.put("UpdateType","UpdateTitle");
                break;
            case ORDER_DETAILS:
                mapModify.put("UpdateType","UpdateDetail");
                break;
        }

        showLoading();
        YHttpManagerUtils httpManagerUtils = new YHttpManagerUtils(this, ConstValues.ORDER_MODIFY_URL,new MyHandler(this),getClass().getSimpleName());
        httpManagerUtils.startPostRequest(mapModify);
    }


    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity fragment) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
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
                        //使用默认值
                        Toast.makeText(ActivityCommEdt.this,"修改失败",Toast.LENGTH_SHORT).show();
                        break;
                    case ConstValues.MSG_NET_INAVIABLE:

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:
                        Toast.makeText(ActivityCommEdt.this,"修改成功",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent();
                        if (!msgNew.equals("")){
                            i.putExtra("edtmsg",msgNew);
                        }
                        setResultBack(i);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setResultBack(Intent intent){
        setResult(title.equals("标题") ? ConstValues.RESULT_FOR_PICKER_TITLE_EDT :
                ConstValues.RESULT_FOR_PICKER_DES_EDT, intent);
        finish();
    }
}
