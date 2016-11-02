package com.y.w.ywker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.y.w.ywker.interf.CommDialogSelectListener;
import com.y.w.ywker.views.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxs on 16/4/18.
 * 常规对话框
 */
public class YCommListDialog {

    private MaterialDialog dialog;
    private CommDialogSelectListener listener;
    private ListView lv;

    public YCommListDialog(Context ctx, String title, final CommDialogSelectListener listener) {
        dialog = new MaterialDialog(ctx);
        dialog.setTitle(title);
        View view = LayoutInflater.from(ctx).inflate(R.layout.comm_dialog_listview,null);
        dialog.setContentView(view);
        lv = (ListView) view.findViewById(R.id.comm_dialog_lv);
        this.listener = listener;
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void initList(){
        List<String> list = new ArrayList<String>();
    }

}
