package com.y.w.ywker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.y.w.ywker.utils.ActivityManager;
import com.y.w.ywker.views.EProgressDialog;

/**
 * Created by lxs on 16/5/21.
 */
public class SuperActivity extends AppCompatActivity{

    private EProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
    }

    /**
     * 显示进度对话框
     */
    public void showLoading(){
        dialog = new EProgressDialog(this);
        dialog.show();
    }

    /**
     * 消失进度对话框
     */
    public void dismissLoading(){
        if(dialog != null){
            dialog.dismiss();;
            dialog = null;
        }
    }
}
