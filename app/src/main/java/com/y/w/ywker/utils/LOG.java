package com.y.w.ywker.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by lxs on 16/4/13.
 */
public class LOG {
    public static void e(Context ctx,String msg){
        Log.e(ctx.getClass().getSimpleName(),msg);
    }
    public static void d(Context ctx,String msg){
        Log.d(ctx.getClass().getSimpleName(),msg);
    }
    public static void v(Context ctx,String msg){
        Log.v(ctx.getClass().getSimpleName(),msg);
    }

}
