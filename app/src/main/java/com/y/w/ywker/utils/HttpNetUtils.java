package com.y.w.ywker.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by lxs on 16/4/12.
 */
public class HttpNetUtils {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static final String CHARSET_NAME = "UTF-8";

    static{
        mOkHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
    }

    /**
     * 开启同步线程
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }
    /**
     * 开启异步线程访问网络
     * @param request
     * @param responseCallback
     */
    public static Call enqueue(Request request, Callback responseCallback){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * @param request
     */
    public static void enqueue(Request request){
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Response arg0) throws IOException {

            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {

            }
        });
    }

    public static String getStringFromServer(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value){
        return url + "?" + name + "=" + value;
    }

    /**
     * 异步提交数据
     * @param url url地址
     * @param map 提交数据的键值对
     * @throws Exception
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void postData(String url,Map<String,String> map,Callback responseCallback) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        Log.e("POST",json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }
}
