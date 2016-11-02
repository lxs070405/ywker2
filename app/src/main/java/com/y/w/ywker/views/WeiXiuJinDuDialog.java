package com.y.w.ywker.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.y.w.ywker.R;

import java.util.List;

import butterknife.Bind;


/**
 * 自定义的dialog 维修进度的提示框
 *
 * @author lxs
 *         2016年8月29日
 */
public class WeiXiuJinDuDialog extends Dialog implements
        View.OnClickListener {
    @Bind(R.id.listview)
    ListView listview;
    private DialogClickListener listener;
    Context context;
    private String leftBtnText;
    private String rightBtnText;

    public WeiXiuJinDuDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    private List<String> contentlist;

    /**
     * 自定义dialog
     * @param theme        主题
     * @param contentlist  主体文字内容信息
     * @param leftBtnText  左按钮文字，若为""则隐藏
     * @param rightBtnText 右按钮文字，若为""则隐藏
     * @param listener     回调接口
     */
    public WeiXiuJinDuDialog(Context context, int theme, List<String> contentlist,
                             String leftBtnText, String rightBtnText,
                             DialogClickListener listener) {
        super(context, theme);
        this.context = context;
        this.contentlist = contentlist;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.diaog_weixiujindu);
        listview = (ListView) findViewById(R.id.listview);
        initView();
        initDialog(context);
    }

    /**
     * 设置dialog的宽为屏幕的3分之1
     */
    private void initDialog(Context context) {
        setCanceledOnTouchOutside(false);
        setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getRepeatCount() == 0) {
                    return false;
                } else {
                    return false;
                }
            }
        });
        WindowManager windowManager = this.getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() / 6 * 5); //// 设置宽度
        this.getWindow().setAttributes(lp);
    }

    private void initView() {
        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.order_status_item, contentlist);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.setWeiXiuJinDu(WeiXiuJinDuDialog.this,contentlist.get(position),position);
            }
        });
    }

    public interface DialogClickListener {
        void setWeiXiuJinDu(WeiXiuJinDuDialog myDialog,String string,int position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.rl_d:
//                this.dismiss();
//                break;
        }
    }
}