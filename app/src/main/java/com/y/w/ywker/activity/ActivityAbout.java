package com.y.w.ywker.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.views.MaterialDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by lxs on 16/5/4.
 */

public class ActivityAbout extends SuperActivity {
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.about_version_code_tv)
    TextView aboutVersionCodeTv;
    @Bind(R.id.about_com_name_tv)
    TextView aboutComNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        layoutCommToolbarTitle.setText("关于");
        try {
            aboutVersionCodeTv.setText("当前版本号:" + getVersionName());
        } catch (Exception e) {

        }
    }

    @OnClick({R.id.about_btn_back})
    public void onClick(View view){
        finish();
    }

    /**
     * 获取软件版本号信息
     * @return
     * @throws Exception
     */
    private String getVersionName() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }

    MaterialDialog dialog;

    /**
     * 设置长按事件复制公众号
     * @param v
     * @return
     */
    @OnLongClick({R.id.about_weixinhao_tv})
    public boolean onMyLongClick(View v){
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText("eweicf"); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        dialog = new MaterialDialog(this)
                .setTitle("提示")
                .setMessage("公众号已复制到粘贴板")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
        return true;
    }
}
