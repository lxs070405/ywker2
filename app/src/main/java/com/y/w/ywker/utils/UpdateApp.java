//package com.y.w.ywker.utils;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Environment;
//import android.view.View;
//import android.widget.Toast;
//
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//import com.y.w.ywker.views.MaterialDialog;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
///**
// * Created by lxs on 2016/7/12.
// * 更新app工具类
// */
//public class UpdateApp {
//    private Context context;
//    private String url;
//
//    /** 下载地址 */
//    private String download_url;
//    /** 更新描述 */
//    String description;
//    /** 版本Code */
//    private String versioncode = "";
//    public UpdateApp(Context context, String url){
//        this.context = context;
//        this.url = url;
//        getNetWorkData();
//    }
//    /***
//     * 检测是否有新版本
//     */
//    private boolean checkUpdate(String version) {
//        if("".equals(version)){
//            return false;
//        }
//        int code = getVersionCode();
//        int versioncode= Integer.getInteger(version);//服务端版本号
//        if( versioncode >  code) {
////            // 校验是否有新版本
////            if (!getVersionName().equals(versionCode)) {
////                // 有新版本，弹出一升级对话框
////                return true;
////            }else {
////                Log.i("Frewen", "versionName不合格");
////            }
//            return true;
//        }else {
//            return false;
//        }
//    }
//    /**
//     * 得到应用程序的版本名称
//     */
//    private String getVersionName() {
//        // 用来管理手机的APK
//        PackageManager pm = context.getPackageManager();
//            // 得到知道APK的功能清单文件
//        PackageInfo info = null;
//        try {
//            info = pm.getPackageInfo(context.getPackageName(), 0);
//            return info.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 获得版本号
//     */
//    private int getVersionCode() {
//        // 用来管理手机的APK
//        PackageManager pm = context.getPackageManager();
//        // 得到知道APK的功能清单文件
//        PackageInfo info = null;
//        try {
//            info = pm.getPackageInfo(context.getPackageName(), 0);
//            return info.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//    public File updateDir = null;
//    public File updateFile = null;
//    /***
//     * 创建文件路径以及文件
//     * @param path 文件路径
//     * @param name 文件名
//     */
//    public void createFile(String path , String name) {
//        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
//                .getExternalStorageState())) {
//            updateDir = new File(Environment.getExternalStorageDirectory()
//                    + "/" + path);
//            updateFile = new File(updateDir + "/" + name + ".apk");
//
//            if (!updateDir.exists()) {
//                updateDir.mkdirs();
//            }
//            if (!updateFile.exists()) {
//                try {
//                    updateFile.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
//
//    /**
//     * 安装APK
//     */
//    private void installAPK(File t) {
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
//        context.startActivity(intent);
//    }
//
//    public void getNetWorkData(){
//        Request request = new Request.Builder().url(url).build();
//        HttpNetUtils.enqueue(request, new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//              String str =  Utils.getStrFromInputSteam(response.body().byteStream());
//                if(str != null){
//                    versioncode = "28";//str
////                    if (checkUpdate( versioncode)) {
////                        showUpdataDialog();
////                     }
//                }
//            }
//        });
//    }
//    public boolean isUpdate(){
//        return checkUpdate( versioncode);
//    }
//
//    public  void showUpdataDialog() {
//        dialogTip = new MaterialDialog(context)
//                .setTitle("版本更新提示")
//                .setMessage("检测到有新的版本,是否更新")
//                .setPositiveButton("确定", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        proDialog = new ProgressDialog(context);
//                        proDialog.setTitle("提示");
//                        proDialog.setMessage("正在下载中......");
//                        proDialog.show();
//                        doUpgrade();
//                    }
//                })
//                .setNegativeButton("取消",new  View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialogTip.dismiss();
//                    }
//                });
//
//    }
//
//    /**
//     * 关闭下载进度提示
//     */
//    public void dismiss(){
//        proDialog.dismiss();
//    }
//    private void doUpgrade() {
//        // 下载APK，并且替换安装
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            Request request = new Request.Builder().url(String.format(url)).build();
//            HttpNetUtils.enqueue(request, new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    proDialog.dismiss();
//                    Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    createFile("Eweic","Eweic"+versioncode);
//                    InputStream inputStream = response.body().byteStream();
//                    String path = updateFile.getAbsolutePath();
//                    OutputStream out = new FileOutputStream(updateFile);
//
//                   byte[] arr = new byte[5*1024];
//                    while (inputStream.read(arr) != -1){
//                        out.write(arr);
//                    }
//                    out.close();
//                    installAPK(updateFile);
//                }
//            });
//        }else{
//            Toast.makeText(context, "未发现sdcard，请安装上在试", Toast.LENGTH_SHORT).show();
//        }
//    }
//    private ProgressDialog proDialog;
//    private MaterialDialog dialogTip;
//
//}
