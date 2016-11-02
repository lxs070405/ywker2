package com.y.w.ywker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonParser;
import com.y.w.ywker.entry.YBasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lxs on 16/4/13.
 * 工具类
 */
public class Utils {

    /**
     * 打开Activity
     *
     * @param activity
     * @param cls
     * @param name
     */
    public static void start_Activity(Activity activity, Class<?> cls,
                                      YBasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        activity.startActivity(intent);
    }

    /**
     * 打开Activity
     *
     * @param activity
     * @param cls
     */
    public static void start_ActivityResult(Activity activity, Class<?> cls,int requestCode,
                                            YBasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.putExtra("requestcode", requestCode);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void showToast(Context ctx,String msg){
      
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }

    public static void start_ActivityResult(Fragment fragment, Class<?> cls,int requestCode,
                                            YBasicNameValuePair... name){
        Intent intent = new Intent();
        intent.setClass(fragment.getActivity(), cls);
        intent.putExtra("requestcode", requestCode);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        fragment.startActivityForResult(intent, requestCode);
    }
    /**
     * 判断是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity == null) {
                Log.w("Utility", "couldn't get connectivity manager");
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].isAvailable()) {
                            Log.d("Utility", "network is available");
                            return true;
                        }
                    }
                }
            }
        }
        Log.d("Utility", "network is not available");
        return false;
    }

    public static void initToolBar(AppCompatActivity activity,Toolbar toolbar){
        if (activity != null && toolbar!= null){
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("");
        }
    }

    public static int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 * var2 + 0.5F);
    }

    public static int px2dip(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 / var2 + 0.5F);
    }

    public static int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 * var2 + 0.5F);
    }

    public static int px2sp(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 / var2 + 0.5F);
    }

    /**
     * 判断是否正常的json数据
     * @param json
     * @return
     */
    public static boolean isWrongJson(String json){
        try {
            new JsonParser().parse(json);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 检测Sdcard是否存在
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String getStrFromInputSteam(InputStream in){
        BufferedReader bf= null;
        //最好在将字节流转换为字符流的时候 进行转码
        StringBuffer buffer=new StringBuffer();
        String line="";
        try {
            bf = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            while((line=bf.readLine())!=null){
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    /**
     * 判断字符串中是否包含字母
     * @param str
     * @return  true 包含 false 不包含
     */
    public static boolean isCanst(String str){
        boolean bflag = false;
        int length = str.length();
        if(length < 6||length > 12){
            return false;
        }
//        for(int i = 0 ; i < length ; i++){
//            int num = str.charAt(i);
//            if((num>='a'&&num<='z')||(num>='A'&&num<='Z'))
//            {
//                bflag = true;
//                break;
//            }
//        }
//       return bflag;
        return true;
    }
    /**
     * dp转px
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context,float dp)
    {
        return (int ) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    /**
     * 获得屏幕宽度
     * @param context
     */
    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE );
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics( outMetrics);
        return outMetrics .widthPixels ;
    }
}
