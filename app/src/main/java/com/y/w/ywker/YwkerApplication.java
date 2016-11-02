package com.y.w.ywker;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.baidu.location.service.LocationService;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by liupengya on 16/4/12.
 */

public class YwkerApplication extends Application{

    private static Context _context;

    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        _context = this.getApplicationContext();
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
//        SDKInitializer.initialize(getApplicationContext());
        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }

    public static Context getInstance(){
        if (_context == null){
            _context = new YwkerApplication();
        }
        return _context;
    }


}
