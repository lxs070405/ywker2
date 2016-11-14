package com.y.w.ywker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lxs on 15/12/25.
 * 离线缓存数据的管理
 */
public class OfflineDataManager {
    private SharedPreferences sh;

    private static OfflineDataManager manager;

    private Context ctx;
    public static OfflineDataManager getInstance(Context ctx){
        if (manager == null){
            manager = new OfflineDataManager(ctx);
        }
        return manager;
    }

    public OfflineDataManager(Context ctx){
        this.ctx = ctx;
        sh = ctx.getSharedPreferences("offlinedata",0);
    }

    /**
     * 是否是第一次使用
     * @param Str
     */
    public  void saveIsFristUse(boolean Str){
        sh.edit().putBoolean("IsFristUse",Str).commit();
    }
    public boolean getIsFristUse(){
        return sh.getBoolean("IsFristUse",true);
    }

    /**
     * 是否完成
     * @param Str
     */
    public  void saveIsFinish(String Str){
        sh.edit().putString("IsFinish",Str).commit();
    }
    public String getIsFinish(){
        return sh.getString("IsFinish","");
    }

    //保存User信息
    public void saveUser(String userStr){
        sh.edit().putString("userinfo",userStr).commit();
        LOG.e(ctx,userStr);
    }

    //保存User信息
    public void saveLoginName(String name){
        sh.edit().putString("loginname",name).commit();
    }
    public void removeLoginData(){
        sh.edit().putString("loginname","").commit();
        sh.edit().putString("loginpwd","").commit();
    }
    //保存User信息
    public void saveLoginPwd(String pwd){
        sh.edit().putString("loginpwd",pwd).commit();
        LOG.e(ctx, pwd);
    }
    public void saveVersionCode(String VersionCode){
        sh.edit().putString("VersionCode",VersionCode).commit();
        LOG.e(ctx, VersionCode);
    }
    public void saveAppPath(String AppPath){
        sh.edit().putString("AppPath",AppPath).commit();
        LOG.e(ctx, AppPath);
    }
    public String getAppPath(){
        return sh.getString("AppPath","");
    }
    public String getVersionCode(){
        return sh.getString("VersionCode","");
    }
    public String getLoginName(){
        return sh.getString("loginname","");
    }
    public String getLoginPwd(){
        return sh.getString("loginpwd","");
    }
    /**
     * 获取用户信息
     * @return
     */
    public String getUser(){
        return sh.getString("userinfo","");
    }

    public void saveObj(String objKey,String objValue){
        sh.edit().putString(objKey,objValue).commit();
    }

    public String getObj(String objKey){
        return sh.getString(objKey,"");
    }

    public void setMainID(String mainID){
        sh.edit().putString("mainID",mainID).commit();
    }

    public String getMainID(){
        return sh.getString("mainID","");
    }

    public String getUserID(){
        return sh.getString("userID","");
    }

    public void setUserID(String userid){
        sh.edit().putString("userID",userid).commit();
    }

    public boolean getAllDynamicOn(){
        return sh.getBoolean("alldynamic", true);
    }
    public void setAllDynamicOn(boolean isOn){
        sh.edit().putBoolean("alldynamic", isOn).commit();
    }

    public boolean getOrderDynamicOn(){
        return sh.getBoolean("orderdynamic",true);
    }
    public void setOrderDynamicOn(boolean isOn){
        sh.edit().putBoolean("orderdynamic",isOn).commit();
    }

    public boolean getOrderReplayOn(){
        return sh.getBoolean("orderreplay",true);
    }
    public void setOrderReplayOn(boolean isOn){
        sh.edit().putBoolean("orderreplay",isOn).commit();
    }

    public boolean getOrderHandleOn(){
        return sh.getBoolean("orderhandle",true);
    }
    public void setOrderHandleOn(boolean isOn){
        sh.edit().putBoolean("orderhandle",isOn).commit();
    }

    public boolean getInnerMsgOn(){
        return sh.getBoolean("innermsg",true);
    }
    public void setInnerMsgOn(boolean isOn){
        sh.edit().putBoolean("innermsg",isOn).commit();
    }

    public boolean getSystemMsgOn(){
        return sh.getBoolean("msgsystem",true);
    }

    public void setSystemMsgOn(boolean isOn){
        sh.edit().putBoolean("msgsystem",isOn).commit();
    }

}
