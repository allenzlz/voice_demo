package com.xiaoi.exp.voice.util;

import net.sf.json.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {
    /**
     * OkHttpClient调用客户端
     */
    private static OkHttpClient client;
    /**
     * second(秒)
     */
    private static final int CONNECT_TIMEOUT = 5;
    /**
     * second(秒)
     */
    private static final int READ_TIMEOUT = 5;

    static {
        client = new OkHttpClient.Builder().connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).readTimeout(READ_TIMEOUT
                , TimeUnit.SECONDS).build();
    }

    /**
     * 发送get请求获取结果
     *
     * @param url 访问的http地址
     * @return 返回字符串结果
     */
    public static String get(String url) {
        Request request = null;
        Response response = null;
        try {
            request = new Request.Builder().url(url).get().build();
            final Call call = client.newCall(request);
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送get请求获取字节流结果
     *
     * @param url 访问的http地址
     * @return 返回字节流结果
     */
    public static InputStream getInputStream(String url) {
        Request request = null;
        Response response = null;
        try {
            request = new Request.Builder().url(url).get().build();
            final Call call = client.newCall(request);
            response = call.execute();
            return response.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送post请求获取结果
     *
     * @param url    访问的http地址
     * @param object 提交的对象
     * @return 返回字符串结果
     */
    public static String post(String url, Object object) {
        Request request = null;
        RequestBody requestBody = null;
        Response response = null;
        try {
            requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),
                    JSONObject.fromObject(object).toString());
            request = new Request.Builder().url(url).put(requestBody).build();
            final Call call = client.newCall(request);
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送post请求获取结果
     *
     * @param url    访问的http地址
     * @param jsonObject 提交的json对象
     * @return 返回字符串结果
     */
    public static String post(String url, JSONObject jsonObject) {
        Request request = null;
        RequestBody requestBody = null;
        Response response = null;
        try {
            requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),
                    jsonObject.toString());
            request = new Request.Builder().url(url).put(requestBody).build();
            final Call call = client.newCall(request);
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(get("http://sjyh.demo.xiaoi.com/robot/ask" +
                ".action?platform=web&question=%E5%8D%A1%E5%8F%B7%E7%9A%84%E6%9F" +
                "%A5%E8%AF%A2&brand=DMT_FUNCTION_0&userId=1234"));
    }
}
